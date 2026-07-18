// THE BLUEPRINT: Defines exactly what columns a row of data must have to fit into Java.
package com.example.demo.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "country_currency_metadata")
public class CountryCodes {

    @Id
    @Column(name = "country_name")
    private String countryName;

    @Column(name = "country_symbol")
    private String countrySymbol;

    @Column(name = "native_name")
    private String nativeName;

    @Column(name = "currency_symbol")
    private String currencySymbol;

    @Column(name = "current_conversion")
    private BigDecimal currentConversion;

    @Column(name = "continent")
    private String continent;

    // Default Constructor (Required by JPA)
    public CountryCodes() {
    }

    // Getters and Setters
    public String getCountryName() { return countryName; }
    public void setCountryName(String countryName) { this.countryName = countryName; }

    public String getCountrySymbol() { return countrySymbol; }
    public void setCountrySymbol(String countrySymbol) { this.countrySymbol = countrySymbol; }

    public String getNativeName() { return nativeName; }
    public void setNativeName(String nativeName) { this.nativeName = nativeName; }

    public String getCurrencySymbol() { return currencySymbol; }
    public void setCurrencySymbol(String currencySymbol) { this.currencySymbol = currencySymbol; }

    public BigDecimal getCurrentConversion() { return currentConversion; }
    public void setCurrentConversion(BigDecimal currentConversion) { this.currentConversion = currentConversion; }

    public String getContinent() { return continent; }
    public void setContinent(String continent) { this.continent = continent; }
}