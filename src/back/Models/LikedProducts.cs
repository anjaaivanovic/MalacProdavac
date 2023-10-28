using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using Microsoft.EntityFrameworkCore;

namespace back.Models
{
    [PrimaryKey(nameof(ProductId), nameof(UserId))]
    public class LikedProducts
    {
        public int ProductId { get; set; }

        public int UserId { get; set; }
    }
}
