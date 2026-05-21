package com.mcp.lab4;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
public class CustomerTools {

    private static final Map<String, String> CUSTOMERS = new ConcurrentHashMap<>(Map.of(
            "Alice", UUID.randomUUID().toString(),
            "Bob", UUID.randomUUID().toString(),
            "Charlie", UUID.randomUUID().toString()));

    @McpTool(name = "get-customer-id", description = "Get the customer identifier from their username, or null if it doesn't exist")
    public String getCustomerId(
            @McpToolParam(description = "The username of the customer", required = true) String username) {
        return CUSTOMERS.get(username);
    }

    @McpTool(name = "register", description = "Register a new customer with their username")
    public String register(
            @McpToolParam(description = "The username to register", required = true) String username) {
        String id = UUID.randomUUID().toString();
        CUSTOMERS.put(username, id);
        return id;
    }

    public boolean existsByUsername(String username) {
        return CUSTOMERS.containsKey(username);
    }

    public boolean existsById(String id) {
        return CUSTOMERS.containsValue(id);
    }

}
