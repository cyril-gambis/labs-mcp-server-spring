# Let's get serious

## Customer management

Create a CustomerInfo record with a `username` as an attribute
Create a CustomerTools class with a static list of customers: an hashmap with the username as the key and an identifier as the value (a String in the UUID format)

Create two tools: one to get a customer id from their username, one to register a customer with a username as a parameter.

Create `existsByUsername` and `existsById` methods that return a boolean if the customer exists in the hashmap.
Add a couple of fake customers in the customer hashmap:

```java
new ConcurrentHashMap<>(Map.of(
            "Alice", UUID.randomUUID().toString(),
            "Bob", UUID.randomUUID().toString(),
            "Charlie", UUID.randomUUID().toString()))
```

## Cart management system

Create a CartTools class. It will contains tools to manage a cart.

You can use Copilot with a prompt like:

```
Add a simple system to manage carts: an hashmap with a list of products, the key of the hashmap is a string corresponding to the identifier of the customer

there is one method to add a product with its id and a quantity

there is one method that displays the cart

there is one method that deletes an item from the cart
```

For the "displayCart" tool, we don't want that the LLM invent a customerId if he doesn't know about it, so we add the following annotation on the parameter "customerId":
```java
@McpToolParam(description = "The identifier of the customer, or empty if unknown", required = false) String customerId
```

## Improving the CartTools

The method to add a product to the cart should be a tool, with 3 required parameters (customerId, productId, quantity).
If the customerId in not in the customer hashmap, the method should return "Customer not found".

Same for removeItemFromCart: it is a tool and it checks if the customerId exists.

The `displayCart` tool will be adjusted: we add "elicitation", that allow the MCP tool to ask the human user for additional data if needed:
- add a `McpSyncRequestContext context` parameter (*not* an McpToolParam)
- if the customerId is empty or null, add the following code to trigger the elicitation:
```java
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
```

## Let's test this!

You can test with the inspector or Copilot.

### Testing with Github Copilot

Copilot can use its regular tools to inspect the files of your project instead of using the tools we just created, we want to prevent this.

Create a new `custom agent` in Copilot (for instance creating the file `.github/agents/shopping.agent.md`), with the name `Shopping`, description is `Assist the customer to shop`, add your tools to it, and the details:
```
# Shopping assistant instructions
You are a shopping assistant. Help the customer to find products, manage their cart, and complete their purchase. Use the available tools to search for products by name or part of the name, add products to the cart, view the cart contents, and remove items from the cart as needed. Provide clear and concise responses to the customer's requests.
```

In the Copilot chat, set the agent as "Shopping"  and:
- Open a new conversation
- Register an account
- Add a item in the cart
-- for instance, add an item `Yoga mat` in the catalog 
-- ask the LLM "Ajoute moi un tapis de yoga" (in French)
-- you can see the various ways the LLM will try to find the item, either calling the `list products` tool or the `search product by name`
- display the cart

Open another conversation (to get rid of the context) and ask "Affiche moi mon panier". Check if the agent asks for your username.

# IMPORTANT
The authorization of a user should not be managed via elicitation or context (telling the llm our username directly). This is simplified for this lab, and we will improve this in the following labs.  
Why? Because we don't want the LLM know our credentials, we will use OAuth2 to manage authentication correctly.