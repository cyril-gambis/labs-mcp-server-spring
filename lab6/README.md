# Let's add authentication with OAuth2

Security is added in the same way as for REST APIs: with OAuth2.

However, there is a subtlety: the client used to access MCP tools, for example GitHub Copilot, is different for each user; therefore, it would require the ability to dynamically register OAuth2 clients with the OAuth2 authentication provider, which is not activated by default for security reasons.

The solution is to "bounce" off the MCP server and use a fixed OAuth2 client configured on this server.

## In action

The easiest way would be to use an Agent Skill for Copilot, to have them add the authentication with OAuth2.


SKILL.md
```
Add OAuth2 Security to MCP Server
Description
This skill provides a standardized approach to securing MCP servers within the Oauth2 ecosystem of your company. It ensures that AI agents and other clients are properly authenticated via OAuth2 before accessing MCP endpoints, maintaining high security standards.

When to use this skill
This skill should be applied in the following situations:

When creating a new MCP server that handles sensitive data or actions.
When adding authentication to an existing unsecured MCP server.
When integrating MCP servers with AI agents that support OAuth2 authentication.

Guidelines
1. Secure MCP Endpoints in SecurityConfig
Configure the Spring Security filter chain to require authentication for MCP-specific endpoints. You should use Spring AI at least in version 1.1, and secure the /mcp and /sse endpoints Use fullyAuthenticated() to ensure only authorized users have access.

2. Provide OAuth2 Metadata via .well-known
Expose an oauth-authorization-server metadata file under the src/main/resources/.well-known/ path. This file contains the necessary OAuth2 endpoints (issuer, authorization, token, etc.) for the Oauth2 server and a registration_endpoint that points back to your MCP server for client configuration.

The default, for the preproduction Oauth2, is located in the xxx directory, in the the ./xxx/oauth-authorization-server file (NOTE: see below)

3. Implement Static Registration for Clients
Create a registration endpoint (e.g., /.well-known/static_registration) that returns a pre-defined client_id and authorized redirect_uris. This allows AI clients (like VS Code Copilot) to automatically discover and use the correct OAuth2 configuration.

4. Strict Redirect URI Validation
Only allow redirect_uris pointing to localhost or 127.0.0.1 for local clients. You can add redirect uris to agents you have yourself deployed (for instance using LibreChat). Allowing other redirects can lead to security vulnerabilities and is strictly prohibited by security policies for this use case.

5. Note for the developer
Tell the developer that he will have to create the client_id on the Oauth2 server, using the Authorization code + PKCE option to create the connection.

Examples
Example 1: Security Configuration
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(matcher ->
                        matcher.requestMatchers("/.well-known/**").permitAll()
                                .requestMatchers("/mcp").fullyAuthenticated()
                                .requestMatchers("/sse").fullyAuthenticated()
                                .anyRequest().fullyAuthenticated()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(config -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .build();
    }
}
Example 2: Well-known Metadata Controller
It is located in the ./assets/WellKnownController.java file.

What to avoid
Exposing /mcp without authentication: This allows unauthorized access to all MCP tools.
Granting access to external Redirect URIs: Always limit redirect_uris to localhost to prevent token theft.
Hardcoding Client Secrets: Use PKCE (Proof Key for Code Exchange) instead of client secrets for client-side authentication.
```


Additional files:

WellKnownController.java

```java
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.Charset;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/.well-known")
public class WellKnownController {

    @Value("${local-mcp-client-id}")
    private String clientId;

    @GetMapping("/{filename}")
    public ResponseEntity<String> getWellKnownFile(@PathVariable String filename, HttpServletRequest request) throws IOException {
        // Validate filename to prevent path traversal
        if (!filename.matches("^[\\w.-]+$") || filename.contains("..")) {
            return ResponseEntity.badRequest().body("Invalid filename");
        }
        Resource resource = new ClassPathResource("well-known/" + filename);
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        var content = resource.getContentAsString(Charset.defaultCharset());
        var origin = request.getScheme()+"://"+request.getHeader(HttpHeaders.HOST);
        content = content.replace("{hostname}",origin);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(content);
    }
    @GetMapping("/{filename}/**")
    public ResponseEntity<String> getWellKnownFileResource(@PathVariable String filename, HttpServletRequest request) throws IOException {
        return getWellKnownFile(filename, request);
    }
    @PostMapping("/static_registration")
    public ResponseEntity<String> handleStaticRegistration(@RequestBody String requestBody) {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {
                            "client_id": "%s",
                            "redirect_uris": ["http://localhost:23717/oauth/callback", "http://localhost:6274/oauth/callback", "http://localhost:6274/oauth/callback/debug"],
                            "scope": "openid profile email"
                        }
                        """.formatted(clientId));
    }
}
```

`oauth-authorization-server` file

_I won't put this file on a public github repository for security reasons. Please liaise with your Oauth2 server team to get this information_
