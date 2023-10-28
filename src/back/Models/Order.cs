using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace back.Models
{
    public class Order
    {
        [Key]
        public int Id { get; set; }
        [ForeignKey("User")]
        public int UserId { get; set; }
        public DateTime CreatedOn { get; set; }
        public int PaymentMethod { get; set; }
        public int DeliveryMethod { get; set; }
        public int Status { get; set; }

        public User User { get; set; }
    }
}
