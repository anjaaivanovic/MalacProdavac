using Microsoft.EntityFrameworkCore;

namespace back.Models
{
    [PrimaryKey(nameof(ShopId), nameof(Day))]
    public class WorkingHours
    {
        public int ShopId { get; set; }
        public DateTime Day {  get; set; }
        public DateTime OpeningHours { get; set; }
        public DateTime ClosingHours { get; set;}
    }
}
