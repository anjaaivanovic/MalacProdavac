using Microsoft.EntityFrameworkCore;

namespace back.Models
{
    [PrimaryKey(nameof(RaterId), nameof(RatedId))]
    public class Rating
    {
        public int RaterId { get; set; }
        public int RatedId { get; set; }
        public float Communication {  get; set; }
        public float Reliability { get; set; }
        public float OverallExperience { get; set; }
        public float Average {  get; set; }

    }
}
