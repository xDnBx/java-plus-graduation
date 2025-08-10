package ru.practicum;

import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.practicum.grpc.stats.action.ActionTypeProto;
import ru.practicum.grpc.stats.action.UserActionProto;
import ru.practicum.grpc.stats.collector.UserActionControllerGrpc;

import java.time.Instant;

@Slf4j
@Service
public class UserActionClient {
    @GrpcClient("collector")
    UserActionControllerGrpc.UserActionControllerBlockingStub client;

    public void collectUserAction(long userId, long eventId, ActionTypeProto actionType, Instant instant) {
        try {
            Timestamp timestamp = Timestamp.newBuilder()
                    .setSeconds(instant.getEpochSecond())
                    .setNanos(instant.getNano())
                    .build();
            UserActionProto request = UserActionProto.newBuilder()
                    .setUserId(userId)
                    .setEventId(eventId)
                    .setActionType(actionType)
                    .setTimestamp(timestamp)
                    .build();

            client.collectUserAction(request);
        } catch (Exception e) {
            log.error("Error while sending request", e);
        }
    }
}