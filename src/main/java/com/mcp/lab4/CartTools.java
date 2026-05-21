package com.mcp.lab4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.ai.mcp.annotation.McpArg;
import org.springframework.ai.mcp.annotation.McpPrompt;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.ai.mcp.annotation.context.McpSyncRequestContext;
import org.springframework.ai.mcp.annotation.context.StructuredElicitResult;
import org.springframework.stereotype.Component;

import com.mcp.lab2.Product;
import com.mcp.lab2.ProductCatalogTools;

import io.modelcontextprotocol.spec.McpSchema.ElicitResult;

@Component
public class CartTools {

    private final ProductCatalogTools productCatalogTools;
    private final CustomerTools customerTools;
    private final Map<String, List<CartItem>> carts = new HashMap<>();

    public CartTools(ProductCatalogTools productCatalogTools, CustomerTools customerTools) {
        this.productCatalogTools = productCatalogTools;
        this.customerTools = customerTools;
    }

    public record CartItem(Product product, int quantity) {
    }

    @McpPrompt(name = "cart-operations", description = "Prompt for managing a customer's shopping cart")
    public String promptCartOperations() {
        return """
                You are an e-commerce shopping cart management tool.
                You can add products to a customer's cart, display the cart contents, and remove items from the cart.

                You must have the id of the customer to perform any action on their cart.
                You can use the 'add-to-cart' tool to add products, the 'display-cart' tool to view the cart contents, and the 'remove-from-cart' tool to remove items.

                To get the id of the customer, use the 'get-customer-id' tool from the CustomerTools and provide the customer's username (ask the user for it).

                When adding a product, ensure you specify the product id and quantity.
                When displaying the cart, provide a clear summary of the items and their quantities.
                When removing an item, confirm the removal by specifying the product id.

                Provide clear and concise responses for each action.
                """;
    }

    @McpPrompt(name = "add-to-cart", description = "Add a product to the customer's cart.")
    public String promptAddToCart(
            @McpArg(name = "customerId", description = "The identifier of the customer", required = true) String customerId,
            @McpArg(name = "productId", description = "The identifier of the product", required = true) String productId,
            @McpArg(name = "quantity", description = "The quantity of the product", required = true) int quantity) {

        return """
                You are an e-commerce shopping cart management tool.
                You can add products to a customer's cart using the 'add-to-cart' tool with %s as the customer id, %s as the product id and %d as the quantity.

                Provide clear and concise responses for each action.
                """
                .formatted(customerId, productId, quantity);
    }

    @McpTool(name = "add-to-cart", description = "Add a product to the customer's cart.")
    public String addProductToCart(
            @McpToolParam(description = "The identifier of the customer", required = true) String customerId,
            @McpToolParam(description = "The identifier of the product", required = true) String productId,
            @McpToolParam(description = "The quantity of the product", required = true) int quantity) {

        if (!customerTools.existsById(customerId)) {
            return "Customer not found";
        }

        Product product = productCatalogTools.getProductById(productId);
        if (product == null) {
            return "Product not found";
        }

        carts.computeIfAbsent(customerId, k -> new ArrayList<>());
        List<CartItem> cart = carts.get(customerId);

        // Check if product already in cart, if so update quantity
        for (int i = 0; i < cart.size(); i++) {
            CartItem item = cart.get(i);
            if (item.product().id().equals(productId)) {
                cart.set(i, new CartItem(item.product(), item.quantity() + quantity));
                return "Updated quantity for " + product.name() + " in cart.";
            }
        }

        cart.add(new CartItem(product, quantity));
        return "Added " + quantity + " x " + product.name() + " to cart.";
    }

    @McpTool(name = "display-cart", description = "Display the content of the customer's cart")
    public List<CartItem> displayCart(
            McpSyncRequestContext context,
            @McpToolParam(description = "The identifier of the customer, or empty if unknown", required = false) String customerId) {

        if (customerId == null || customerId.isEmpty()) {
            context.info("Customer ID not provided, eliciting customer information.");

            // Check capabilities before using
            if (context.elicitEnabled()) {
                // Request user input (only in stateful mode)
                StructuredElicitResult<CustomerInfo> elicitResult = context.elicit(CustomerInfo.class);
                if (elicitResult.action() == ElicitResult.Action.ACCEPT) {
                    // Use elicited data
                    String elicitedUsername = elicitResult.structuredContent().username();
                    String customerIdFromUsername = customerTools.getCustomerId(elicitedUsername);
                    return carts.getOrDefault(customerIdFromUsername, List.of());
                } else {
                    context.info("Elicitation was cancelled or failed.");
                    return List.of();
                }
            } else {
                return List.of();
            }
        } else {
            return carts.getOrDefault(customerId, List.of());
        }
    }

    @McpTool(name = "remove-from-cart", description = "Remove an item from the customer's cart")
    public String removeItemFromCart(
            @McpToolParam(description = "The identifier of the customer", required = true) String customerId,
            @McpToolParam(description = "The identifier of the product", required = true) String productId) {

        if (!customerTools.existsById(customerId)) {
            return "Customer not found";
        }

        List<CartItem> cart = carts.get(customerId);
        if (cart == null) {
            return "Cart is empty";
        }

        boolean removed = cart.removeIf(item -> item.product().id().equals(productId));
        if (removed) {
            return "Removed product from cart";
        } else {
            return "Product not found in cart";
        }
    }

}
