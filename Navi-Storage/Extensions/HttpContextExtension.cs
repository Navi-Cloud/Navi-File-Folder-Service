namespace Navi_Storage.Extensions;

public static class HttpContextExtension
{
    public const string UserIdKey = "userId";

    public static string GetUserId(this HttpContext httpContext)
    {
        return httpContext.Items[UserIdKey] as string ??
               throw new NullReferenceException("Tried to get userId but context is not holding userId!");
    }

    public static void SetUserId(this HttpContext httpContext, string userId)
    {
        httpContext.Items[UserIdKey] = userId;
    }
}