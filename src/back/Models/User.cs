﻿namespace back.Models
{
    public class User
    {
        public int Id { get; set; }
        public string? Username { get; set; }
        public string? Name { get; set; }
        public string? Lastname { get; set; }
        public string? Email { get; set; }
        public string? Password { get; set; }
        public string? PasswordSalt { get; set; }
        public string? Address { get; set; }
        public int RoleId { get; set; }
        public bool LoggedIn { get; set; }
        public DateTime ? CreatedOn { get; set; }
        public bool LightTheme { get; set; }
        public float Latitude { get; set; }
        public float Longitude { get; set; }
        public string? Image {  get; set; }

    }
}
