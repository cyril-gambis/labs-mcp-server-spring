# labs-mcp-server-spring
Welcom to the Labs on MCP servers with Spring framework !

In this training, you will understand what is an MCP tool, how it works, and implement your own tools.

## Prerequisites

- You must have Java 25 installed
- You must have Maven (v3.9) installed
- You must have a Github Copilot account activated, with some credits left
- You must have VS Code installed, to test your MCP tools. You can still develop in another IDE (like IntelliJ IDEA); VS Code is used to test.

(Optional but useful) To use the `mcp inspector`, you need `npx` command: it starts a javascript app, so it requires `node`.  
On Mac, you can use `brew` to install it.

Check that everything is installed correctly by starting the Java application in the src/main folder: you must get a log similar to this in the terminal:

`Started McpApplication in 0.705 seconds (process running for 0.853)`

## IMPORTANT NOTICE

These labs are related to MCP, but we will also use Github Copilot, since Github Copilot is a MCP *client* and it can be used as an AI agent.

Be mindful to clearly separate what is related to the MCP server, and what is related to the MCP client !

## Lab 1

Let's get started with a simple MCP tool and test it. We will use Java and Spring framework.

## Lab 2

Going deeper; what is an "AI agent" ? Let's implement a real (but simplified) use case with a product catalog.

## Lab 3

More advanced features of MCP tools: notifications, prompts; MCP tools are not "just" basic API endpoints, you can have bidirectional behaviour between an agent and a MCP server.

## Lab 4

Let's get serious: even more advanced features. You should now have a good grasp of MCP tools. We will now be able to select your products from your catalog and put them in a cart.

We will use the Github Copilot "custom agent" feature to interact with our MCP tools.

### Lab 4 - bonus

If you have time: the "sampling" feature. It is the ability for an MCP tool to rely on the intelligence of the LLM directly, adding intelligence in a MCP tool.

## Lab 5

What could possibly go wrong? MCP tools and Generative AI have new threats and security vulnerabilities to take into account. Let's find about some of them.

## Lab 6

We add security on our MCP endpoints, as we would do with REST API endpoints. We will rely on OAuth2 for that.



