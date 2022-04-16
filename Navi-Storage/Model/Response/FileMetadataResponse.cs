using Navi_Storage.Model.Data;

namespace Navi_Storage.Model.Response;

public class FileMetadataResponse
{
    public string FileId { get; set; }
    public string ParentFileId { get; set; }
    public string FileName { get; set; }
    public string UserId { get; set; }
    public MetadataType MetadataType { get; set; }
}