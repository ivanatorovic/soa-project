package com.soa.stakeholders_service.grpc;

import com.soa.stakeholders_service.dto.AuthResponse;
import com.soa.stakeholders_service.dto.LoginRequest;
import com.soa.stakeholders_service.dto.RegisterRequest;
import com.soa.stakeholders_service.dto.UserResponse;
import com.soa.stakeholders_service.model.UserRole;
import com.soa.stakeholders_service.service.AuthService;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class AuthGrpcService extends AuthRpcServiceGrpc.AuthRpcServiceImplBase {

    private final AuthService authService;

    public AuthGrpcService(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void register(RegisterGrpcRequest request,
                         StreamObserver<UserGrpcResponse> responseObserver) {

        RegisterRequest registerRequest = new RegisterRequest();

        registerRequest.setUsername(request.getUsername());
        registerRequest.setPassword(request.getPassword());
        registerRequest.setEmail(request.getEmail());
        registerRequest.setRole(UserRole.valueOf(request.getRole()));

        UserResponse user = authService.register(registerRequest);

        UserGrpcResponse response = UserGrpcResponse.newBuilder()
                .setId(user.getId())
                .setUsername(user.getUsername())
                .setEmail(user.getEmail())
                .setRole(user.getRole().toString())
                .setBlocked(user.isBlocked())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void login(LoginGrpcRequest request,
                      StreamObserver<AuthGrpcResponse> responseObserver) {

        LoginRequest loginRequest = new LoginRequest();

        loginRequest.setUsername(request.getUsername());
        loginRequest.setPassword(request.getPassword());

        AuthResponse auth = authService.login(loginRequest);

        UserGrpcResponse userResponse = UserGrpcResponse.newBuilder()
                .setId(auth.getUser().getId())
                .setUsername(auth.getUser().getUsername())
                .setEmail(auth.getUser().getEmail())
                .setRole(auth.getUser().getRole().toString())
                .setBlocked(auth.getUser().isBlocked())
                .build();

        AuthGrpcResponse response = AuthGrpcResponse.newBuilder()
                .setToken(auth.getToken())
                .setUser(userResponse)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}