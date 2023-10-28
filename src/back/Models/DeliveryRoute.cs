using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace back.Models
{
    public class DeliveryRoute
    {
        [Key]
        public int Id { get; set; }
        [ForeignKey("DeliveryPerson")]
        public int DeliveryPersonId { get; set; }
        public DateTime StartDate { get; set; }
        public DateTime StartTime { get; set; }

        public User DeliveryPerson { get; set; }
    }
}
