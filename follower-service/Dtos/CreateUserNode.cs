namespace follower_service.Dtos
{
	public class CreateUserNodeDto
	{
		public long UserId { get; set; }
		public string Username { get; set; } = string.Empty;
	}
}