using follower_service.Repositories;
using follower_service.Dtos;


namespace follower_service.Services
{
    public class FollowService
    {
        private readonly FollowRepository _followRepository;

        public FollowService(FollowRepository followRepository)
        {
            _followRepository = followRepository;
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

        public async Task<List<FollowUserDto>> GetFollowedUsersAsync(long followerId)
        {
            return await _followRepository.GetFollowedUsersAsync(followerId);
        }
    }
}