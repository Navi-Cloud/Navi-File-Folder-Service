using System;
using System.Text;
using System.Threading.Tasks;
using MongoDB.Bson;
using MongoDB.Bson.Serialization;
using MongoDB.Driver.GridFS;
using Navi_Storage_Test.Integration;
using Navi_Storage.Model.Data;
using Navi_Storage.Repository;
using Newtonsoft.Json;
using Xunit;

namespace Navi_Storage_Test.Repository;

[Collection("Integration")]
public class NaviFileRepositoryTest
{
    private readonly INaviFileRepository _naviFileRepository;
    private readonly IGridFSBucket<string> _gridFsBucket;

    public NaviFileRepositoryTest(IntegrationFixture fixture)
    {
        var configuration = fixture.TestConfiguration;
        var database = fixture.MongoClient.GetDatabase(configuration["MongoSection:DatabaseName"]);
        _gridFsBucket = new GridFSBucket<string>(database);

        _naviFileRepository = new NaviFileRepository(fixture.MongoClient, configuration);
    }

    [Fact(DisplayName =
        "GetUserFileMetadataByIdOrDefaultAsync: GetUserFileMetadataByIdOrDefaultAsync should return null if data does not exists.")]
    public async Task Is_GetUserFileMetadataByIdOrDefaultAsync_Returns_Null_When_Data_None()
    {
        // Let
        var fileId = ObjectId.Empty;
        var userId = "testUserId";

        // Do
        var result = await _naviFileRepository.GetUserFileMetadataByIdOrDefaultAsync(fileId.ToString(), userId);

        // Check
        Assert.Null(result);
    }

    [Fact(DisplayName =
        "GetUserFileMetadataByIdOrDefaultAsync: GetUserFileMetadataByIdOrDefaultAsync should return corresponding data if data exists.")]
    public async Task Is_GetUserFileMetadataByIdOrDefaultAsync_Returns_Data_When_Exists()
    {
        // Let
        var metadata = new NaviFileMetadata
        {
            MetadataType = MetadataType.File,
            ParentId = "testParentId",
            UserId = "testUserId"
        };
        var fileByte = Encoding.UTF8.GetBytes("testFile");
        var fileId = Ulid.NewUlid().ToString();
        await _gridFsBucket.UploadFromBytesAsync(fileId, "fileName", fileByte, new GridFSUploadOptions
        {
            Metadata = metadata.ToBsonDocument()
        });

        // Do
        var result = await _naviFileRepository.GetUserFileMetadataByIdOrDefaultAsync(fileId, metadata.UserId);

        // Check
        Assert.NotNull(result);
        Assert.Equal(fileId, result.Id);
        Assert.Equal("fileName", result.Filename);
        var fileMetadata = BsonSerializer.Deserialize<NaviFileMetadata>(result.Metadata);
        Assert.NotNull(fileMetadata);
        Assert.Equal(metadata.MetadataType, fileMetadata.MetadataType);
        Assert.Equal(metadata.ParentId, fileMetadata.ParentId);
        Assert.Equal(metadata.UserId, fileMetadata.UserId);
    }
}