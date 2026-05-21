package com.mcp.lab3;

import org.springframework.ai.mcp.annotation.McpPrompt;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.ai.mcp.annotation.context.McpSyncRequestContext;
import org.springframework.stereotype.Component;

import com.mcp.lab2.Product;
import com.mcp.lab2.ProductCatalogTools;

@Component
public class AdvancedProductCatalogTools {

    private ProductCatalogTools productCatalogTools;

    public AdvancedProductCatalogTools(ProductCatalogTools productCatalogTools) {
        this.productCatalogTools = productCatalogTools;
    }

    @McpPrompt(name = "product-search-prompt", description = "Prompt to search products by name")
    public String getProductSearchPrompt() {
        return """
                You are a product search assistant.
                A user will provide you with a product name or part of a product name.
                Your task is to find and return the most relevant product from the product catalog.
                Use the 'get-product-by-name' tool to perform the search.
                """;
    }

    @McpTool(name = "get-product-by-name", description = "Returns a product by its name")
    public Product getProductByName(McpSyncRequestContext context,
            @McpToolParam(description = "The name of the product", required = true) String name) {

        context.info("Processing your request to find product by name: " + name);

        // Sleep for 5 seconds to simulate a long processing time
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Send progress notification (using convenient method)
        context.progress(p -> p.progress(0.5).total(1.0).message("Processing..."));

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        // Ping the client
        context.ping();

        return productCatalogTools.getAllProducts().stream()
                .filter(product -> product.name().toLowerCase().contains(name.toLowerCase()))
                .findFirst()
                .orElse(null);
    }
}
