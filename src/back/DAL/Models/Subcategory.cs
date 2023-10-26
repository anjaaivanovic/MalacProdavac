﻿using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace back.Models
{
    public class Subcategory
    {
        [Key]
        public int Id { get; set; }
        public string? Name { get; set; }
        [ForeignKey("Category")]
        public int CategoryId { get; set; }

        public Category Category { get; set; }
    }
}
