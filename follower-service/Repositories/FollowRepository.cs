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

        public async Task<bool> UserNodeExistsAsync(long userId)
        {
            await using var session = _neo4jContext.GetSession();

            var cursor = await session.RunAsync(@"
                MATCH (u:User {userId: $userId})
                RETURN COUNT(u) AS count
            ", new
            {
                userId
            });

            var record = await cursor.SingleAsync();
            var count = record["count"].As<long>();

            return count > 0;
        }

        public async Task CreateUserNodeAsync(long userId, string username)
        {
            await using var session = _neo4jContext.GetSession();

            await session.RunAsync(@"
                CREATE (u:User {userId: $userId, username: $username})
            ", new
            {
                userId,
                username
            });
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

        public async Task<List<FollowUserDto>> GetFollowRecommendationsAsync(long userId)
        {
            await using var session = _neo4jContext.GetSession();

            var cursor = await session.RunAsync(@"
        MATCH (:User {userId: $userId})-[:FOLLOWS]->(middle:User)-[:FOLLOWS]->(recommended:User)
        WHERE recommended.userId <> $userId
          AND NOT EXISTS {
              MATCH (:User {userId: $userId})-[:FOLLOWS]->(recommended)
          }
        RETURN DISTINCT recommended.userId AS userId, recommended.username AS username
    ", new
            {
                userId
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

        public async Task<bool> UnfollowAsync(long followerId, long followedId)
        {
            await using var session = _neo4jContext.GetSession();

            var cursor = await session.RunAsync(@"
        MATCH (:User {userId: $followerId})-[r:FOLLOWS]->(:User {userId: $followedId})
        DELETE r
        RETURN COUNT(r) AS deletedCount
    ", new
            {
                followerId,
                followedId
            });

            var record = await cursor.SingleAsync();
            var deletedCount = record["deletedCount"].As<long>();

            return deletedCount > 0;
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
        public async Task<List<FollowUserDto>> GetFollowersAsync(long followedId)
        {
            await using var session = _neo4jContext.GetSession();

            var cursor = await session.RunAsync(@"
        MATCH (a:User)-[:FOLLOWS]->(:User {userId: $followedId})
        RETURN a.userId AS userId, a.username AS username
    ", new
            {
                followedId
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

        public async Task<int> GetFollowersCountAsync(long followedId)
        {
            await using var session = _neo4jContext.GetSession();

            var cursor = await session.RunAsync(@"
        MATCH (:User)-[:FOLLOWS]->(:User {userId: $followedId})
        RETURN COUNT(*) AS count
    ", new
            {
                followedId
            });

            var record = await cursor.SingleAsync();
            return (int)record["count"].As<long>();
        }

        public async Task<int> GetFollowingCountAsync(long followerId)
        {
            await using var session = _neo4jContext.GetSession();

            var cursor = await session.RunAsync(@"
        MATCH (:User {userId: $followerId})-[:FOLLOWS]->(:User)
        RETURN COUNT(*) AS count
    ", new
            {
                followerId
            });

            var record = await cursor.SingleAsync();
            return (int)record["count"].As<long>();
        }
    }

}