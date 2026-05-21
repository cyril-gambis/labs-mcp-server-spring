package com.mcp.lab2;

import static com.mcp.lab2.Price.Currency.EUR;

import java.util.List;

import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
public class ProductCatalogTools {

    private static final List<Product> PRODUCTS = List.of(
            new Product("1", "Running Shoes", "Lightweight running shoes", new Price(79.99, EUR)),
            new Product("2", "Fitness Tracker", "Waterproof fitness tracker", new Price(49.99, EUR)),
            new Product("3", "Yoga Mat", "Non-slip yoga mat", new Price(19.99, EUR)),
            new Product("4", "Tennis Racket", "Professional graphite tennis racket", new Price(124.50, EUR)),
            new Product("5", "Basketball", "Official size indoor/outdoor basketball", new Price(29.95, EUR)),
            new Product("6", "Swimming Goggles", "Anti-fog wide vision swimming goggles", new Price(14.99, EUR)),
            new Product("7", "Dumbbells", "Pair of 5kg vinyl coated dumbbells", new Price(34.90, EUR)),
            new Product("8", "Football", "Size 5 match training football", new Price(24.99, EUR)),
            new Product("9", "Cycling Helmet", "Aerodynamic adjustable cycling helmet", new Price(59.90, EUR)),
            new Product("10", "Gym Bag", "Durable sports bag with shoe compartment", new Price(39.99, EUR)),
            new Product("11", "Resistance Bands", "Set of 5 exercise resistance bands", new Price(12.50, EUR)),
            new Product("12", "Water Bottle", "1L stainless steel vacuum insulated bottle", new Price(18.00, EUR)),
            new Product("13", "Badminton Set", "Set including 4 rackets and 6 shuttlecocks", new Price(45.00, EUR)),
            new Product("14", "Hiking Backpack", "40L waterproof hiking backpack with ergonomic straps",
                    new Price(89.90, EUR)));

    @McpTool(name = "list-products", description = "Returns a list of all products in the catalog")
    public List<Product> getAllProducts() {
        return PRODUCTS;
    }

    @McpTool(name = "get-product-by-id", description = "Returns a product by its identifier", annotations = @McpTool.McpAnnotations(title = "Get Product by ID", readOnlyHint = true, destructiveHint = false, idempotentHint = true))
    public Product getProductById(
            @McpToolParam(description = "The identifier of the product", required = true) String id) {
        return PRODUCTS.stream()
                .filter(product -> product.id().equals(id))
                .findFirst()
                .orElse(null);
    }

}
