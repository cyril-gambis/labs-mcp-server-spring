# Let's add authentication with OAuth2

Security is added in the same way as for REST APIs: with OAuth2.

However, there is a subtlety: the client used to access MCP tools, for example GitHub Copilot, is different for each user; therefore, it would require the ability to dynamically register OAuth2 clients with the OAuth2 authentication provider, which is not activated by default for security reasons.

The solution is to "bounce" off the MCP server and use a fixed OAuth2 client configured on this server.
