using follower_service.Dtos;
using follower_service.Services;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace follower_service.Controllers
{
    [ApiController]
    [Route("api/follows")]
    [Authorize]
    public class FollowController : ControllerBase
    {
        private readonly FollowService _followService;

        public FollowController(FollowService followService)
        {
            _followService = followService;
        }

        [HttpPost]
        public async Task<IActionResult> Follow([FromBody] FollowRequestDto request)
        {
            var userIdClaim = User.FindFirst("id")?.Value;

            if (string.IsNullOrWhiteSpace(userIdClaim) || !long.TryParse(userIdClaim, out var followerId))
            {
                return Unauthorized(new { message = "ID korisnika nije pronadjen u tokenu ili nije ispravan." });
            }

            await _followService.FollowAsync(followerId, request.FollowedId);

            return Ok(new
            {
                message = "Uspesno pracenje korisnika.",
                followerId,
                followedId = request.FollowedId
            });
        }

        [HttpGet]
        public async Task<IActionResult> GetFollowing()
        {
            var userIdClaim = User.FindFirst("id")?.Value;

            if (string.IsNullOrWhiteSpace(userIdClaim) || !long.TryParse(userIdClaim, out var followerId))
            {
                return Unauthorized(new { message = "ID korisnika nije pronadjen u tokenu ili nije ispravan." });
            }

            var followedUsers = await _followService.GetFollowedUsersAsync(followerId);

            return Ok(followedUsers);
        }
    }
}