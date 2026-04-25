FROM mcr.microsoft.com/dotnet/aspnet:9.0 AS base
WORKDIR /app

FROM mcr.microsoft.com/dotnet/sdk:9.0 AS build
WORKDIR /src

COPY . .
RUN dotnet restore follower-service.csproj
RUN dotnet build follower-service.csproj -c Release -o /app/build

FROM build AS publish
RUN dotnet publish follower-service.csproj -c Release -o /app/publish

FROM base AS final
WORKDIR /app
ENV ASPNETCORE_URLS=http://+:5000
COPY --from=publish /app/publish .
CMD ["dotnet", "follower-service.dll"]