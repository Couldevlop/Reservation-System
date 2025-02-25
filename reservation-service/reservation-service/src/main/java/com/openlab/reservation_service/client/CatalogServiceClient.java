package com.openlab.reservation_service.client;

import com.openlab.reservation_service.model.dto.CarDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "catalog-service")
public interface CatalogServiceClient {
@GetMapping("/api/v1/cars/{id}")
 CarDTO getByCardId(@PathVariable("id") String id);

}
