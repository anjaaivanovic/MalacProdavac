﻿using back.BLL.Dtos;
using back.BLL.Dtos.Cards;

namespace back.BLL.Services
{
    public interface IDeliveryService
    {
        public Task<bool> InsertDeliveryRequest(DeliveryRequestDto req);
        public Task<bool> InsertDeliveryRoute(DeliveryRouteDto route);
        public Task<bool> AddToRoute(int requestId, int routeId);
        public Task<List<DeliveryRequestCard>> GetRequestsForDeliveryPerson(int deliveryPerson);
        public Task<List<DeliveryRequestCard>> GetRequestsForShop(int userId);
        public Task<List<DeliveryRouteCard>> GetRoutesForRequest(int userId, int requestId);
        public Task<List<DeliveryPersonCard>> GetDeliveryPeopleForRequest(int requestId);
    }
}