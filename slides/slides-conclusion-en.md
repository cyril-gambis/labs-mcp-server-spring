---
marp: true
paginate: true
header: MCP server in Java
footer: May 2026 - Cyril Gambis
---

# **MCP Best Practices**

---

# **Drawing Parallels with Reality**

I am in customer support.
I want to find a customer's orders.

I will use the feature to search for orders by email, which displays the list of orders and delivery status.

What I do NOT do:
- I search for a customer ID from their email
- I search for the orders for that customer ID
- For each order, I search for the delivery status

---

# **Why this is a problem for agents**

I have 3 round trips, more tokens consumed, more tool choices to make, and they are less clear because they are further from the precise need (retrieving orders to see delivery status).


## Outcomes, Not Operations

What do I want at the end? Rather than exactly how to do it.


---

# **Managing Tool Parameters Effectively**

Use clear enumerations and well-identified parameters rather than generic parameters.

For example, for a multi-criteria email search:
"email", "status" from ["pending", "shipped", "delivered"] rather than a list of free criteria for a search.

This will reduce hallucinations and call errors.


---

# **Crafting Instructions**

Add clear usage instructions in tool descriptions:
- when to use the tool: "Use when the user asks about order status"
- how to format arguments: "Email must be lowercase"
- what the result is: "Returns order ID and current status"

## Craft Error Messages with attention

Avoid exceptions, return clear strings: "User not found. Please try searching by email address instead."
The agent sees the error and uses the error message to correct itself and retry (or for later use).


---

# **Limiting the Number of Tools**

All descriptions, tool responses, and error messages add to the LLM context.
- Limit the number of tools per MCP server: 5-15 (May 2026) is good.
- Have a specific MCP server in a specific context (no "catch-all" that does everything).
- Remove unnecessary tools.
- Separate by "persona" (admin/user).

The agent must be able to find the tool quickly.
## Build for discovery
Put yourself in the agent's shoes trying to find the right tools when creating MCP tools.

---

# **Tool Naming is Important**

An agent may have access to several servers; it must be able to distinguish them easily.

Use the service name as a prefix and describe the action performed by the tool.

## For example, use the pattern: {service}_{action}_{resource}

`slack_send_message`, `sentry_get_error_details`, `onesuite_get_orders`


---

# **Paginate Results if Necessary**

If there are dozens/hundreds of results, the LLM has the same problem as a human: difficult to sort, takes time to look through everything, and costs tokens.

- Use a "limit" parameter (default between 20 and 50).
- Return `has_more`, `next_offset`, `total_count`.
- Be careful not to load all results into persistent memory on the MCP server.


---

# **An Example: Gmail MCP Server**

```python
# Reading an email requires 2 tools + understanding nested types
def messages_list(query: str, max_results: int) -> {"messages": [{"id": str, "threadId": str}], "nextPageToken": str}: ...
def messages_get(message_id: str, format: str) -> {"id": str, "snippet": str, "payload": {"headers": list, "body": {"data": str}}}: ...
 
# Sending an email requires base64-encoding a MIME message
def messages_send(message: {"raw": str}) -> {"id": str, "threadId": str}: ...  # raw = base64url RFC 2822
 
# Creating a draft has nested message object
def drafts_create(draft: {"message": {"raw": str}}) -> {"id": str, "message": {"id": str}}: ...
```

---

# **An Example: Improved Gmail MCP Server**

```python
# Reading: 2 flat tools with curated returns
def gmail_search(query: str, limit: int = 10) -> [{"id": str, "subject": str, "sender": str, "date": str, "snippet": str}]: ...
def gmail_read(message_id: str) -> {"subject": str, "sender": str, "body": str, "attachments": [str]}: ...
 
# Writing: Simple examples, not including cc, bcc.
def gmail_send(to: List[str], subject: str, body: str, reply_to_id: str = None) -> {"success": bool, "message_id": str}: ...
```

---

# **A Few Words on Agent Skills**

This is a `SKILL.md` text file that gives instructions for an LLM to perform something specific. And if necessary, additional resource files.
The file is statically integrated into the AI agent.

The AI agent can decide to use a skill if it thinks it is relevant. It is a static usage guide.

Depending on the Use Cases, this can be more interesting than an MCP tool.

**Example**: a skill that retrieves the status of an order in different systems and displays an aggregated view, with more or less detail depending on the status (for example, a call to a tool on delivery if the order is in the "in delivery" status).

---

# **A Few Words on "CLIs"**

Alternative to MCP tools: we can give an agent access to an execution environment (a terminal) and an executable it can call through the terminal (a CLI).

This CLI can make calls to APIs.

This can make sense depending on the problem to be addressed:
- CLI: easy locally, more complex if deployed (access to terminal + CLI updates).
- CLI: allows reducing context size, hence the number of tokens.
- but... loses its appeal for more advanced use cases.

https://circleci.com/blog/mcp-vs-cli/

---

# **Final Best Practices**

- Favor the "what" over the "how" when designing a tool.
- Specify tool parameters: primitive types and enums.
- Structure instructions: when to use, how to set parameters, what to expect; pay attention to error messages.
- Limit the number of tools, clarify usage (admin/user).
- Choose tool names for easier discovery.
- Paginate large results.

https://www.philschmid.de/mcp-best-practices

---

# MCP is a User Interface for AI agents.

# You must build MCP tools with this in mind.
