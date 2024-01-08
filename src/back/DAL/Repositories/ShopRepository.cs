using back.BLL.Dtos;
using back.BLL.Dtos.Cards;
using back.BLL.Dtos.HelpModels;
using back.BLL.Dtos.Infos;
using back.BLL.Services;
using back.DAL.Contexts;
using back.Models;
using Microsoft.EntityFrameworkCore;

namespace back.DAL.Repositories
{
    public class ShopRepository : IShopRepository
    {
        Context _context;
        int numberOfItems = 10;
        int numberOfReviews = 3;

        public ShopRepository(Context context)
        {
            _context = context;
        }

        #region filterHelp
        public List<ShopCard> SortShops(int sort, List<ShopCard> shops)
        {
            switch (sort)
            {
                case 1:
                    return shops.OrderBy(x => x.Rating).ToList();
                case 2:
                    return shops.OrderByDescending(x => x.Rating).ToList();
                case 3:
                    return shops.OrderBy(x => x.Name).ToList();
                case 4:
                    return shops.OrderByDescending(x => x.Name).ToList();
                default:
                    return shops.OrderBy(x => x.Id).ToList();
            }
        }

        public static double CalculateDistance(float lat1, float lon1, float lat2, float lon2)
        {
            const double radiusOfEarth = 6371;

            // degrees -> radians
            double dLat = Math.PI / 180.0 * (lat2 - lat1);
            double dLon = Math.PI / 180.0 * (lon2 - lon1);

            // Haversine formula
            double a = Math.Sin(dLat / 2) * Math.Sin(dLat / 2) +
                Math.Cos(Math.PI / 180.0 * lat1) * Math.Cos(Math.PI / 180.0 * lat2) *
                Math.Sin(dLon / 2) * Math.Sin(dLon / 2);
            double c = 2 * Math.Atan2(Math.Sqrt(a), Math.Sqrt(1 - a));

            double distance = radiusOfEarth * c;

            return distance;
        }

        #endregion

        public async Task<List<ShopCard>> GetUnsortedShops(int? userId, List<int>? categories, int? rating, bool? open, int? range, string? location, string? search, bool? favorite, float? currLat, float? currLong)
        {
            if (categories == null || categories.Count == 0) categories = await _context.Categories.Select(x => x.Id).ToListAsync();
            if (search == null) search = "";
            if (rating == null) rating = 0;

            List<ShopCard>  shops = await _context.Shop
                    .Where(x => x.Name.ToLower().Contains(search.Trim().ToLower()) && x.OwnerId != userId)
                    .GroupJoin(_context.ShopReviews.GroupBy(x => x.ShopId).Select(group => new
                    {
                        ShopId = group.Key,
                        AvgRating = group.Average(x => x.Rating)
                    }), s => s.Id, sr => sr.ShopId, (s, sr) => new ShopCard
                    {
                        Id = s.Id,
                        Name = s.Name,
                        Address = s.Address,
                        Image = s.Image,
                        WorkingHours = _context.WorkingHours.Where(x => x.ShopId == s.Id).ToList(),
                        Liked = _context.LikedShops.Any(x => x.ShopId == s.Id && x.UserId == userId),
                        Rating = sr.DefaultIfEmpty().Select(x => x.AvgRating).FirstOrDefault(),
                        Latitude = (float)s.Latitude,
                        Longitude = (float)s.Longitude
                    })
                .Where(x => x.Rating >= rating)
                .Join(_context.ShopCategories.Where(x => categories.Contains(x.CategoryId)), s => s.Id, sr => sr.ShopId, (s, sr) => s)
                .GroupBy(x => x.Id).Select(x => x.First())
                .ToListAsync();
            
            if (favorite != null && favorite == true)
            {
                shops = shops.Join(_context.LikedShops.Where(x => x.UserId == userId), s => s.Id, ls => ls.ShopId, (s, ls) => s).ToList();
            }

            if (open != null && open == true)
            {
                shops = shops
                        .Join(_context.WorkingHours, s => s.Id, w => w.ShopId, (s, w) => new { s , w })
                        .ToList()
                        .Where(x => x.w.Day == DateTime.Now.DayOfWeek && x.w.OpeningHours <= DateTime.Now.TimeOfDay && x.w.ClosingHours >= DateTime.Now.TimeOfDay)
                        .Select(x => x.s)
                        .ToList();
            }

            if (location != null && location.Trim().Length > 0)
            {
                shops = shops.Where(x => x.Address.Trim().ToLower().Contains(location.Trim().ToLower())).ToList();
            }
            else if (range != null && range > 0)
            {
                shops = shops.Where(x => CalculateDistance((float)x.Latitude, (float)x.Longitude, (float)currLat, (float)currLong) <= range).ToList();
            }

            return shops;
        }

