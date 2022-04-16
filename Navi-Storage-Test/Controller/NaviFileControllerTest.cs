using System.Collections.Generic;
using System.Net;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Net.Http.Json;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Mvc.Testing;
using Microsoft.AspNetCore.TestHost;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Moq;
using Moq.Contrib.HttpClient;
using Navi_Storage_Test.Integration;
using Navi_Storage.Model.Response;
using Navi_Storage.Service.Common;
using Xunit;

namespace Navi_Storage_Test.Controller;

[Collection("Integration")]
public class NaviFileControllerTest
{
    private readonly WebApplicationFactory<Program> _applicationFactory;
    private readonly HttpClient _testHttpClient;

    // For Authorization Service Mockup
    private readonly Mock<HttpMessageHandler> _mockMessageHandler;
    private readonly Mock<IHttpClientFactory> _mockHttpClientFactory;

    public NaviFileControllerTest(IntegrationFixture fixture)
    {
        // Setup Mock for authorization
        _mockMessageHandler = new Mock<HttpMessageHandler>();
        _mockHttpClientFactory = new Mock<IHttpClientFactory>();

        // Setup Integration Tests
        var integrationConfiguration = fixture.TestConfiguration;
        _applicationFactory = new WebApplicationFactory<Program>()
            .WithWebHostBuilder(builder =>
            {
                builder.ConfigureTestServices(services =>
                {
                    services.AddSingleton(fixture.MongoClient);
                    services.AddHttpClient(NaviServerNames.NaviAuth)
                        .ConfigurePrimaryHttpMessageHandler(_ => _mockMessageHandler.Object);
                });
                builder.UseConfiguration(integrationConfiguration);
            });
        _testHttpClient = _applicationFactory.CreateClient();
    }

    [Fact(DisplayName = "GET /api/file/{fileId} Returns 401 Unauthorized when no auth token provided.")]
    public async Task Is_GetFileMetadata_Returns_401_When_No_Token()
    {
        // Let
        _mockMessageHandler.SetupAnyRequest()
            .ReturnsResponse(HttpStatusCode.Unauthorized);

        // Do
        var response = await _testHttpClient.GetAsync("/api/file/test");

        // Check
        Assert.False(response.IsSuccessStatusCode);
        Assert.Equal(HttpStatusCode.Unauthorized, response.StatusCode);
    }

    [Fact(DisplayName = "GET /api/file/{fileId} Returns 404 Not Found when requested file is not found.")]
    public async Task Is_GetFileMetadata_Returns_404_When_File_Not_Found()
    {
        // Let
        _mockMessageHandler.SetupAnyRequest()
            .ReturnsResponse(HttpStatusCode.OK, JsonContent.Create(new UserProjection {UserId = "testUserId"}));

        // Do
        _testHttpClient.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer", "test");
        var response = await _testHttpClient.GetAsync("/api/file/test");

        // Check
        Assert.False(response.IsSuccessStatusCode);
        Assert.Equal(HttpStatusCode.NotFound, response.StatusCode);
    }

    [Fact(DisplayName = "GET /api/file/{fileId}/explore returns list of data if exists")]
    public async Task Is_ExploreFolder_Returns_List_Of_Data_If_Exists()
    {
        // Let
        _mockMessageHandler.SetupAnyRequest()
            .ReturnsResponse(HttpStatusCode.OK, JsonContent.Create(new UserProjection {UserId = "testUserId"}));

        // Do
        _testHttpClient.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer", "test");
        var response = await _testHttpClient.GetAsync($"api/file/test/explore");

        // Check
        Assert.True(response.IsSuccessStatusCode);
        Assert.Equal(HttpStatusCode.OK, response.StatusCode);
    }
}