using System.ComponentModel.DataAnnotations;

namespace back.Models
{
    public class Metric
    {
        [Key]
        public int Id { get; set; }
        public string? Name { get; set; }

    }
}
