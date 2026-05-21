package com.mcp.lab4.bonus;

import org.springframework.ai.mcp.annotation.McpPrompt;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.context.McpSyncRequestContext;
import org.springframework.stereotype.Component;

import io.modelcontextprotocol.spec.McpSchema.CreateMessageResult;
import io.modelcontextprotocol.spec.McpSchema.TextContent;

@Component
public class SamplingTools {
    
    @McpPrompt(
        name = "customer-support-strategy",
        description = "Analyzes the tone of a customer message and prepares a tailored response prompt with specific business instructions."
    )
    public String supportPrompt(McpSyncRequestContext context, String userMessage) {
        if (context.sampleEnabled()) {
            // 1. Ask the LLM to classify the need (Sampling)
            CreateMessageResult analysis = context.sample(
                "Analyze this Decathlon customer message and respond with ONE WORD in uppercase " +
                "from: COMPLAINT (if very dissatisfied), ADVICE (technical need), RETURN (size/product issue): " + userMessage
            );

            String category = "";
            if (analysis.content() instanceof TextContent text) {
                category = text.text().trim().toUpperCase();
            }

            // 2. Conditional business logic in Java (impossible to do cleanly in a single prompt without sampling)
            String businessInstructions = switch (category) {
                case "COMPLAINT" -> "Apply the 'Satisfied or Satisfied' charter. Offer a 10€ voucher immediately. Maintain a very empathetic tone.";
                case "ADVICE" -> "Use the Decathlon technical catalog. Emphasize usage benefits and product durability.";
                case "RETURN" -> "Remind the customer they have 365 days to change their mind with the Decathlon card. Guide them to the in-store kiosk.";
                default -> "Respond in a cordial and professional manner.";
            };

            // 3. Final prompt enriched by the decision made by the server
            return """
                The customer sent: "%s"
                
                BUSINESS INSTRUCTIONS TO APPLY:
                %s
                
                Now draft the final response for the customer.
                """.formatted(userMessage, businessInstructions);
        }
        return "Sampling activation required to define the response strategy.";
    }

    @McpTool(
        name = "workshop-repair-estimator",
        description = "Estimates the cost and parts needed for a repair based on a technician's description."
    )
    public String estimateRepair(McpSyncRequestContext context, String repairDescription) {
        if (context.sampleEnabled()) {
            // 1. Sampling: Ask the LLM to extract technical entities
            CreateMessageResult extraction = context.sample(
                "From this bike or sports equipment failure description, extract only: " +
                "1. Product type, 2. Defective part, 3. Difficulty (1 to 5). " +
                "Be very concise. Description: " + repairDescription
            );

            String technicalAnalysis = "";
            if (extraction.content() instanceof TextContent text) {
                technicalAnalysis = text.text();
            }

            // 2. Java logic: We could query a price database or apply a rate here
            // Here, we simulate a structured business response
            return """
                *** DECATHLON WORKSHOP ESTIMATE ***
                Technical Analysis: %s
                
                Note: This estimate is generated via AI assistance. 
                Please check part availability in stock before confirmation.
                """.formatted(technicalAnalysis);
        }
        return "Sampling activation required to analyze the technical description.";
    }

}