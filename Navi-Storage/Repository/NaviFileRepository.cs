using MongoDB.Bson;
using MongoDB.Driver;
using MongoDB.Driver.GridFS;

namespace Navi_Storage.Repository;

public interface INaviFileRepository
{
    Task<GridFSFileInfo<string>?> GetUserFileMetadataByIdOrDefaultAsync(string fileId, string userId);
    Task<List<GridFSFileInfo<string>>> ListFileInformationByIdAsync(string parentId, string userId);
}

public class NaviFileRepository : INaviFileRepository
{
    private readonly IMongoClient _mongoClient;
    private readonly IMongoDatabase _mongoDatabase;
    private readonly IGridFSBucket<string> _gridFsBucket;

    public NaviFileRepository(IMongoClient mongoClient, IConfiguration configuration)
    {
        _mongoClient = mongoClient;
        _mongoDatabase = mongoClient.GetDatabase(configuration["MongoSection:DatabaseName"]);
        _gridFsBucket = new GridFSBucket<string>(_mongoDatabase);
    }

    public async Task<GridFSFileInfo<string>?> GetUserFileMetadataByIdOrDefaultAsync(string fileId, string userId)
    {
        var filter = Builders<GridFSFileInfo<string>>.Filter.And(
            Builders<GridFSFileInfo<string>>.Filter.Eq(a => a.Id, fileId),
            Builders<GridFSFileInfo<string>>.Filter.Eq("metadata.userId", userId));

        using var cursor = await _gridFsBucket.FindAsync(filter);

        return await cursor.FirstOrDefaultAsync();
    }

    public async Task<List<GridFSFileInfo<string>>> ListFileInformationByIdAsync(string parentId, string userId)
    {
        var filter = Builders<GridFSFileInfo<string>>.Filter.And(
            Builders<GridFSFileInfo<string>>.Filter.Eq("metadata.userId", userId),
            Builders<GridFSFileInfo<string>>.Filter.Eq("metadata.parentId", parentId));

        using var cursor = await _gridFsBucket.FindAsync(filter);

        return await cursor.ToListAsync();
    }
}