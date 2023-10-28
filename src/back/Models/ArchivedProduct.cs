using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace back.Models
{
    public class ArchivedProduct
    {
        [Key]
        public int Id { get; set; }
        [ForeignKey("Shop")]
        public int ShopId { get; set; }
        public string? Name { get; set; }
        [ForeignKey("Metric")]
        public int MetricId { get; set; }

        public Shop Shop { get; set; }
        public Metric Metric { get; set; }
    }
}
