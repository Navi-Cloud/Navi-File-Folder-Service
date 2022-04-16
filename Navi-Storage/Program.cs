using MongoDB.Driver;
using Navi_Storage.Repository;
using Navi_Storage.Service;
using Navi_Storage.Service.Common;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.

builder.Services.AddControllers();
// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

// Add Scoped Service
builder.Services.AddScoped<NaviFileService>();

// Add Singleton Service
builder.Services.AddSingleton(new MongoClient(builder.Configuration.GetConnectionString("MongoConnection")));
builder.Services.AddSingleton<INaviFileRepository, NaviFileRepository>();

// Add HttpClient
builder.Services.AddHttpClient(NaviServerNames.NaviAuth, client =>
{
    var addressStr = builder.Configuration.GetConnectionString(NaviServerNames.NaviAuth);
    client.BaseAddress = new Uri(addressStr);
});

var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseHttpsRedirection();

app.UseAuthorization();

app.MapControllers();

app.Run();