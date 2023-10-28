using Microsoft.EntityFrameworkCore;

namespace back.Models
{
    [PrimaryKey(nameof(ReviewerId), nameof(ProductId))]
    public class ProductReview
    {
        public int ReviewerId { get; set; }
        public int ProductId { get; set; }
        public float Rating { get; set; }
        public string? Comment { get; set; }
        public DateTime? PostedOn { get; set; }
    }
}
