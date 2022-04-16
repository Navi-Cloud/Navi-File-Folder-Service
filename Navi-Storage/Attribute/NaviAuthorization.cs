using System.Diagnostics.CodeAnalysis;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Filters;
using Navi_Storage.Extensions;
using Navi_Storage.Model.Response;
using Navi_Storage.Service.Common;

namespace Navi_Storage.Attribute;

[ExcludeFromCodeCoverage]
public class NaviAuthorization : ActionFilterAttribute
{
    public override async Task OnActionExecutionAsync(ActionExecutingContext context, ActionExecutionDelegate next)
    {
        var httpContext = context.HttpContext;
        var accessToken = httpContext.Request.GetAuthorizationToken();

        if (accessToken == null)
        {
            context.Result = new UnauthorizedObjectResult(new ErrorResponse
            {
                Code = StatusCodes.Status401Unauthorized,
                Message = "Unauthorized!",
                DetailedMessage =
                    "Requesting to server is not authorized. Make sure you are providing correct auth information."
            });
            return;
        }

        // Get Service
        var naviAuthClient = context.HttpContext.RequestServices.GetService<IHttpClientFactory>()
            .CreateClient(NaviServerNames.NaviAuth);
        var requestMessage = new HttpRequestMessage(HttpMethod.Get, "/api/user")
        {
            Headers =
            {
                {"Authorization", $"Bearer {accessToken}"}
            }
        };
        var response = await naviAuthClient.SendAsync(requestMessage);

        if (!response.IsSuccessStatusCode)
        {
            context.Result = new UnauthorizedObjectResult(new ErrorResponse
            {
                Code = StatusCodes.Status401Unauthorized,
                Message = "Unauthorized!",
                DetailedMessage =
                    "Requesting to server is not authorized. You provided auth information, but authentication service did not authorized you."
            });

            return;
        }

        var responseBody = await response.Content.ReadFromJsonAsync<UserProjection>()
                           ?? throw new NullReferenceException(
                               "Authentication returned successful response but deserializing body returned null!");

        context.HttpContext.SetUserId(responseBody.UserId);

        await base.OnActionExecutionAsync(context, next);
    }
}