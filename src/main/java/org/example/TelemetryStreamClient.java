package org.example;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.telemetry.TelemetryRequest;
import org.example.telemetry.TelemetryServiceGrpc;
import org.example.telemetry.TelemetryUpdate;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelemetryStreamClient implements ApplicationRunner {

    private final TelemetryUpdateService telemetryUpdateService;

    @GrpcClient("telemetry-service")
    private TelemetryServiceGrpc.TelemetryServiceStub telemetryServiceStub;

    @Override
    public void run(ApplicationArguments args) {
        TelemetryRequest request = TelemetryRequest.newBuilder().build();
        telemetryServiceStub.streamTelemetry(request, new StreamObserver<>() {
            @Override
            public void onNext(TelemetryUpdate update) {
                telemetryUpdateService.updateTemperatures(
                        update.getSatelliteName(),
                        update.getInsideTemperature(),
                        update.getOutsideTemperature()
                );
            }

            @Override
            public void onError(Throwable throwable) {
                log.warn("Telemetry stream stopped", throwable);
            }

            @Override
            public void onCompleted() {
                log.info("Telemetry stream completed");
            }
        });
    }
}
