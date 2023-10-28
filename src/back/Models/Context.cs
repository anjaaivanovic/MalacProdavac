using Microsoft.EntityFrameworkCore;
using Microsoft.Data.Sqlite;

namespace back.Models
{
    public class Context : DbContext
    {
        public Context(DbContextOptions<Context> options)
        : base(options)
        {
        }

        public DbSet<ArchivedProduct> ArchivedProducts { get; set; }
        public DbSet<Card> Cards { get; set; }
        public DbSet<Cart> Carts { get; set; }
        public DbSet<Category> Categories{ get; set; }
        public DbSet<ChosenCategory> ChosenCategories { get; set; }
        public DbSet<DeliveryMethod> DeliveryMethods { get; set; }
        public DbSet<DeliveryRequest> DeliveryRequests { get; set; }
        public DbSet<DeliveryRoute> DeliveryRoutes { get; set; }
        public DbSet<LikedProducts> LikedProducts { get; set; }
        public DbSet<LikedShops> LikedShops { get; set; }
        public DbSet<Metric> Metrics { get; set; }
        public DbSet<Notification> Notifications { get; set; }
        public DbSet<NotificationType> NotificationTypes { get; set; }
        public DbSet<Order> Orders { get; set; }
        public DbSet<OrderItem> OrderItems { get; set; }
        public DbSet<OrderStatus> OrderStatuses { get; set; }
        public DbSet<PaymentMethod> PaymentMethods { get; set; }
        public DbSet<Product> Products { get; set; }
        public DbSet<ProductAnswer> ProductAnswers { get; set; }
        public DbSet<ProductDisplay> ProductDisplays { get; set; }
        public DbSet<ProductImage> ProductImages { get; set; }
        public DbSet<ProductQuestion> ProductQuestions { get; set; }
        public DbSet<ProductReview> ProductReviews { get; set; }
        public DbSet<Rating> Ratings { get; set; }
        public DbSet<Role> Roles { get; set; }
        public DbSet<Shop> Shop { get; set; }
        public DbSet<ShopCategory> ShopCategories { get; set; }
        public DbSet<ShopReview> ShopReviews { get; set; }
        public DbSet<ShopSubcategory> ShopSubcategories { get; set; }
        public DbSet<Subcategory> Subcategories{ get; set; }
        public DbSet<User> Users { get; set; }
        public DbSet<WorkingHours> WorkingHours { get; set; }

        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
       => optionsBuilder.UseSqlite("Data Source=database.db");


        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            
        }
    }
}
