FROM golang:1.23-alpine AS build

WORKDIR /app

RUN apk add --no-cache protobuf protobuf-dev

RUN go install google.golang.org/protobuf/cmd/protoc-gen-go@v1.35.1
RUN go install google.golang.org/grpc/cmd/protoc-gen-go-grpc@v1.5.1

COPY go.mod ./
RUN go mod download

COPY . .

RUN protoc --go_out=. --go-grpc_out=. proto/tour.proto

RUN go mod tidy

RUN go build -o gateway .

FROM alpine:latest

WORKDIR /app

COPY --from=build /app/gateway .

EXPOSE 8000

CMD ["./gateway"]