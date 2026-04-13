using Neo4j.Driver;

namespace follower_service.Data
{
    public class Neo4jContext : IAsyncDisposable
    {
        private readonly IDriver _driver;

        public Neo4jContext(IConfiguration configuration)
        {
            var uri = configuration["Neo4j:Uri"];
            var username = configuration["Neo4j:Username"];
            var password = configuration["Neo4j:Password"];

            if (string.IsNullOrWhiteSpace(uri) ||
                string.IsNullOrWhiteSpace(username) ||
                string.IsNullOrWhiteSpace(password))
            {
                throw new InvalidOperationException("Neo4j configuration is missing.");
            }

            _driver = GraphDatabase.Driver(uri, AuthTokens.Basic(username, password));
        }

        public IAsyncSession GetSession()
        {
            return _driver.AsyncSession();
        }

        public async ValueTask DisposeAsync()
        {
            await _driver.DisposeAsync();
        }
    }
}