        public async Task<List<ShopCard>> GetShops(int? userId, List<int>? categories, int? rating, bool? open, int? range, string? location, int sort, string? search, int page, bool? favorite, float? currLat, float? currLong)
        {
            List<ShopCard> shops = await GetUnsortedShops(userId, categories, rating, open, range, location, search, favorite, currLat, currLong);
            shops = SortShops(sort, shops);
            return shops.Skip((page - 1) * numberOfItems).Take(numberOfItems).ToList();
        }

        public async Task<int> ShopPages(int? userId, List<int>? categories, int? rating, bool? open, int? range, string? location, string? search,  bool? favorite, float? currLat, float? currLong)
        {
            return (int)Math.Ceiling((double)(await GetUnsortedShops(userId, categories, rating, open, range, location, search, favorite, currLat, currLong)).Count()/numberOfItems);
        }
        
        public async Task<ShopInfo> ShopDetails(int shopId, int userId)
        {
            Shop shop = await _context.Shop.FirstOrDefaultAsync(x => x.Id == shopId);
            if (shop == null) return null;
            
            List<string> categories = await _context.ShopCategories.Where(x => x.ShopId == shopId).Join(_context.Categories, sc => sc.CategoryId, c => c.Id, (sc, c) => c).Select(x => x.Name).ToListAsync();
            List<string> subcategories = await _context.ShopSubcategories.Where(x => x.ShopId == shopId).Join(_context.Subcategories, sc => sc.SubcategoryId, c => c.Id, (sc, c) => c).Select(x => x.Name).ToListAsync();
            List<WorkingHours> workingHours = await _context.WorkingHours.Where(x => x.ShopId == shopId).Select(wh => new WorkingHours
                                {
                                    Day = wh.Day,
                                    OpeningHours = wh.OpeningHours,
                                    ClosingHours = wh.ClosingHours,
                                    Shop = null
                                }).ToListAsync();

            float average = 0;
            if (_context.ShopReviews.Where(x => x.ShopId == shopId).Count() > 0) average = _context.ShopReviews.Where(x => x.ShopId == shopId).Select(x => x.Rating).Average();

            return new ShopInfo
            {
                Id = shop.Id,
                Name = shop.Name,
                Address = shop.Address,
                Image = shop.Image,
                Rating = average,
                Liked = _context.LikedShops.Any(x => x.ShopId == shopId && x.UserId ==  userId),
                BoughtFrom = _context.Orders.Join(_context.OrderItems, o => o.Id, oi => oi.OrderId, (o, oi) => new { o, oi })
                            .Join(_context.Products, order => order.oi.ProductId, p => p.Id, (order, p) => new { order, p })
                            .Any(x => x.p.ShopId == shopId  && x.order.o.UserId == userId),
                Rated = _context.ShopReviews.Any(x => x.ShopId == shopId && x.ReviewerId == userId),
                IsOwner = shop.OwnerId == userId,
                OwnerId = shop.OwnerId,
                Categories = categories,
                Subcategories = subcategories,
                WorkingHours = workingHours,
                PIB = shop.PIB,
                ProductDisplayId = await _context.ProductDisplays.FirstOrDefaultAsync(x => x.ShopId == shop.Id) != null ? (await _context.ProductDisplays.FirstOrDefaultAsync(x => x.ShopId == shop.Id)).Id : 0
            };
        }

        public async Task<List<ShopReviewExtended>> GetShopReviews(int shopId, int page)
        {
            if (page == 0)
            {
                return await _context.ShopReviews.Where(x => x.ShopId == shopId).Select(x => new ShopReviewExtended
                {
                    ReviewerId = x.ReviewerId,
                    ShopId = x.ShopId,
                    Rating = x.Rating,
                    Comment = x.Comment,
                    PostedOn = x.PostedOn,
                    Username = _context.Users.FirstOrDefault(u => u.Id == x.ReviewerId).Username,
                    Image = _context.Users.FirstOrDefault(i => i.Id == x.ReviewerId).Image,
                    Shop = null
                }).Take(1).ToListAsync();
            }

            return await _context.ShopReviews.Where(x => x.ShopId == shopId).Select(x => new ShopReviewExtended
            {
                ReviewerId = x.ReviewerId,
                ShopId = x.ShopId,
                Rating = x.Rating,
                Comment = x.Comment,
                PostedOn = x.PostedOn,
                Username = _context.Users.FirstOrDefault(u => u.Id == x.ReviewerId).Username,
                Image = _context.Users.FirstOrDefault(i => i.Id == x.ReviewerId).Image,
                Shop = null
            }).Skip((page - 1) * numberOfReviews).Take(numberOfReviews).ToListAsync();
        }

        #region likes
        public async Task<LikedShops> GetLike(int shopId, int userId)
        {
            return await _context.LikedShops.FirstOrDefaultAsync(x => x.ShopId == shopId && x.UserId == userId);
        }

