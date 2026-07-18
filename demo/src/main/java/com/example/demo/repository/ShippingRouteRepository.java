package com.example.demo.repository;

import com.example.demo.model.ShippingRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShippingRouteRepository extends JpaRepository<ShippingRoute, Long> {

    // For your 4-box wireframe ranking engine
    List<ShippingRoute> findByDestinationPortContainingIgnoreCase(String destinationPort);

    // 1. Keep this for standard Optional handling
    Optional<ShippingRoute> findByRouteId(String routeId);

    // 2. PASTE THIS LINE HERE: This ensures any code treating it as a List will instantly compile!
    List<ShippingRoute> findRoutesByRouteId(String routeId);
}