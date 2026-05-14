package com.soa.blog_service.grpc;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FollowerGrpcService {

    @GrpcClient("follower-service")
    private FollowCheckerServiceGrpc.FollowCheckerServiceBlockingStub followStub;

    public boolean isFollowing(Long followerId, Long followedId) {
        IsFollowingRequest request = IsFollowingRequest.newBuilder()
                .setFollowerId(followerId)
                .setFollowedId(followedId)
                .build();

        IsFollowingReply response = followStub.isFollowing(request);

        return response.getFollows();
    }

    public List<Long> getFollowingIds(Long userId) {
        GetFollowingRequest request = GetFollowingRequest.newBuilder()
                .setUserId(userId)
                .build();

        GetFollowingReply response = followStub.getFollowing(request);

        return response.getUsersList()
                .stream()
                .map(FollowUser::getUserId)
                .toList();
    }
}