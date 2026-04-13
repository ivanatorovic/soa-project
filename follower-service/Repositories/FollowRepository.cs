using follower_service.Data;
using follower_service.Dtos;
using Neo4j.Driver;

namespace follower_service.Repositories
{
    public class FollowRepository
    {
        private readonly Neo4jContext _neo4jContext;

        public FollowRepository(Neo4jContext neo4jContext)
        {
            _neo4jContext = neo4jContext;
        }

        public async Task<bool> ExistsAsync(long followerId, long followedId)
        {
            await using var session = _neo4jContext.GetSession();

            var cursor = await session.RunAsync(@"
                MATCH (:User {userId: $followerId})-[:FOLLOWS]->(:User {userId: $followedId})
                RETURN COUNT(*) AS count
            ", new
            {
                followerId,
                followedId
            });

            var record = await cursor.SingleAsync();
            var count = record["count"].As<long>();

            return count > 0;
        }

        public async Task<bool> FollowAsync(long followerId, long followedId)
        {
            await using var session = _neo4jContext.GetSession();

            var cursor = await session.RunAsync(@"
                MATCH (a:User {userId: $followerId}), (b:User {userId: $followedId})
                MERGE (a)-[:FOLLOWS]->(b)
                RETURN a, b
            ", new
            {
                followerId,
                followedId
            });

            var records = await cursor.ToListAsync();

            return records.Count > 0;
        }

        public async Task<List<FollowUserDto>> GetFollowedUsersAsync(long followerId)
        {
            await using var session = _neo4jContext.GetSession();

            var cursor = await session.RunAsync(@"
        MATCH (:User {userId: $followerId})-[:FOLLOWS]->(b:User)
        RETURN b.userId AS userId, b.username AS username
    ", new
            {
                followerId
            });

            var records = await cursor.ToListAsync();

            var result = new List<FollowUserDto>();

            foreach (var record in records)
            {
                result.Add(new FollowUserDto
                {
                    UserId = record["userId"].As<long>(),
                    Username = record["username"].As<string>()
                });
            }

            return result;
        }
    }
}