        public async Task<bool> LikeShop(int shopId, int userId)
        {
            await _context.LikedShops.AddAsync(new LikedShops { ShopId = shopId, UserId = userId });
            return await _context.SaveChangesAsync() > 0;
        }

        public async Task<bool> DislikeShop(int shopId, int userId)
        {
            _context.LikedShops.Remove(await GetLike(shopId, userId));
            return await _context.SaveChangesAsync() > 0;
        }
        #endregion

        public async Task<bool> LeaveReview(ReviewDto review)
        {
            _context.ShopReviews.AddAsync(new ShopReview
            {
                ReviewerId = review.UserId,
                ShopId = review.Id,
                Rating = review.Rating,
                Comment = review.Comment,
                PostedOn = DateTime.Now
            });

            return _context.SaveChanges() > 0;
        }

        public async Task<bool> ChangeShopPhoto(int id, string path)
        {
            Shop s = await _context.Shop.FirstOrDefaultAsync(x => x.Id == id);
            s.Image = path;
            return await _context.SaveChangesAsync() > 0;
        }

        public async Task<bool> InsertShop(Shop shop)
        {
            await _context.Shop.AddAsync(shop);
            return await _context.SaveChangesAsync() > 0;
        }

        public async Task<bool> InsertWorkingHours(List<WorkingHoursDto> workingHours, int shopId)
        {
            foreach (var wh in workingHours) await _context.WorkingHours.AddAsync(new WorkingHours {  Day = wh.Day, OpeningHours = TimeSpan.Parse(wh.OpeningHours), ClosingHours = TimeSpan.Parse(wh.ClosingHours), ShopId = shopId});
            return await _context.SaveChangesAsync() > 0;
        }

        public async Task<bool> InsertShopCategories(List<ShopCategory> shopCategories, int shopId)
        {
            await _context.ShopCategories.AddRangeAsync(shopCategories);
            return await _context.SaveChangesAsync() > 0;
        }

        public async Task<bool> EditShop(EditShopDto shop)
        {
            Shop s = await _context.Shop.FirstOrDefaultAsync(x => x.Id == shop.Id);
            if ((shop.PIB == null && shop.Address == null && shop.Name == null && shop.AccountNumber == null) || (shop.PIB == s.PIB && shop.Address == s.Address && shop.Name == s.Name && shop.AccountNumber == s.AccountNumber)) return true;

            if (shop.AccountNumber != null) s.AccountNumber = shop.AccountNumber;
            if (shop.PIB != null) s.PIB = (int)shop.PIB;
            if (shop.Address != null)
            {
                (double, double) coords = await HelperService.GetCoordinates(shop.Address);
                s.Latitude = (float)coords.Item1;
                s.Longitude = (float)coords.Item2;
            }
            if (shop.Name != null) s.Name = shop.Name;

            return await _context.SaveChangesAsync() > 0;
        }

        public async Task<bool> EditWorkingHours(List<WorkingHoursDto> workingHours, int shopId)
        {
            var current = await _context.WorkingHours.Where(x => x.ShopId == shopId).ToListAsync();
            var toDelete = current.Where(x => !workingHours.Select(x => x.Day).Contains(x.Day));
            if (toDelete.Count() > 0)
            {
                _context.WorkingHours.RemoveRange(toDelete);
                if (!(await _context.SaveChangesAsync() > 0)) return false;
            }

            int ind = 0;
            foreach (var wh in workingHours)
            {
                WorkingHours curr = current.FirstOrDefault(x => x.Day == wh.Day);
                if (curr == null)
                {
                    await _context.WorkingHours.AddAsync(new WorkingHours { Day = wh.Day, OpeningHours = TimeSpan.Parse(wh.OpeningHours), ClosingHours = TimeSpan.Parse(wh.ClosingHours), ShopId = shopId });
                }
                else
                {
                    if (curr.ClosingHours == TimeSpan.Parse(wh.ClosingHours) && curr.OpeningHours == TimeSpan.Parse(wh.OpeningHours)) ind++;
                    else
                    {
                        curr.OpeningHours = TimeSpan.Parse(wh.OpeningHours);
                        curr.ClosingHours = TimeSpan.Parse(wh.ClosingHours);
                    }
                }
            }

            if (ind == workingHours.Count()) return true;

            return await _context.SaveChangesAsync() > 0;
        }

        public async Task<List<ShopCategory>> GetShopCategories(int shopId)
        {
            return await _context.ShopCategories.Where(x => x.ShopId == shopId).ToListAsync();
        }

