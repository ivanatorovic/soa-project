FROM golang:1.23-alpine AS build

WORKDIR /app

COPY go.mod ./
RUN go mod download

COPY . .

RUN go build -o gateway .

FROM alpine:latest

WORKDIR /app

COPY --from=build /app/gateway .

EXPOSE 8000

CMD ["./gateway"]