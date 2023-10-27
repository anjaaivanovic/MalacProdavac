﻿using back.BLL.Dtos;
using back.DAL.Repositories;
using back.Models;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System.Security.Cryptography;
using System.Text;
using System.Text.RegularExpressions;

namespace back.BLL.Services
{
    public class AuthService : IAuthService
    {
        IAuthRepository _authRepository;
        public AuthService(IAuthRepository authRepository)
        {
            _authRepository = authRepository;
        }

        public string defaultImagePath = Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "..\\..\\..\\images\\default.png");

        #region registrationHelperMethods
        public string CreateUsername(string name, string lastname)
        {
            string username = name.ToLower() + "." + lastname.ToLower();
            int count = _authRepository.CountUsers(username);
            if (count > 0) username += count;

            return username;
        }

        public string CheckEmail(string email)
        {
            string pattern = @"^([\w\.\-]+)@([\w\-]+)((\.(\w){2,3})+)$";
            Regex regex = new Regex(pattern);

            if (!regex.IsMatch(email)) return "Invalid email format.";
            if (_authRepository.SameEmail(email)) return "Email address already registered.";

            return "";
        }

        public string CheckPassword(string password)
        {
            string pattern = @"^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*\W).{8,}$";

            Regex regex = new Regex(pattern);

            if (password.Length < 8) return "Password must be at least 8 characters long.";
            if (!regex.IsMatch(password)) return "Password must contain at least one lowercase and one uppercase letter, a digit and a special character.";

            return "";
        }

        public bool CheckAddress(string address)
        {
            string pattern = @"^[^,]+, [^,]+, [^,]+$";

            Regex regex = new Regex(pattern);

            if (regex.IsMatch(address)) return true;

            return false;
        }

        public async Task<(double, double)> GetCoordinates(string address)
        {
            var bingMapsApiKey = "Aj_nYJhXf_C_QoPf7gOQch6KOhTJo2iX2VIyvOlwb7hDpGCtS8rOhyQYp5kAbR54";

            var urlBuilder = new UriBuilder("http://dev.virtualearth.net/REST/v1/Locations");
            urlBuilder.Query = $"q={Uri.EscapeDataString(address)}&key={bingMapsApiKey}";
            var url = urlBuilder.ToString();

            HttpClient _httpClient = new HttpClient();

            var response = await _httpClient.GetAsync(url);
            var responseString = await response.Content.ReadAsStringAsync();

            var data = JObject.Parse(responseString);
            var latitude = data["resourceSets"][0]["resources"][0]["point"]["coordinates"][0].Value<double>();
            var longitude = data["resourceSets"][0]["resources"][0]["point"]["coordinates"][1].Value<double>();


            return (latitude, longitude);
        }
        #endregion

        public async Task<int> Register(UserDto userDto)
        {
            #region validation
            string checkEmail = CheckEmail(userDto.Email.ToLower());
            string checkPassword = CheckPassword(userDto.Password);

            if (!checkEmail.Equals("")) throw new ArgumentException(checkEmail);
            if (!checkPassword.Equals("")) throw new ArgumentException(checkPassword);
            if (!CheckAddress(userDto.Address)) throw new ArgumentException("Invalid form. \nRequired form: Street, City, Country");
            #endregion

            User user = new User();

            user.Name = userDto.Name;
            user.Lastname = userDto.Lastname;
            user.Username = CreateUsername(user.Name, user.Lastname);
            user.Email = userDto.Email.ToLower();
            user.Address = userDto.Address;
            user.RoleId = userDto.RoleId;
            user.LoggedIn = true;
            user.LightTheme = true;
            user.CreatedOn = DateTime.Now;

            byte[] passwordHash;
            byte[] passwordSalt;

            using (HMACSHA512 hmac = new HMACSHA512())
            {
                passwordSalt = hmac.Key;
                passwordHash = hmac.ComputeHash(Encoding.UTF8.GetBytes(userDto.Password));
            }

            user.Password = passwordHash;
            user.PasswordSalt = passwordSalt;

            if (File.Exists(defaultImagePath))
            {

                using (var stream = new FileStream(defaultImagePath, FileMode.Open))
                {
                    var bytes = new byte[stream.Length];
                    await stream.ReadAsync(bytes, 0, (int)stream.Length);

                    user.Image = Convert.ToBase64String(bytes);
                }
            }

            var coordinates = await GetCoordinates(userDto.Address);
            user.Latitude = (float)coordinates.Item1;
            user.Longitude = (float)coordinates.Item2;

            if (await _authRepository.InsertUser(user)) return user.Id;
            return -1;
        }

        public async Task<int> Login(LoginDto loginDto)
        {
            User user = await _authRepository.GetUser(loginDto.username);
            if (user == null) throw new ArgumentException("Invalid username.");

            using (var hmac = new HMACSHA512(user.PasswordSalt))
            {
                var hashPass = hmac.ComputeHash(Encoding.UTF8.GetBytes(loginDto.password));

                if (!hashPass.SequenceEqual(user.Password)) throw new ArgumentException("Invalid password");
            }

            return user.Id;
        }
    }
}