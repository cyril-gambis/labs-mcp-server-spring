---
marp: true
paginate: true
header: MCP server in Java
footer: May 2026 - Cyril Gambis
---

# **MCP Best Practices**

---

# **Faire le parallèle avec la réalité**

Je suis au support client.
Je veux trouver les commandes d’un client.

Je vais utiliser la fonctionnalité pour chercher les commandes par emails, qui affichent la liste des commandes et le statut des livraisons.

Ce que je ne fais PAS:
- je recherche un identifiant client à partir de son mail
- je recherche les commandes de cet identifiant client
- pour chacune, je recherche le statut de livraison

---

# **Pourquoi c’est un problème avec les agents**

J’ai 3 aller-retours, plus de tokens consommés, plus de choix de tools à effectuer, et ils sont moins clairs car plus éloignés du besoin précis (récupérer les commandes pour voir le statut de livraison).
  
  
## Outcomes, Not Operations

Je veux avoir quoi à la fin? Et non pas comment on doit faire précisément 


---

# **Bien gérer les paramètres des tools**

Utiliser des énumérations claires et des paramètres bien identifiés plutôt que des paramètres génériques

Par exemple, pour une recherche d’emails multi-critères:
“email”, “status” parmi [“pending”, “shipped”, “delivered”] plutôt qu’une liste de critères libres pour une recherche

Cela va réduire les hallucinations et les erreurs d’appel.


---

# **Soigner les instructions**

Ajouter dans la description des tools des instructions d’utilisation claires:
- quand utiliser l’outil: “Use when the user asks about order status”
- comment formater les arguments: “Email must be lowercase”
- quel est le résultat: “Returns order ID and current status”

## Soigner les messages d’erreur

Eviter les exceptions, renvoyer une chaîne de caractères claires: “User not found. Please try searching by email address instead.”
L’agent voit l’erreur et utilise le message d’erreur pour se corriger et ré-essayer (ou pour les utilisations ultérieures).


---

# **Limiter le nombre d’outils**

Toutes les descriptions, les réponses des outils, les messages d’erreurs s’ajoutent au contexte du LLM.
- limiter le nombre d’outils par serveur MCP: 5-15 (mai 2026), c’est bien
- un serveur MCP particulier dans un cadre spécifique (pas de “fourre-tout”)
- supprimer les tools inutiles
- séparer par “persona” (administrateur/utilisateur)

L’agent doit être capable de trouver l’outil rapidement.
## “Build for discovery”
Se mettre à la place de l’agent qui essaye de trouver les bons outils lorsque l’on crée les tools MCP.

---

# **Le nommage des tools est important**

Un agent peut avoir accès à plusieurs serveurs, il doit pouvoir les distinguer facilement.

Utiliser le nom du service en préfixe et décrire l’action effectué par le tool.

## Par exemple, utiliser le pattern: {service}_{action}_{resource}

`slack_send_message`, `sentry_get_error_details`, `onesuite_get_orders`


---

# **Paginer les résultats si nécessaire**

S’il y a plusieurs dizaines/centaines de résultats, le LLM a le même problème qu’avec un humain: difficile de faire le tri, ça prend du temps de tout regarder, ça coûte des tokens.

- utiliser un paramètre “limit” (par défaut entre 20 et 50)
- renvoyer `has_more`, `next_offset`, `total_count`
- attention à ne pas charger tous les résultats en mémoire persistante sur le serveur MCP


---

# **Un exemple: Gmail MCP Server**

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

# **Un exemple: Gmail MCP Server amélioré**

```python
# Reading: 2 flat tools with curated returns
def gmail_search(query: str, limit: int = 10) -> [{"id": str, "subject": str, "sender": str, "date": str, "snippet": str}]: ...
def gmail_read(message_id: str) -> {"subject": str, "sender": str, "body": str, "attachments": [str]}: ...
 
# Writing: Simple examples, not including cc, bcc.
def gmail_send(to: List[str], subject: str, body: str, reply_to_id: str = None) -> {"success": bool, "message_id": str}: ...
```

---

# **Quelques mots sur les Agent Skills**

Il s’agit d’un fichier texte `SKILL.md` qui donne des instructions pour un LLM, pour faire quelque chose de particulier. Et si nécessairedes fichiers de ressource additionnels.
Le fichier est intégré à l’agent IA, de façon statique.

L’agent IA peut décider d’utiliser une skills s’il pense que c’est pertinent. C’est un guide d’utilisation statique.

En fonction des Use Cases, cela peut être plus intéressant qu’un tool MCP.

**Exemple**: une skill qui permet de récupérer l’état d’une commande dans différents système et qui affiche une vision agrégée, avec plus ou moins de détails en fonction du statut (par exemple un appel à un tool sur la livraison si la commande est dans le statut “en cours de livraison”).

---

# **Quelques mots sur les "CLIs"**

Alternative aux tools MCP: on peut donner accès à un agent à un environnement d'exécution (un terminal), et à un exécutable qu'il peut appeler à travers le terminal (un CLI).

Ce CLI peut faire les appels à des APIs.

Cela peut avoir du sens, en fonction du problème que l'on veut traiter:
- CLI: facile en local, plus complexe si déployé (accès au terminal + mise à jour du CLI)
- CLI: permet de réduire la taille du contexte, donc le nombre de tokens
- mais... perd son intérêt pour des uses cases plus avancés

https://circleci.com/blog/mcp-vs-cli/

---

# **Au final, les bonnes pratiques**

- Privilégier le “quoi” au “comment” lorsque l’on conçoit un tool
- Préciser les paramètres du tool: types primitifs et enums
- Structurer les instructions: when to use, how to set parameters, what to expect; faire attention aux messages d’erreur
- Limiter le nombre d’outils, clarifier l’utilisation (admin/user)
- Choisir le nom de l’outils pour le retrouver plus facilement
- Paginer les gros résultats

https://www.philschmid.de/mcp-best-practices

---

# MCP est une Interface Utilisateur pour les agents IA.

# Il faut construire les tools MCP en gardant ça en tête.
