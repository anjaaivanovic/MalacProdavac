﻿namespace back.Models
{
    public class Card
    {
        public int Id { get; set; }
        public string? Fullname { get; set; }
        public string? CardNumber { get; set; }
        public DateTime ExpirationDate { get; set; }
        public int CVV { get; set; }
    }
}
