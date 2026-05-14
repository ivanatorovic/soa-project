using follower_service.Services;
using Grpc.Core;

namespace follower_service.Grpc
{
    public class FollowGrpcService : FollowCheckerService.FollowCheckerServiceBase
    {
        private readonly FollowService _followService;

        public FollowGrpcService(FollowService followService)
        {
            _followService = followService;
        }

        public override async Task<IsFollowingReply> IsFollowing(
            IsFollowingRequest request,
            ServerCallContext context)
        {
            var follows = await _followService.IsFollowingAsync(
                request.FollowerId,
                request.FollowedId
            );

            return new IsFollowingReply
            {
                Follows = follows
            };
        }

        public override async Task<GetFollowingReply> GetFollowing(
            GetFollowingRequest request,
            ServerCallContext context)
        {
            var followedUsers = await _followService.GetFollowedUsersAsync(request.UserId);

            var reply = new GetFollowingReply();

            reply.Users.AddRange(
                followedUsers.Select(user => new FollowUser
                {
                    UserId = user.UserId,
                    Username = user.Username ?? ""
                })
            );

            return reply;
        }
    }
}