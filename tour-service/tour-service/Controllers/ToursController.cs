using Microsoft.AspNetCore.Mvc;
using tour_service.Models;

namespace tour_service.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class ToursController : ControllerBase
    {
        private static List<Tour> tours = new List<Tour>();

        [HttpGet]
        public ActionResult<List<Tour>> GetAll()
        {
            return Ok(tours);
        }

        [HttpPost]
        public ActionResult<Tour> Create(Tour tour)
        {
            tour.Id = tours.Count + 1;
            tour.CreatedAt = DateTime.Now;
            tour.Status = "Draft";
            tour.Price = 0;

            tours.Add(tour);

            return Ok(tour);
        }
    }
}