package com.soa.tour_service.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.63.0)",
    comments = "Source: tour.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class TourRpcServiceGrpc {

  private TourRpcServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "TourRpcService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.soa.tour_service.grpc.GetPublishedToursRequest,
      com.soa.tour_service.grpc.TourListGrpcResponse> getGetPublishedToursMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetPublishedTours",
      requestType = com.soa.tour_service.grpc.GetPublishedToursRequest.class,
      responseType = com.soa.tour_service.grpc.TourListGrpcResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.soa.tour_service.grpc.GetPublishedToursRequest,
      com.soa.tour_service.grpc.TourListGrpcResponse> getGetPublishedToursMethod() {
    io.grpc.MethodDescriptor<com.soa.tour_service.grpc.GetPublishedToursRequest, com.soa.tour_service.grpc.TourListGrpcResponse> getGetPublishedToursMethod;
    if ((getGetPublishedToursMethod = TourRpcServiceGrpc.getGetPublishedToursMethod) == null) {
      synchronized (TourRpcServiceGrpc.class) {
        if ((getGetPublishedToursMethod = TourRpcServiceGrpc.getGetPublishedToursMethod) == null) {
          TourRpcServiceGrpc.getGetPublishedToursMethod = getGetPublishedToursMethod =
              io.grpc.MethodDescriptor.<com.soa.tour_service.grpc.GetPublishedToursRequest, com.soa.tour_service.grpc.TourListGrpcResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetPublishedTours"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.soa.tour_service.grpc.GetPublishedToursRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.soa.tour_service.grpc.TourListGrpcResponse.getDefaultInstance()))
              .setSchemaDescriptor(new TourRpcServiceMethodDescriptorSupplier("GetPublishedTours"))
              .build();
        }
      }
    }
    return getGetPublishedToursMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.soa.tour_service.grpc.GetTourByIdRequest,
      com.soa.tour_service.grpc.TourGrpcResponse> getGetTourByIdMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetTourById",
      requestType = com.soa.tour_service.grpc.GetTourByIdRequest.class,
      responseType = com.soa.tour_service.grpc.TourGrpcResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.soa.tour_service.grpc.GetTourByIdRequest,
      com.soa.tour_service.grpc.TourGrpcResponse> getGetTourByIdMethod() {
    io.grpc.MethodDescriptor<com.soa.tour_service.grpc.GetTourByIdRequest, com.soa.tour_service.grpc.TourGrpcResponse> getGetTourByIdMethod;
    if ((getGetTourByIdMethod = TourRpcServiceGrpc.getGetTourByIdMethod) == null) {
      synchronized (TourRpcServiceGrpc.class) {
        if ((getGetTourByIdMethod = TourRpcServiceGrpc.getGetTourByIdMethod) == null) {
          TourRpcServiceGrpc.getGetTourByIdMethod = getGetTourByIdMethod =
              io.grpc.MethodDescriptor.<com.soa.tour_service.grpc.GetTourByIdRequest, com.soa.tour_service.grpc.TourGrpcResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetTourById"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.soa.tour_service.grpc.GetTourByIdRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.soa.tour_service.grpc.TourGrpcResponse.getDefaultInstance()))
              .setSchemaDescriptor(new TourRpcServiceMethodDescriptorSupplier("GetTourById"))
              .build();
        }
      }
    }
    return getGetTourByIdMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.soa.tour_service.grpc.GetMyToursRequest,
      com.soa.tour_service.grpc.TourListGrpcResponse> getGetMyToursMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetMyTours",
      requestType = com.soa.tour_service.grpc.GetMyToursRequest.class,
      responseType = com.soa.tour_service.grpc.TourListGrpcResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.soa.tour_service.grpc.GetMyToursRequest,
      com.soa.tour_service.grpc.TourListGrpcResponse> getGetMyToursMethod() {
    io.grpc.MethodDescriptor<com.soa.tour_service.grpc.GetMyToursRequest, com.soa.tour_service.grpc.TourListGrpcResponse> getGetMyToursMethod;
    if ((getGetMyToursMethod = TourRpcServiceGrpc.getGetMyToursMethod) == null) {
      synchronized (TourRpcServiceGrpc.class) {
        if ((getGetMyToursMethod = TourRpcServiceGrpc.getGetMyToursMethod) == null) {
          TourRpcServiceGrpc.getGetMyToursMethod = getGetMyToursMethod =
              io.grpc.MethodDescriptor.<com.soa.tour_service.grpc.GetMyToursRequest, com.soa.tour_service.grpc.TourListGrpcResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetMyTours"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.soa.tour_service.grpc.GetMyToursRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.soa.tour_service.grpc.TourListGrpcResponse.getDefaultInstance()))
              .setSchemaDescriptor(new TourRpcServiceMethodDescriptorSupplier("GetMyTours"))
              .build();
        }
      }
    }
    return getGetMyToursMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static TourRpcServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<TourRpcServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<TourRpcServiceStub>() {
        @java.lang.Override
        public TourRpcServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new TourRpcServiceStub(channel, callOptions);
        }
      };
    return TourRpcServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static TourRpcServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<TourRpcServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<TourRpcServiceBlockingStub>() {
        @java.lang.Override
        public TourRpcServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new TourRpcServiceBlockingStub(channel, callOptions);
        }
      };
    return TourRpcServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static TourRpcServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<TourRpcServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<TourRpcServiceFutureStub>() {
        @java.lang.Override
        public TourRpcServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new TourRpcServiceFutureStub(channel, callOptions);
        }
      };
    return TourRpcServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void getPublishedTours(com.soa.tour_service.grpc.GetPublishedToursRequest request,
        io.grpc.stub.StreamObserver<com.soa.tour_service.grpc.TourListGrpcResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetPublishedToursMethod(), responseObserver);
    }

    /**
     */
    default void getTourById(com.soa.tour_service.grpc.GetTourByIdRequest request,
        io.grpc.stub.StreamObserver<com.soa.tour_service.grpc.TourGrpcResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetTourByIdMethod(), responseObserver);
    }

    /**
     */
    default void getMyTours(com.soa.tour_service.grpc.GetMyToursRequest request,
        io.grpc.stub.StreamObserver<com.soa.tour_service.grpc.TourListGrpcResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetMyToursMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service TourRpcService.
   */
  public static abstract class TourRpcServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return TourRpcServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service TourRpcService.
   */
  public static final class TourRpcServiceStub
      extends io.grpc.stub.AbstractAsyncStub<TourRpcServiceStub> {
    private TourRpcServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected TourRpcServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new TourRpcServiceStub(channel, callOptions);
    }

    /**
     */
    public void getPublishedTours(com.soa.tour_service.grpc.GetPublishedToursRequest request,
        io.grpc.stub.StreamObserver<com.soa.tour_service.grpc.TourListGrpcResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetPublishedToursMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getTourById(com.soa.tour_service.grpc.GetTourByIdRequest request,
        io.grpc.stub.StreamObserver<com.soa.tour_service.grpc.TourGrpcResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetTourByIdMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getMyTours(com.soa.tour_service.grpc.GetMyToursRequest request,
        io.grpc.stub.StreamObserver<com.soa.tour_service.grpc.TourListGrpcResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetMyToursMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service TourRpcService.
   */
  public static final class TourRpcServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<TourRpcServiceBlockingStub> {
    private TourRpcServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected TourRpcServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new TourRpcServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.soa.tour_service.grpc.TourListGrpcResponse getPublishedTours(com.soa.tour_service.grpc.GetPublishedToursRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetPublishedToursMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.soa.tour_service.grpc.TourGrpcResponse getTourById(com.soa.tour_service.grpc.GetTourByIdRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetTourByIdMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.soa.tour_service.grpc.TourListGrpcResponse getMyTours(com.soa.tour_service.grpc.GetMyToursRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetMyToursMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service TourRpcService.
   */
  public static final class TourRpcServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<TourRpcServiceFutureStub> {
    private TourRpcServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected TourRpcServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new TourRpcServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.soa.tour_service.grpc.TourListGrpcResponse> getPublishedTours(
        com.soa.tour_service.grpc.GetPublishedToursRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetPublishedToursMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.soa.tour_service.grpc.TourGrpcResponse> getTourById(
        com.soa.tour_service.grpc.GetTourByIdRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetTourByIdMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.soa.tour_service.grpc.TourListGrpcResponse> getMyTours(
        com.soa.tour_service.grpc.GetMyToursRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetMyToursMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_PUBLISHED_TOURS = 0;
  private static final int METHODID_GET_TOUR_BY_ID = 1;
  private static final int METHODID_GET_MY_TOURS = 2;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_PUBLISHED_TOURS:
          serviceImpl.getPublishedTours((com.soa.tour_service.grpc.GetPublishedToursRequest) request,
              (io.grpc.stub.StreamObserver<com.soa.tour_service.grpc.TourListGrpcResponse>) responseObserver);
          break;
        case METHODID_GET_TOUR_BY_ID:
          serviceImpl.getTourById((com.soa.tour_service.grpc.GetTourByIdRequest) request,
              (io.grpc.stub.StreamObserver<com.soa.tour_service.grpc.TourGrpcResponse>) responseObserver);
          break;
        case METHODID_GET_MY_TOURS:
          serviceImpl.getMyTours((com.soa.tour_service.grpc.GetMyToursRequest) request,
              (io.grpc.stub.StreamObserver<com.soa.tour_service.grpc.TourListGrpcResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getGetPublishedToursMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.soa.tour_service.grpc.GetPublishedToursRequest,
              com.soa.tour_service.grpc.TourListGrpcResponse>(
                service, METHODID_GET_PUBLISHED_TOURS)))
        .addMethod(
          getGetTourByIdMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.soa.tour_service.grpc.GetTourByIdRequest,
              com.soa.tour_service.grpc.TourGrpcResponse>(
                service, METHODID_GET_TOUR_BY_ID)))
        .addMethod(
          getGetMyToursMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.soa.tour_service.grpc.GetMyToursRequest,
              com.soa.tour_service.grpc.TourListGrpcResponse>(
                service, METHODID_GET_MY_TOURS)))
        .build();
  }

  private static abstract class TourRpcServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    TourRpcServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.soa.tour_service.grpc.TourProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("TourRpcService");
    }
  }

  private static final class TourRpcServiceFileDescriptorSupplier
      extends TourRpcServiceBaseDescriptorSupplier {
    TourRpcServiceFileDescriptorSupplier() {}
  }

  private static final class TourRpcServiceMethodDescriptorSupplier
      extends TourRpcServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    TourRpcServiceMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (TourRpcServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new TourRpcServiceFileDescriptorSupplier())
              .addMethod(getGetPublishedToursMethod())
              .addMethod(getGetTourByIdMethod())
              .addMethod(getGetMyToursMethod())
              .build();
        }
      }
    }
    return result;
  }
}
