using Microsoft.AspNetCore.Mvc;
using Navi_Storage.Attribute;
using Navi_Storage.Extensions;
using Navi_Storage.Model.Response;
using Navi_Storage.Service;

namespace Navi_Storage.Controllers;

[Route("/api/file")]
[ApiController]
public class NaviFileController : ControllerBase
{
    private readonly NaviFileService _naviFileService;

    public NaviFileController(NaviFileService naviFileService)
    {
        _naviFileService = naviFileService;
    }

    [NaviAuthorization]
    [HttpGet("{fileId}")]
    public async Task<IActionResult> GetFileMetadata(string fileId)
    {
        var userId = HttpContext.GetUserId();
        var fileMetadata = await _naviFileService.GetFileMetadata(fileId, userId);

        if (fileMetadata == null)
        {
            return NotFound(new ErrorResponse
            {
                Code = StatusCodes.Status404NotFound,
                Message = "Requested resource not found!",
                DetailedMessage = $"File Id {fileId} is not found on server!"
            });
        }

        return Ok(fileMetadata);
    }
}