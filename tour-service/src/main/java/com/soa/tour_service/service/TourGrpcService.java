package com.soa.tour_service.service;
import org.springframework.transaction.annotation.Transactional;
import com.soa.tour_service.dto.TourResponse;
import com.soa.tour_service.grpc.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import com.soa.tour_service.dto.TourTransportTimeResponse;

import java.util.List;

@GrpcService
public class TourGrpcService extends TourRpcServiceGrpc.TourRpcServiceImplBase {

    private final TourService tourService;

    public TourGrpcService(TourService tourService) {
        this.tourService = tourService;
    }

    @Override
    @Transactional(readOnly = true)
    public void getPublishedTours(
            GetPublishedToursRequest request,
            StreamObserver<TourListGrpcResponse> responseObserver
    ) {
        List<TourResponse> tours =
                tourService.getPublishedToursForTourist(request.getTouristId());

        TourListGrpcResponse.Builder responseBuilder =
                TourListGrpcResponse.newBuilder();

        for (TourResponse tour : tours) {
            responseBuilder.addTours(mapToGrpc(tour));
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    @Transactional(readOnly = true)
    public void getTourById(
            GetTourByIdRequest request,
            StreamObserver<TourGrpcResponse> responseObserver
    ) {
        TourResponse tour = tourService.getTourById(
                request.getTourId(),
                request.getUserId(),
                request.getRole()
        );

        responseObserver.onNext(mapToGrpc(tour));
        responseObserver.onCompleted();
    }

    @Override
    @Transactional(readOnly = true)
    public void getMyTours(
            GetMyToursRequest request,
            StreamObserver<TourListGrpcResponse> responseObserver
    ) {
        List<TourResponse> tours =
                tourService.getMyTours(request.getAuthorId());

        TourListGrpcResponse.Builder responseBuilder =
                TourListGrpcResponse.newBuilder();

        for (TourResponse tour : tours) {
            responseBuilder.addTours(mapToGrpc(tour));
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    private TourGrpcResponse mapToGrpc(TourResponse tour) {
        TourGrpcResponse.Builder builder = TourGrpcResponse.newBuilder()
                .setId(tour.getId())
                .setName(tour.getName())
                .setDescription(tour.getDescription())
                .setDifficulty(tour.getDifficulty() != null ? tour.getDifficulty().toString() : "")
                .setPrice(tour.getPrice() != null ? tour.getPrice() : 0.0)
                .setStatus(tour.getStatus() != null ? tour.getStatus().toString() : "")
                .setAuthorId(tour.getAuthorId() != null ? tour.getAuthorId() : 0L)
                .setDistanceInKm(tour.getDistanceInKm() != null ? tour.getDistanceInKm() : 0.0)
                .setInShoppingCart(tour.isInShoppingCart())
                .setPurchased(tour.isPurchased())
                .setAvailableSlots(
                        tour.getAvailableSlots() != null ? tour.getAvailableSlots() : 0
                );

        if (tour.getTransportTimes() != null) {
            for (TourTransportTimeResponse time : tour.getTransportTimes()) {
                builder.addTransportTimes(
                        TourTransportTimeGrpcResponse.newBuilder()
                                .setId(time.getId() != null ? time.getId() : 0L)
                                .setTransportType(time.getTransportType() != null ? time.getTransportType().toString() : "")
                                .setDurationMinutes(time.getDurationMinutes() != null ? time.getDurationMinutes() : 0)
                                .build()
                );
            }
        }

        return builder.build();
    }
}