        public async Task<bool> EditShopCategories(int shopId, List<ShopCategory> categories)
        {
            List<ShopCategory> existing = await GetShopCategories(shopId);
            List<ShopCategory> newCategories = categories.Where(x => !existing.Contains(x)).ToList();
            List<ShopCategory> toDelete = existing.Where(x => !categories.Contains(x)).ToList();

            if (toDelete.Count() == 0 && newCategories.Count() == 0) return true;

            if (toDelete.Count > 0)
            {
                _context.ShopCategories.RemoveRange(toDelete);
                if (await _context.SaveChangesAsync() <= 0) return false;
            }
            if (newCategories.Count() > 0)
            {
                _context.ShopCategories.AddRange(newCategories);
                if (await _context.SaveChangesAsync() <= 0) return false;
            }

            return true;
        }

        public async Task<bool> DeleteShop(int shopId)
        {
            _context.Shop.Remove(await _context.Shop.FirstOrDefaultAsync(x => x.Id == shopId));
            return await _context.SaveChangesAsync() > 0;
        }

        public async Task<Shop> GetShop(int shopId)
        {
            return (await _context.Shop.FirstOrDefaultAsync(x => x.Id == shopId));
        }

        public async Task<bool> InsertProductDisplay(ProductDisplay productDisplay)
        {
            await _context.ProductDisplays.AddAsync(productDisplay);
            return await _context.SaveChangesAsync() > 0;
        }

        public async Task<List<int>> GetNearbyUsers(float latitude, float longitude, int ownerId)
        {
            int range = 10;
            return (await _context.Users.Select(x => new {id = x.Id, lat = x.LatestLatitude, lon = x.LatestLongitude }).ToListAsync()).Where(x => x.id != ownerId && CalculateDistance(x.lat, x.lon, latitude, longitude) <= range).Select(x => x.id).ToList();
        }

        public async Task<bool> EditProductDisplay(EditProductDisplayDto productDisplayDto)
        {
            ProductDisplay pd = await _context.ProductDisplays.FirstOrDefaultAsync(x => x.Id == productDisplayDto.Id);

            if (productDisplayDto.StartDate != null) pd.StartDate = (DateTime)productDisplayDto.StartDate;
            if (productDisplayDto.StartTime != null) pd.StartTime = TimeSpan.Parse(productDisplayDto.StartTime);
            if (productDisplayDto.EndDate != null) pd.EndDate = (DateTime)productDisplayDto.EndDate;
            if (productDisplayDto.EndTime != null) pd.EndTime = TimeSpan.Parse(productDisplayDto.EndTime);
            if (productDisplayDto.Address != null)
            {
                (double, double) coords = await HelperService.GetCoordinates(productDisplayDto.Address);
                pd.Address = productDisplayDto.Address;
                pd.Latitude = (float)coords.Item1;
                pd.Longitude = (float)coords.Item2;
            }

            return await _context.SaveChangesAsync() > 0;
        }

        public async Task<ProductDisplayInfo> GetProductDisplay(int id)
        {
            return await _context.ProductDisplays.Select(x => new ProductDisplayInfo
            {
                Address = x.Address,
                EndDate = x.EndDate.ToShortDateString(),
                EndTime = x.EndTime,
                Id = x.Id,
                Latitude = x.Latitude,
                Longitude = x.Longitude,
                ShopId = x.ShopId,
                StartDate = x.StartDate.ToShortDateString(),
                StartTime = x.StartTime
            }).FirstOrDefaultAsync(x => x.Id == id);
        }

        public async Task<bool> DeleteProductDisplay(int id)
        {
            ProductDisplay pd = await _context.ProductDisplays.FirstOrDefaultAsync(x => x.Id == id);
            _context.ProductDisplays.Remove(pd);

            return await _context.SaveChangesAsync() > 0;
        }

        public async Task<Shop> GetShopByUserId(int userId)
        {
            return await _context.Shop.FirstOrDefaultAsync(x => x.OwnerId == userId);
        }

        public async Task<List<Shop>> GetAllShops()
        {
            return await _context.Shop.ToListAsync();
        }

        public async Task<List<LikedShops>> GetFollowerIds(int shopId)
        {
            return await _context.LikedShops.Where(x => x.ShopId == shopId).ToListAsync();
        }

        public async Task<int> GetOwnerId(int shopId)
        {
            return (await _context.Shop.FirstOrDefaultAsync(x => x.Id == shopId)).OwnerId;
        }

        public async Task<List<ShopCheckoutCard>> GetShopsForCheckout(List<int> shopIds)
        {
            return await _context.Shop.Where(s => shopIds.Contains(s.Id)). Select(s => new ShopCheckoutCard 
                {
                    Id = s.Id,
                    Name = s.Name,
                    Address = s.Address,
                    Image = s.Image,
                    WorkingHours = _context.WorkingHours.Where(x => x.ShopId == s.Id).ToList(),
                    Latitude = s.Latitude,
                    Longitude = s.Longitude
                })
                .ToListAsync();
        }
    }
}
