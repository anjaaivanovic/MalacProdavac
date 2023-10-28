using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace back.Models
{
    public class ProductQuestion
    {
        [Key]
        public int Id { get; set; }
        [ForeignKey("Poster")]
        public int PosterId { get; set; }
        [ForeignKey("Product")]
        public int ProductId { get; set; }
        public string? QuestionText { get; set; }
        public DateTime PostedOn { get; set; }

        public User Poster { get; set; }
        public Product Product { get; set; }
    }
}
