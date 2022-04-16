using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using MongoDB.Bson;
using MongoDB.Driver.GridFS;
using Moq;
using Navi_Storage.Model.Data;
using Navi_Storage.Repository;
using Navi_Storage.Service;
using Newtonsoft.Json;
using Xunit;

namespace Navi_Storage_Test.Service;

public class NaviFileServiceTest
{
    private readonly Mock<INaviFileRepository> _mockRepository;

    private NaviFileService TestService => new NaviFileService(_mockRepository.Object);

    public NaviFileServiceTest()
    {
        _mockRepository = new Mock<INaviFileRepository>();
    }

    [Fact(DisplayName = "GetFileMetadata: GetFileMetadata should return null if data is not found.")]
    public async Task Is_GetFileMetadata_Returns_Null_When_Data_Null()
    {
        // Let
        var fileId = "testFileId";
        var testUserId = "testUserId";
        _mockRepository.Setup(a => a.GetUserFileMetadataByIdOrDefaultAsync(fileId, testUserId))
            .ReturnsAsync(value: null);

        // Do
        var response = await TestService.GetFileMetadata(fileId, testUserId);

        // Verify
        _mockRepository.VerifyAll();

        // Check
        Assert.Null(response);
    }

    [Fact(DisplayName = "GetFileMetadata: GetFileMetadata should return corresponding data if data exists.")]
    public async Task Is_GetFileMetadata_Returns_Data_If_Exists()
    {
        // Let
        var fileId = "testFileId";
        var testUserId = "testUserId";
        var naviFileMetadata = new NaviFileMetadata
        {
            MetadataType = MetadataType.File,
            ParentId = "testParentId",
            UserId = testUserId
        };

        var gridFsDictionary = new BsonDocument(new Dictionary<string, object>
        {
            ["_id"] = fileId,
            ["metadata"] = naviFileMetadata.ToBsonDocument(),
            ["filename"] = "testFileName"
        });
        var repositoryResponse = new GridFSFileInfo<string>(gridFsDictionary, new GridFSFileInfoSerializer<string>());
        _mockRepository.Setup(a => a.GetUserFileMetadataByIdOrDefaultAsync(fileId, testUserId))
            .ReturnsAsync(repositoryResponse);

        // Do
        var response = await TestService.GetFileMetadata(fileId, testUserId);

        // Verify
        _mockRepository.VerifyAll();

        // Check
        Assert.NotNull(response);
        Assert.Equal(gridFsDictionary["_id"], response.FileId);
        Assert.Equal(gridFsDictionary["filename"], response.FileName);
        Assert.Equal(naviFileMetadata.ParentId, response.ParentFileId);
        Assert.Equal(naviFileMetadata.MetadataType, response.MetadataType);
        Assert.Equal(naviFileMetadata.UserId, response.UserId);
    }

    [Fact(DisplayName = "GetFileMetadata: GetFileMetadata should return null if data does not exists.")]
    public async Task Is_GetFileMetadata_Returns_Null_If_Data_Does_Not_Exists()
    {
        // Let
        var userId = "testUserId";
        var fileId = "testFileId";
        _mockRepository.Setup(a => a.GetUserFileMetadataByIdOrDefaultAsync(fileId, userId))
            .ReturnsAsync(value: null);

        // Do
        var response = await TestService.GetFileMetadata(fileId, userId);

        // Verify
        _mockRepository.VerifyAll();

        // Check
        Assert.Null(response);
    }
}