package com.mcp.lab1;

import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.stereotype.Component;

@Component
public class HelloWorldTool {
    
    @McpTool(name = "hello-world", description = "Returns a Hello, World! message")
    public String getHelloMessage() {
        return "Hello, World!";
    }
}
