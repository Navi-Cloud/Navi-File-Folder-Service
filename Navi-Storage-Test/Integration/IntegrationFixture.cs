using System;
using System.Collections.Generic;
using Microsoft.Extensions.Configuration;
using MongoDB.Bson.Serialization.Conventions;
using MongoDB.Driver;
using Navi_Storage.Service.Common;

namespace Navi_Storage_Test.Integration;

public class IntegrationFixture
{
    // Test Configuration For Integration. Created Every time whenever called.
    public IConfiguration TestConfiguration => new ConfigurationBuilder()
        .AddInMemoryCollection(new Dictionary<string, string>
        {
            ["MongoSection:DatabaseName"] = Guid.NewGuid().ToString(),
            [$"ConnectionStrings:{NaviServerNames.NaviAuth}"] = "http://navi-auth-container",
            ["ConnectionStrings:MongoConnection"] = "mongodb://root:testPassword@localhost:27018"
        })
        .Build();

    public readonly IMongoClient MongoClient;

    public IntegrationFixture()
    {
        // Setup MongoDB Naming Convention
        var camelCase = new ConventionPack {new CamelCaseElementNameConvention()};
        ConventionRegistry.Register("CamelCase", camelCase, a => true);
        MongoClient = new MongoClient("mongodb://root:testPassword@localhost:27018");
    }
}