using System.ComponentModel.DataAnnotations;

namespace back.Models
{
    public class DeliveryMethod
    {
        [Key]
        public int Id { get; set; }
        public string? Name { get; set; }
    }
}
