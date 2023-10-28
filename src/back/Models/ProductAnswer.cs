using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace back.Models
{
    public class ProductAnswer
    {
        [Key]
        public int Id { get; set; }
        [ForeignKey("Poster")]
        public int PosterId { get; set; }
        [ForeignKey("Question")]
        public int QuestionId { get; set; }
        public string? AnswerText { get; set; }
        public DateTime PostedOn { get; set; }

        public User Poster { get; set; }
        public ProductQuestion Question { get; set; }
    }
}
