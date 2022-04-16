using System.Net.Http.Headers;

namespace Navi_Storage.Extensions;

public static class HttpRequestExtension
{
    public static string? GetAuthorizationToken(this HttpRequest request)
    {
        if (!AuthenticationHeaderValue.TryParse(request.Headers.Authorization, out var headerValue))
        {
            return null;
        }

        return headerValue.Parameter;
    }
}