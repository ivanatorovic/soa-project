using follower_service.Repositories;
using follower_service.Dtos;
using System.Linq;

namespace follower_service.Services
{
    public class FollowService
    {
        private readonly FollowRepository _followRepository;

        public FollowService(FollowRepository followRepository)
        {
            _followRepository = followRepository;
        }

        public async Task CreateUserNodeAsync(long userId, string username)
        {
            if (userId <= 0)
            {
                throw new InvalidOperationException("UserId mora biti veci od nule.");
            }

            if (string.IsNullOrWhiteSpace(username))
            {
                throw new InvalidOperationException("Username je obavezan.");
            }

            var exists = await _followRepository.UserNodeExistsAsync(userId);

            if (exists)
            {
                throw new InvalidOperationException("Cvor korisnika vec postoji u grafu.");
            }

            await _followRepository.CreateUserNodeAsync(userId, username);
        }

        public async Task FollowAsync(long followerId, long followedId)
        {
            if (followerId == followedId)
            {
                throw new InvalidOperationException("Ne mozete zapratiti sami sebe.");
            }

            var alreadyFollows = await _followRepository.ExistsAsync(followerId, followedId);

            if (alreadyFollows)
            {
                throw new InvalidOperationException("Vec pratite ovog korisnika.");
            }

            var success = await _followRepository.FollowAsync(followerId, followedId);

            if (!success)
            {
                throw new InvalidOperationException("Korisnik koji prati ili korisnik koji se prati ne postoji u grafu.");
            }
        }

        public async Task UnfollowAsync(long followerId, long followedId)
        {
            if (followerId == followedId)
            {
                throw new InvalidOperationException("Ne mozete otpratiti sami sebe.");
            }

            var follows = await _followRepository.ExistsAsync(followerId, followedId);

            if (!follows)
            {
                throw new InvalidOperationException("Ne pratite ovog korisnika.");
            }

            var success = await _followRepository.UnfollowAsync(followerId, followedId);

            if (!success)
            {
                throw new InvalidOperationException("Follow odnos nije pronadjen u grafu.");
            }
        }

        public async Task<List<FollowUserDto>> GetFollowedUsersAsync(long followerId)
        {
            return await _followRepository.GetFollowedUsersAsync(followerId);
        }

        public async Task<List<FollowUserDto>> GetFollowersAsync(long userId)
        {
            if (userId <= 0)
            {
                throw new InvalidOperationException("Prosledjeni userId nije ispravan.");
            }

            return await _followRepository.GetFollowersAsync(userId);
        }

        public async Task<int> GetFollowersCountAsync(long userId)
        {
            if (userId <= 0)
            {
                throw new InvalidOperationException("Prosledjeni userId nije ispravan.");
            }

            return await _followRepository.GetFollowersCountAsync(userId);
        }

        public async Task<int> GetFollowingCountAsync(long userId)
        {
            if (userId <= 0)
            {
                throw new InvalidOperationException("Prosledjeni userId nije ispravan.");
            }

            return await _followRepository.GetFollowingCountAsync(userId);
        }

        public async Task<List<FollowUserDto>> GetFollowRecommendationsAsync(long userId)
        {
            if (userId <= 0)
            {
                throw new InvalidOperationException("Prosledjeni userId nije ispravan.");
            }

            return await _followRepository.GetFollowRecommendationsAsync(userId);
        }

        public async Task<bool> IsFollowingAsync(long followerId, long followedId)
        {
            var followedUsers = await GetFollowedUsersAsync(followerId);

            return followedUsers.Any(user => user.UserId == followedId);
        }
    }
}