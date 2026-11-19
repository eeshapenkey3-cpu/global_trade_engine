# Global Trade Engine

A Spring Boot web app that helps an importer compare shipping routes and work out the **total landed cost** of a shipment, converted into the buyer's local currency. (The UI is branded *Clear Trade Engine*.)

**Stack:** Java 17 · Spring Boot 4.1 · Spring Data JPA · Thymeleaf · PostgreSQL · Maven · Google Gemini API

---

## What it does

| Page | Route | What you get |
| --- | --- | --- |
| Dashboard | `/dashboard` | Landing page and navigation |
| Find Deals | `/find-deals` | Pick a destination port and a product category; the app ranks matching shipping routes by total cost and shows the top four |
| TEU Calculator | `/teu-calculator` | Pick a route, product category, cargo weight and target country; get freight cost, transit time and the total landed cost in local currency |
| Currency Prediction | `/currency-trend-prediction` | Select a currency to see its historical USD rate with a dotted projection line |
| AI Assistant | `/ai-assistant` | Ask a trade or logistics question; the answer comes from the Gemini API |

## How the costs are calculated

**Find Deals** (`FindDealService`) ranks each route by:

```
total = product price + customs duty + demurrage (+ base freight if the incoterm is FOB)
customs duty = product price x route's duty rate
demurrage    = transit days x daily demurrage fee
```

**TEU Calculator** (`CurrencyService`) works in three steps:

```
freight   = (cargo tonnes / 21.6 tonnes per TEU) x route's base freight cost (USD)
landed    = product price + freight + (product price x customs duty rate)
local     = landed (USD) x country's conversion rate, rounded to 2 decimals
```

Money is handled with `BigDecimal` and `RoundingMode.HALF_UP`.

## Project structure

```
global_trade_engine/
├── demo/                              Spring Boot application
│   └── src/main/
│       ├── java/com/example/demo/
│       │   ├── controller/            DashboardController (all routes)
│       │   ├── service/               CurrencyService, FindDealService,
│       │   │                          ProductCategoryService, Gemini32Service
│       │   ├── repository/            Spring Data JPA repositories
│       │   └── model/                 ShippingRoute, CountryCodes (JPA entities)
│       └── resources/
│           ├── templates/             Thymeleaf pages
│           └── static/                CSS and pre-rendered currency graphs
├── TEU_logistics.csv                  Shipping route data
├── country_currency_metdata.csv       Country / currency / conversion rates
├── Global_Trade_Exchange_Rates_2000_2025_BALANCED.csv
├── HS-codes.csv
└── currency_prediction.xlsx
```

## Data

- **`teu_logistics` table** (`ShippingRoute`): route ID, origin and destination port, TEU capacity, base freight (USD), customs duty rate, incoterm, container type, daily demurrage fee, transit days.
- **`country_currency_metadata` table** (`CountryCodes`): country, symbol, native name, currency symbol, current conversion rate, continent.
- The CSVs in the repo root are the source data for these tables.

## Getting started

**Prerequisites:** JDK 17, PostgreSQL, and a [Gemini API key](https://aistudio.google.com/) (only needed for the AI Assistant page).

1. **Create the database**
   ```sql
   CREATE DATABASE thecleartrade_db;
   ```
2. **Set your secrets as environment variables** (never commit them):
   ```bash
   export DB_PASSWORD="your-postgres-password"
   export GEMINI_API_KEY="your-gemini-key"
   ```
   On Windows PowerShell use `$env:DB_PASSWORD="..."`. In IntelliJ, add them under *Run > Edit Configurations > Environment variables*.
3. **Run the app**
   ```bash
   cd demo
   ./mvnw spring-boot:run
   ```
   Hibernate creates the tables on first run (`ddl-auto=update`). Import `TEU_logistics.csv` into `teu_logistics` and `country_currency_metdata.csv` into `country_currency_metadata`, then restart.
4. Open <http://localhost:8080/dashboard>.

## Configuration

`demo/src/main/resources/application.properties` reads secrets from the environment:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/thecleartrade_db
spring.datasource.username=postgres
spring.datasource.password=${DB_PASSWORD}
gemini.api.key=${GEMINI_API_KEY}
```

## Known limitations

- Product prices come from eight fixed category values in `ProductCategoryService`, not from real product data.
- Conversion rates are a static snapshot from the CSV, not live rates.
- The TEU calculation assumes 21.6 tonnes per TEU for every cargo type.
- The currency projection is simple: each currency's average annual growth over 2016 to 2025, extended forward. It is not a statistical forecasting model.
- The graphs on the Currency Prediction page are pre-rendered images, so they don't update when the data changes.

## Roadmap

- Load route and country data from the CSVs automatically on startup
- Live exchange rates
- Per-category weight and TEU assumptions
- Unit tests for `CurrencyService` and `FindDealService`
