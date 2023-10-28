using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace back.Models
{
    public class Notification
    {
        [Key]
        public int Id { get; set; }
        [ForeignKey("User")]
        public int UserId { get; set; }
        public string? Title { get; set; }
        public string? Text { get; set; }
        public int TypeId { get; set; }
        public DateTime? CreatedOn { get; set; }

        public User User { get; set; }
    }
}
