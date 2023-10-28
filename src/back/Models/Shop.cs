using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace back.Models
{
    public class Shop
    {
        [Key]
        public int Id { get; set; }
        [ForeignKey("Owner")]
        public int OwnerId { get; set; }
        public string? Name { get; set; }
        public string? Address { get; set; }
        public float? Latitude { get; set; }
        public float? Longitude { get; set; }
        public string? Image {  get; set; }

        public User Owner { get; set; }
    }
}
