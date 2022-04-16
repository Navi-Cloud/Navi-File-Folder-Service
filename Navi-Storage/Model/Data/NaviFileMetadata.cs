namespace Navi_Storage.Model.Data;

public enum MetadataType
{
    Folder, File
}

public class NaviFileMetadata
{
    public string UserId { get; set; }
    public string ParentId { get; set; }
    public MetadataType MetadataType { get; set; }
}