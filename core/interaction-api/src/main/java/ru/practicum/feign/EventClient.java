package ru.practicum.feign;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "event-service", path = "/api/v1/shopping-cart")
public interface EventClient {
}