FROM mcr.microsoft.com/dotnet/sdk:6.0 AS build
WORKDIR /src
COPY ["Navi-Storage/Navi-Storage.csproj", "Navi-Storage/"]
RUN dotnet restore "Navi-Storage/Navi-Storage.csproj"
COPY . .
WORKDIR "/src/Navi-Storage"
RUN dotnet build "Navi-Storage.csproj" -c Release -o /app/build
RUN dotnet publish "Navi-Storage.csproj" -c Release -o /app/publish
WORKDIR "/app/publish"
ENTRYPOINT ["dotnet", "Navi-Storage.dll"]