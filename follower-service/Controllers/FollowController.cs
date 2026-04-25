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

        [HttpPost("{followedId:long}")]
        public async Task<IActionResult> Follow(long followedId)
        {
            var userIdClaim = User.FindFirst("id")?.Value;

            if (string.IsNullOrWhiteSpace(userIdClaim) || !long.TryParse(userIdClaim, out var followerId))
            {
                return Unauthorized(new { message = "ID korisnika nije pronadjen u tokenu ili nije ispravan." });
            }

            await _followService.FollowAsync(followerId, followedId);

            return Ok(new
            {
                message = "Uspesno pracenje korisnika.",
                followerId,
                followedId
            });
        }

        [HttpDelete("{followedId:long}")]
        public async Task<IActionResult> Unfollow(long followedId)
        {
            var userIdClaim = User.FindFirst("id")?.Value;

            if (string.IsNullOrWhiteSpace(userIdClaim) || !long.TryParse(userIdClaim, out var followerId))
            {
                return Unauthorized(new { message = "ID korisnika nije pronadjen u tokenu ili nije ispravan." });
            }

            await _followService.UnfollowAsync(followerId, followedId);

            return Ok(new
            {
                message = "Uspesno ste otpratili korisnika.",
                followerId,
                followedId
            });
        }

        [HttpPost("users")]
        [AllowAnonymous]
        public async Task<IActionResult> CreateUserNode([FromBody] CreateUserNodeDto request)
        {
            await _followService.CreateUserNodeAsync(request.UserId, request.Username);

            return Ok(new
            {
                message = "User node created successfully.",
                userId = request.UserId,
                username = request.Username
            });
        }

        

        [HttpGet("{userId:long}/following")]
        [AllowAnonymous]
        public async Task<IActionResult> GetFollowing(long userId)
        {
            var result = await _followService.GetFollowedUsersAsync(userId);
            return Ok(result);
        }

        [HttpGet("{userId:long}/followers")]
        [AllowAnonymous]
        public async Task<IActionResult> GetFollowers(long userId)
        {
            var result = await _followService.GetFollowersAsync(userId);
            return Ok(result);
        }

        [HttpGet("{userId:long}/following/count")]
        [AllowAnonymous]
        public async Task<IActionResult> GetFollowingCount(long userId)
        {
            var count = await _followService.GetFollowingCountAsync(userId);
            return Ok(new { count });
        }

        [HttpGet("{userId:long}/followers/count")]
        [AllowAnonymous]
        public async Task<IActionResult> GetFollowersCount(long userId)
        {
            var count = await _followService.GetFollowersCountAsync(userId);
            return Ok(new { count });
        }

        [HttpGet("recommendations/{userId:long}")]
        [AllowAnonymous]
        public async Task<IActionResult> GetFollowRecommendations(long userId)
        {
            var recommendations = await _followService.GetFollowRecommendationsAsync(userId);
            return Ok(recommendations);
        }
    }
}