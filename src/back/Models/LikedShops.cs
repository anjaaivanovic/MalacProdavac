using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using Microsoft.EntityFrameworkCore;

namespace back.Models
{
    [PrimaryKey(nameof(ShopId), nameof(UserId))]
    public class LikedShops
    {
        public int ShopId { get; set; }
        public int UserId { get; set; }
    }
}
