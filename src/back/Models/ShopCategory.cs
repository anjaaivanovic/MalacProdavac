using Microsoft.EntityFrameworkCore;

namespace back.Models
{
    [PrimaryKey(nameof(ShopId), nameof(CategoryId))]
    public class ShopCategory
    {
        public int ShopId { get; set; }
        public int CategoryId { get; set; }
    }
}
