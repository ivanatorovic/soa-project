package com.soa.blog_service.grpc;

import com.soa.blog_service.dto.CommentResponse;
import com.soa.blog_service.dto.CreateCommentRequest;
import com.soa.blog_service.service.CommentService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

@GrpcService
public class BlogGrpcService extends BlogRpcServiceGrpc.BlogRpcServiceImplBase {

    private final CommentService commentService;

    public BlogGrpcService(CommentService commentService) {
        this.commentService = commentService;
    }

    @Override
    public void createComment(
            CreateCommentGrpcRequest request,
            StreamObserver<CommentGrpcResponse> responseObserver
    ) {
        try {
            CreateCommentRequest createRequest = new CreateCommentRequest();
            createRequest.setBlogId(request.getBlogId());
            createRequest.setText(request.getText());

            ResponseEntity<?> response = commentService.createComment(
                    createRequest,
                    request.getAuthorId(),
                    request.getAuthorUsername(),
                    request.getRole()
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                responseObserver.onError(
                        Status.INVALID_ARGUMENT
                                .withDescription(extractMessage(response.getBody()))
                                .asRuntimeException()
                );
                return;
            }

            CommentResponse comment = (CommentResponse) response.getBody();

            CommentGrpcResponse grpcResponse = mapCommentToGrpc(comment);

            responseObserver.onNext(grpcResponse);
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Greška pri kreiranju komentara: " + e.getMessage())
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void getCommentsByBlogId(
            GetCommentsByBlogIdGrpcRequest request,
            StreamObserver<GetCommentsGrpcResponse> responseObserver
    ) {
        try {
            ResponseEntity<?> response = commentService.getCommentsByBlogId(request.getBlogId());

            if (!response.getStatusCode().is2xxSuccessful()) {
                responseObserver.onError(
                        Status.NOT_FOUND
                                .withDescription(extractMessage(response.getBody()))
                                .asRuntimeException()
                );
                return;
            }

            List<CommentResponse> comments = (List<CommentResponse>) response.getBody();

            GetCommentsGrpcResponse.Builder builder = GetCommentsGrpcResponse.newBuilder();

            for (CommentResponse comment : comments) {
                builder.addComments(mapCommentToGrpc(comment));
            }

            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Greška pri čitanju komentara: " + e.getMessage())
                            .asRuntimeException()
            );
        }
    }

    private CommentGrpcResponse mapCommentToGrpc(CommentResponse comment) {
        return CommentGrpcResponse.newBuilder()
                .setId(comment.getId() == null ? "" : comment.getId())
                .setBlogId(comment.getBlogId() == null ? "" : comment.getBlogId())
                .setAuthorId(comment.getAuthorId() == null ? 0L : comment.getAuthorId())
                .setAuthorUsername(comment.getAuthorUsername() == null ? "" : comment.getAuthorUsername())
                .setText(comment.getText() == null ? "" : comment.getText())
                .setCreatedAt(comment.getCreatedAt() == null ? "" : comment.getCreatedAt().toString())
                .build();
    }

    private String extractMessage(Object body) {
        if (body instanceof Map<?, ?> map && map.get("message") != null) {
            return map.get("message").toString();
        }

        if (body != null) {
            return body.toString();
        }

        return "Greška pri obradi zahteva";
    }
}