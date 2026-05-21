# More advanced tools

## Product catalog

Create a record "Product", with id, name, description, Price
Create a record "Price", with amount (BigDecimal) and currency (String)
Create a "ProductCatalogTools" class (with @Component)
Create a new tool `list-products` that return a list of 14 sport products (a static list created in the code)

Test your tool in the MCP inspector

Create a new tool `get-product-by-id` that return the product by its id. The id is a parameter.

Test your tool in the MCP inspector

## AI agent

An AI agent is:
- an advanced LLM (like Gemini, GPT-4.1, Claude Sonnet...)
- memory
- tools

Copilot can be an agent.

Add a MCP Server in Copilot:
SSE
http://localhost:8080/sse
name: lab-mcp-server-spring (or whatever you want)
in the workspace

Check that Copilot can access the tools.

Open a new chat in "Agent" mode

Ask: "Display the list of products"
Allow the call to the MCP Server

Ask: "Quel est le produit avec l'id 8 ?"
It may, or may not, call the MCP tool "get-product-by-id". You can adjust the prompt to tell the LLM to use the tool.

Ask: "Get the details of a product"
What is the behaviour?

## Improve your tools

The LLM understand an "intent", you don't tell them how they must do.

So it's important to give it details about your tools.

Add an annotation @McpToolParam for the "id" param of your `get-product-by-id`tool, add a description and "required = true".
Add annotations to the McpTool to indicate that the method is read only (it doesn't modify anything), it is not destructive (it won't delete anything), and it is idempotent (we can call it several time and always get the same result), via:
```
annotations = @McpTool.McpAnnotation(...)
```

### MCP server instructions

You can add a global description of the server in the spring.ai.mcp.server.instructions property, like: "This MCP server offers various tools to get product information and manage customer carts."
