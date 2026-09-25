package com.example.demo.repository;

import com.example.demo.model.ShippingRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShippingRouteRepository extends JpaRepository<ShippingRoute, String> {


    List<ShippingRoute> findByDestinationPortContainingIgnoreCase(String destinationPort);

    // 1. Keep this for standard Optional handling
    Optional<ShippingRoute> findByRouteId(String routeId);

}