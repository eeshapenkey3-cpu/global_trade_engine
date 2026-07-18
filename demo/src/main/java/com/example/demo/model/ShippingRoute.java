// THE BLUEPRINT: Defines exactly what columns a row of data must have to fit into Java.
package com.example.demo.model;

import jakarta.persistence.*;
@Entity
@Table(name = "teu_logistics")
public class ShippingRoute {

    @Id
    private String routeId;
    private String originPort;
    private String destinationPort;
    private int teuCapacity;
    private double baseFreightCostUsd;
    private double customsDutyRate;
    private String incoterm;
    private String container_type;
    private double daily_demurrage_fee;
    private int transit_days;

    // --- Getters and Setters ---
    public String getRouteId() { return routeId; }
    public void setRouteId(String routeId) { this.routeId = routeId; }

    public String getOriginPort() { return originPort; }
    public void setOriginPort(String originPort) { this.originPort = originPort; }

    public String getDestinationPort() { return destinationPort; }
    public void setDestinationPort(String destinationPort) { this.destinationPort = destinationPort; }

    public int getTeuCapacity() { return teuCapacity; }
    public void setTeuCapacity(int teuCapacity) { this.teuCapacity = teuCapacity; }

    public double getBaseFreightCostUsd() { return baseFreightCostUsd; }
    public void setBaseFreightCostUsd(double baseFreightCostUsd) { this.baseFreightCostUsd = baseFreightCostUsd; }

    public double getCustomsDutyRate() { return customsDutyRate; }
    public void setCustomsDutyRate(double customsDutyRate) { this.customsDutyRate = customsDutyRate; }

    public String getIncoterm(){ return incoterm; }
    public void setIncoterm(String incoterm){ this.incoterm = incoterm; }

    public String getContainerType(){ return container_type; }
    public void setContainerType(String container_type){ this.container_type = container_type; }

    public double getDailyDemurrageFee(){return daily_demurrage_fee; }
    public void setDailyDemurrageFee(double daily_demurrage_fee){this.daily_demurrage_fee = daily_demurrage_fee; }

    @Transient
    private double calculatedFinalCost;

    public double getCalculatedFinalCost() { return calculatedFinalCost; }
    public void setCalculatedFinalCost(double calculatedFinalCost) { this.calculatedFinalCost = calculatedFinalCost; }

    public int getTransitDays(){return transit_days;}
    public void setTransitDays(int transit_days){this.transit_days = transit_days;}
}
