using Microsoft.EntityFrameworkCore;

namespace back.Models
{
    [PrimaryKey(nameof(ShopId), nameof(SubcategoryId))]
    public class ShopSubcategory
    {
        public int ShopId { get; set; }
        public int SubcategoryId { get; set; }
    }
}
