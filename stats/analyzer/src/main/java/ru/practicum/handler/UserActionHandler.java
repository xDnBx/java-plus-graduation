package ru.practicum.handler;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.model.ActionType;
import ru.practicum.model.UserAction;
import ru.practicum.repository.UserActionRepository;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserActionHandler {
    final UserActionRepository userActionRepository;

    @Value("${user-action.view:0.4}")
    Double viewAction;

    @Value("${user-action.register:0.8}")
    Double registerAction;

    @Value("${user-action.like:1.0}")
    Double likeAction;

    public void handle(UserActionAvro avro) {
        log.info("Сохранение действия пользователя: {}", avro);
        Optional<UserAction> userActionOpt = userActionRepository.findByUserIdAndEventId(avro.getUserId(),
                avro.getEventId());

        if (userActionOpt.isPresent()) {
            UserAction userAction = userActionOpt.get();
            Double weight = toWeight(userAction.getActionType());
            Double newWeight = toWeight(ActionType.valueOf(avro.getActionType().name()));

            if (newWeight > weight) {
                userAction.setActionType(ActionType.valueOf(avro.getActionType().name()));
                userAction.setTimestamp(avro.getTimestamp());
                userActionRepository.save(userAction);
            }
        } else {
            UserAction userAction = UserAction.builder()
                    .userId(avro.getUserId())
                    .eventId(avro.getEventId())
                    .actionType(ActionType.valueOf(avro.getActionType().name()))
                    .timestamp(avro.getTimestamp())
                    .build();
            userActionRepository.save(userAction);
        }
    }

    private Double toWeight(ActionType actionType) {
            return switch (actionType) {
                case VIEW -> viewAction;
                case REGISTER -> registerAction;
                case LIKE -> likeAction;
            };
    }
}