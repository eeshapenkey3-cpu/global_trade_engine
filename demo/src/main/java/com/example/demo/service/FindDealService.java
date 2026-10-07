package com.example.demo.service;

import com.example.demo.model.ShippingRoute;
import com.example.demo.repository.ShippingRouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FindDealService {
    @Autowired
    private ShippingRouteRepository shippingRouteRepository;

    public List<ShippingRoute> findAndRankDeals(String destinationPort, double productPrice){
        List<ShippingRoute> possibleRoutes = shippingRouteRepository.findByDestinationPortContainingIgnoreCase(destinationPort);
        if(possibleRoutes == null || possibleRoutes.isEmpty()){
            return possibleRoutes;
        }

        for(ShippingRoute route : possibleRoutes){
            double customsDuty = productPrice * route.getCustomsDutyRate();
            double demurrage = route.getTransitDays() * route.getDailyDemurrageFee();
            double total = productPrice + customsDuty + demurrage;

            if("FOB".equalsIgnoreCase(route.getIncoterm())){
                total += route.getBaseFreightCostUsd();
            }
            route.setCalculatedFinalCost(total);
        }
        possibleRoutes.sort((r1,r2) -> Double.compare(r1.getCalculatedFinalCost(), r2.getCalculatedFinalCost()));
        return possibleRoutes;
    }
}
