# Create a new Spring application

## Installing the environment

(I use VS Code)

- You must have Java 25 installed
- You must have Maven (v3.9) installed

(Optional but useful) To use the `mcp inspector`, you need `npx` command: it starts a javascript app, so it requires `node`.  
On Mac, you can use `brew` to install it.


The application has been created via:
https://start.spring.io/
with the options: Java 25, Maven, Spring Boot 3.5.x and with the libraries:
- "Spring Web"
- "Model Context Protocol Server" (AI)

Start the application to check everything is alright.

## Your first MCP tool

Create a new class "HelloWorldTool" - add @Component on it, so that it can be in the Spring context.
Add a method `getHelloMessage`, no parameter
Add `@McpTool` annotation, with a name and a description (!important)

Open the application.yaml file, in the resources
Add the property:

```
spring:
  ai:
    mcp:
      server:
        annotation-scanner:
          enabled: true
```

## Test your MCP tool

Start your application

Look for the following line in the log:
`o.s.a.m.s.c.a.McpServerAutoConfiguration : Registered tools: 1`

### MCP Inspector (optional)

We will use this tool, the equivalent of Bruno for REST APIs

Install Node.js, for instance: `brew install node`
Start the MCP inspector: `npx @modelcontextprotocol/inspector`
If you have troubles, use Copilot to solve the issue

Transport Type: SSE
URL: http://localhost:8080/sse

Connect

Go to "Tools"
List Tools
Run your tool

## Some explanations

Spring auto-configuration will:
- scan for beans with MCP annotations
- create appropriate specifications
- register them with the MCP server

(https://docs.spring.io/spring-ai/reference/api/mcp/mcp-server-boot-starter-docs.html)