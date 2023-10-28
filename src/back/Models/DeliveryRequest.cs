using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace back.Models
{
    public class DeliveryRequest
    {
        [Key]
        public int Id { get; set; }
        [ForeignKey("Route")]
        public int? RouteId { get; set; }
        [ForeignKey("Shop")]
        public int ShopId { get; set; }
        [ForeignKey("Order")]
        public int OrderId { get; set; }
        public DateTime? PickupDate { get; set; }
        public DateTime CreatedOn { get; set; }

        public DeliveryRoute Route { get; set; }
        public Shop Shop { get; set; }
        public Order Order { get; set; }
    }
}
