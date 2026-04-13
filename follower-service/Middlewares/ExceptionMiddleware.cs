using System.Text.Json;

namespace follower_service.Middlewares
{
	public class ExceptionMiddleware
	{
		private readonly RequestDelegate _next;

		public ExceptionMiddleware(RequestDelegate next)
		{
			_next = next;
		}

		public async Task InvokeAsync(HttpContext context)
		{
			try
			{
				await _next(context);
			}
			catch (InvalidOperationException ex)
			{
				context.Response.StatusCode = StatusCodes.Status400BadRequest;
				context.Response.ContentType = "application/json";

				var response = new
				{
					message = ex.Message
				};

				await context.Response.WriteAsync(JsonSerializer.Serialize(response));
			}
			catch (Exception)
			{
				context.Response.StatusCode = StatusCodes.Status500InternalServerError;
				context.Response.ContentType = "application/json";

				var response = new
				{
					message = "Doslo je do neocekivane greske na serveru."
				};

				await context.Response.WriteAsync(JsonSerializer.Serialize(response));
			}
		}
	}
}