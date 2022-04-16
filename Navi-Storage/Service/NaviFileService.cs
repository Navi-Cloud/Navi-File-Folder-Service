using MongoDB.Bson.Serialization;
using Navi_Storage.Model.Data;
using Navi_Storage.Model.Response;
using Navi_Storage.Repository;

namespace Navi_Storage.Service;

public class NaviFileService
{
    private readonly INaviFileRepository _naviFileRepository;

    public NaviFileService(INaviFileRepository fileRepository)
    {
        _naviFileRepository = fileRepository;
    }

    public async Task<FileMetadataResponse?> GetFileMetadata(string fileId, string userId)
    {
        var response = await _naviFileRepository.GetUserFileMetadataByIdOrDefaultAsync(fileId, userId);

        if (response == null) return null;

        var naviFileMetadata = BsonSerializer.Deserialize<NaviFileMetadata>(response.Metadata);
        return new FileMetadataResponse
        {
            FileId = response.Id,
            FileName = response.Filename,
            ParentFileId = naviFileMetadata.ParentId,
            MetadataType = naviFileMetadata.MetadataType,
            UserId = naviFileMetadata.UserId
        };
    }
}