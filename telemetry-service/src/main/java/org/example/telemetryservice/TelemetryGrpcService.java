package org.example.telemetryservice;

import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.example.telemetryservice.kafka.SatelliteRegistry;
import org.example.telemetry.TelemetryRequest;
import org.example.telemetry.TelemetryServiceGrpc;
import org.example.telemetry.TelemetryUpdate;

import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@GrpcService
public class TelemetryGrpcService extends TelemetryServiceGrpc.TelemetryServiceImplBase {

    private final ScheduledExecutorService executorService;
    private final SatelliteRegistry satelliteRegistry;
    private final Random random = new Random();

    public TelemetryGrpcService(ScheduledExecutorService executorService, SatelliteRegistry satelliteRegistry) {
        this.executorService = executorService;
        this.satelliteRegistry = satelliteRegistry;
    }

    @Override
    public void streamTelemetry(TelemetryRequest request, StreamObserver<TelemetryUpdate> responseObserver) {
        AtomicInteger satelliteIndex = new AtomicInteger();
        AtomicReference<ScheduledFuture<?>> scheduledFuture = new AtomicReference<>();

        if (responseObserver instanceof ServerCallStreamObserver<TelemetryUpdate> serverObserver) {
            serverObserver.setOnCancelHandler(() -> {
                ScheduledFuture<?> future = scheduledFuture.get();
                if (future != null) {
                    future.cancel(false);
                }
            });
        }

        scheduledFuture.set(executorService.scheduleAtFixedRate(() -> {
            List<String> satellites = satelliteRegistry.names();
            if (satellites.isEmpty()) {
                return;
            }
            int currentIndex = Math.floorMod(
                    satelliteIndex.getAndUpdate(value -> (value + 1) % satellites.size()),
                    satellites.size()
            );
            String satelliteName = satellites.get(currentIndex);
            TelemetryUpdate update = TelemetryUpdate.newBuilder()
                    .setSatelliteName(satelliteName)
                    .setInsideTemperature(randomTemperature(-5.0, 35.0))
                    .setOutsideTemperature(randomTemperature(-160.0, 120.0))
                    .setTimestamp(Instant.now().toString())
                    .build();
            responseObserver.onNext(update);
        }, 0, 2, TimeUnit.SECONDS));
    }

    private double randomTemperature(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }
}
