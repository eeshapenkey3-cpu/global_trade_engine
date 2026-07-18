// THE BRAINS: Does all the heavy business math, weight scaling, and currency conversions.
package com.example.demo.service;

import com.example.demo.model.CountryCodes;
import com.example.demo.model.ShippingRoute;
import com.example.demo.repository.CountryCodesRepository;
import com.example.demo.repository.ShippingRouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service //contains important business logic calculations
public class CurrencyService {

    @Autowired //connects this service to CountryCodesRepository
    private CountryCodesRepository countryRepository;

    @Autowired //connects this service to ShippingRouteRepository
    private ShippingRouteRepository routeRepository;

    //convert standard USD price to local country currency
    public BigDecimal convertFromUSD(String countryName, double amountInUSD) {
        return countryRepository.findByCountryName(countryName)//runs a background SQL query: SELECT * FROM current_conversion_rates WHERE country_name = 'Australia';
                .map(country -> { //maps which currency belongs to the chosen country
                    BigDecimal usdAmount = BigDecimal.valueOf(amountInUSD); //fancier way of a object
                    return usdAmount.multiply(country.getCurrentConversion()) //multiplies original us dollars with the country to be converted to a.k.a mapping the currency
                            .setScale(2, RoundingMode.HALF_UP);//method for rounding till 2 UP, so above 5 or below 5
                })
                .orElseThrow(() -> new RuntimeException("Country not found in dataset: " + countryName));
    }

    // Calculate allocated ocean freight cost based on shipment weight
    public double calculateAllocatedFreightFee(String routeId, double cargoWeightInTonnes) {

        // CHANGED: Using this.routeRepository (lowercase) instead of the capitalized interface name
        ShippingRoute route = this.routeRepository.findByRouteId(routeId)
                .orElseThrow(() -> new RuntimeException("Logistics route not found: " + routeId));

        double maxWeightPerTEU = 21.6;

        double requiredTeuSpace = cargoWeightInTonnes / maxWeightPerTEU;

        return requiredTeuSpace * route.getBaseFreightCostUsd();
    }

    // Calculates the comprehensive Landed Cost and converts it to the customer's local currency
    public BigDecimal calculateTotalLandedCostInLocalCurrency(
            String routeId,
            double productPriceUSD,
            double cargoWeightInTonnes,
            String targetCountryName) {

        // CHANGED: Using this.routeRepository (lowercase) here too!
        ShippingRoute route = this.routeRepository.findByRouteId(routeId)
                .orElseThrow(() -> new RuntimeException("Logistics route not found: " + routeId));

        // 2. Reuse your existing method to get the allocated freight fee
        double allocatedFreightUSD = calculateAllocatedFreightFee(routeId, cargoWeightInTonnes);

        // 3. Calculate Customs Duty fee based on the product value
        double customsFeeUSD = productPriceUSD * route.getCustomsDutyRate();

        // 4. Sum everything up to find the Total Landed Cost in USD
        double totalLandedCostUSD = productPriceUSD + allocatedFreightUSD + customsFeeUSD;

        // 5. Convert the grand total into the client's local currency using your existing engine!
        return convertFromUSD(targetCountryName, totalLandedCostUSD);
    }
}