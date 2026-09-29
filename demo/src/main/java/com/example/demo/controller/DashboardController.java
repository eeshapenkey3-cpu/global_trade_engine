package com.example.demo.controller;
//pulls in external Spring Boot framework libraries, standard Java collections (List) so file can use them
import com.example.demo.repository.ShippingRouteRepository;
import com.example.demo.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import com.example.demo.model.ShippingRoute;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller //web controller responsible for handling incoming HTTP requests
public class DashboardController {

    @Autowired
    private ShippingRouteRepository shippingRouteRepository; //opens db repo connection automatically and links it here (injection)

    @Autowired
    private ProductCategoryService productCategoryService;

    @GetMapping("/dashboard")
    public String homeDashboard() {
        return "dashboard"; // Loads dashboard.html when link is opened
    }

    @PostMapping("/find-deals")
    public String runLogisticsEngine(
            @RequestParam String destinationPort, //extracts the text entered by the user in the browser
            @RequestParam String product,
            Model model) {

        double productPrice = 10000.00;

        model.addAttribute("product", product); //UI mirrors the input so it doesnt forget
        model.addAttribute("productPrice", productPrice);
        model.addAttribute("destinationPort", destinationPort);

        List<ShippingRoute> possibleRoutes = shippingRouteRepository.findByDestinationPortContainingIgnoreCase(destinationPort); //Spring Data JPA automatically writes db SQL queries, just by looking at the name of Java method

        if (possibleRoutes == null || possibleRoutes.isEmpty()) {
            model.addAttribute("errorMessage", "No active ports found matching: " + destinationPort);
            return "find-deals"; //fallback
        }

        for (ShippingRoute route : possibleRoutes) {
            double customsDuty = productPrice * route.getCustomsDutyRate();
            double demurrage = route.getTransitDays() * route.getDailyDemurrageFee();
            double total = productPrice + customsDuty + demurrage;

            if ("FOB".equalsIgnoreCase(route.getIncoterm())) {
                total += route.getBaseFreightCostUsd(); //adds extra fee
            }
            route.setCalculatedFinalCost(total);
        }

        possibleRoutes.sort((r1, r2) -> Double.compare(r1.getCalculatedFinalCost(), r2.getCalculatedFinalCost())); //compares the two lists r1, and r2

        model.addAttribute("topDeal", possibleRoutes.get(0)); // initially as 0; marker
        if (possibleRoutes.size() > 1) model.addAttribute("secondDeal", possibleRoutes.get(1)); //incase theres more than expected routes
        if (possibleRoutes.size() > 2) model.addAttribute("thirdDeal", possibleRoutes.get(2)); //even if it calculates 100 routes, only 4 is shown
        if (possibleRoutes.size() > 3) model.addAttribute("fourthDeal", possibleRoutes.get(3));

        model.addAttribute("calculationSuccess", true);
        return "find-deals";
    }

    @GetMapping("/currency-trend-prediction")
    public String currencyConverterPage(
            @RequestParam(required = false) String currency, //grabs word after ?currency; not required so page works fine even w/out chosen currency
            Model model) {//model = data passed by controller to be diplayed in html
        if (currency != null && !currency.isBlank()) { //only bothers attribute if something is ther
            model.addAttribute("selectedCurrency", currency); //decides to show the chart
        }
        return "currency-trend-prediction";
    }

    @GetMapping("/teu-calculator")
    public String teuCalculatorPage() {

        return "teu-calculator";
    }
}