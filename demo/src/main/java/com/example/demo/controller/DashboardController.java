package com.example.demo.controller;

import com.example.demo.repository.ShippingRouteRepository;
import com.example.demo.repository.CountryCodesRepository;
import com.example.demo.service.CurrencyService;
import com.example.demo.service.FindDealService;
import com.example.demo.service.Gemini32Service;
import com.example.demo.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import com.example.demo.model.ShippingRoute;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.stream.Collectors;
import java.util.List;
import java.math.BigDecimal;

@Controller //web controller responsible for handling incoming HTTP requests
public class DashboardController {

    @Autowired
    private ShippingRouteRepository shippingRouteRepository; //opens db repo connection automatically and links it here (injection)

    @Autowired
    private CountryCodesRepository countryCodesRepository;

    @Autowired
    private ProductCategoryService productCategoryService;

    @Autowired
    private Gemini32Service gemini32Service;

    @Autowired
    private FindDealService findDealService;

    @Autowired
    private CurrencyService currencyService;

    @GetMapping("/dashboard")
    public String homeDashboard(Model model) {
        model.addAttribute("currentPage", "dashboard");
        return "dashboard"; // Loads dashboard.html when link is opened
    }

    @PostMapping("/find-deals")
    public String runLogisticsEngine(
            @RequestParam String destinationPort, //extracts the text entered by the user in the browser
            @RequestParam String category,
            Model model) {
        model.addAttribute("categories", productCategoryService.getAllCategoryNames());
        double productPrice = productCategoryService.getCategoryValue(category);

        List<String> destinationPorts = shippingRouteRepository.findAll().stream()
                .map(ShippingRoute::getDestinationPort)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        model.addAttribute("product", category); //UI mirrors the input so it doesnt forget
        model.addAttribute("productPrice", productPrice);
        model.addAttribute("destinationPort", destinationPort);
        model.addAttribute("destinationPorts", destinationPorts);

        List<ShippingRoute> possibleRoutes = findDealService.findAndRankDeals(destinationPort, productPrice);

        if (possibleRoutes == null || possibleRoutes.isEmpty()) {
            model.addAttribute("errorMessage", "No active ports found matching: " + destinationPort);
            return "find-deals"; //fallback
        }

        model.addAttribute("topDeal", possibleRoutes.get(0)); // initially as 0; marker
        if (possibleRoutes.size() > 1)
            model.addAttribute("secondDeal", possibleRoutes.get(1)); //incase theres more than expected routes
        if (possibleRoutes.size() > 2)
            model.addAttribute("thirdDeal", possibleRoutes.get(2)); //even if it calculates 100 routes, only 4 is shown
        if (possibleRoutes.size() > 3) model.addAttribute("fourthDeal", possibleRoutes.get(3));

        model.addAttribute("calculationSuccess", true);
        return "find-deals";
    }

    @GetMapping("/find-deals")
    public String findDealsPage(Model model) {
        model.addAttribute("categories", productCategoryService.getAllCategoryNames());
        List<String> destinationPorts = shippingRouteRepository.findAll().stream()
                .map(ShippingRoute::getDestinationPort)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        model.addAttribute("destinationPorts", destinationPorts);
        return "find-deals";
    }

    @GetMapping("/currency-trend-prediction")
    public String currencyConverterPage(
            @RequestParam(required = false) String currency, //grabs word after ?currency; not required so page works fine even w/out chosen currency
            Model model) {//model = data passed by controller to be diplayed in html
        model.addAttribute("currentPage", "currency-trend-prediction");
        if (currency != null && !currency.isBlank()) { //only bothers attribute if something is ther
            model.addAttribute("selectedCurrency", currency); //decides to show the chart
        }
        return "currency-trend-prediction";
    }

    @GetMapping("/teu-calculator")
    public String teuCalculatorPage(Model model) {

        model.addAttribute("allRoutes", shippingRouteRepository.findAll());
        model.addAttribute("allCountries", countryCodesRepository.findAll());
        model.addAttribute("categories", productCategoryService.getAllCategoryNames());
        model.addAttribute("currentPage", "teu-calculator");
        return "teu-calculator";
    }

    @PostMapping("/teu-calculator")
    public String calculateTeu(
            @RequestParam String routeId,
            @RequestParam String category,
            @RequestParam double cargoWeight,
            @RequestParam String targetCountry,
            Model model) {
        model.addAttribute("allRoutes", shippingRouteRepository.findAll());
        model.addAttribute("allCountries", countryCodesRepository.findAll());
        model.addAttribute("categories", productCategoryService.getAllCategoryNames());
        model.addAttribute("currentPage", "teu-calculator");

        try {
            double productPrice = productCategoryService.getCategoryValue(category);
            ShippingRoute route = shippingRouteRepository.findByRouteId(routeId)
                    .orElseThrow(() -> new RuntimeException("route not found: " + routeId));

            double freightFeeUsd = currencyService.calculateAllocatedFreightFee(routeId, cargoWeight);

            BigDecimal totalLandedCost = currencyService.calculateTotalLandedCostInLocalCurrency(
                    routeId, productPrice, cargoWeight, targetCountry);

            model.addAttribute("totalLandedCost", totalLandedCost);
            model.addAttribute("freightFeeUsd", freightFeeUsd);
            model.addAttribute("transitDays", route.getTransitDays());
            model.addAttribute("targetCountryName", targetCountry);
            model.addAttribute("routeLabel", route.getOriginPort() + " to " + route.getDestinationPort());
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
        }
        return "teu-calculator";
    }
    @GetMapping("/ai-assistant")
    public String aiPage(Model model){
        model.addAttribute("currentPage","ai-assistant");
        return "ai-assistant";
    }

    @PostMapping("/ai-assistant")
    public String askAi(@RequestParam String query, Model model){
        model.addAttribute("currentPage","ai-assistant");
        model.addAttribute("aiQuestion",query);
        model.addAttribute("aiAswer",gemini32Service.askGemini32(query));
        return "ai-assistant";
    }
}
