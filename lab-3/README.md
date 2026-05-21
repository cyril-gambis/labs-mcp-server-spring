# More advanced tools

## Create AdvancedProductCatalogTools.java

Add a tool that search a product by a part of its name, and implements the search.
Add `McpSyncRequestContext context` as a parameter (not an McpToolParam)

Add a notification in the context to indicates the process is starting
Add a timer of 2 seconds
Add a notification in the context to indicate the process is completed
Add another timer of 1 second
Return the result

### Test with MCP inspector

### Test with Copilot

## Create a prompt

In AdvancedProductCatalogTools.java, create an MCP Prompt `getProductSearchPrompt` with the @McpPrompt annotation.
Have Copilot complete the prompt for you, or use:
```
You are a product search assistant.
A user will provide you with a product name or part of a product name.
Your task is to find and return the most relevant product from the product catalog.
Use the 'get-product-by-name' tool to perform the search.
```

Test in MCP inspector
Test in Copilot using the slash command

