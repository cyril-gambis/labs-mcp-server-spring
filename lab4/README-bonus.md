# Bonus for lab4: Sampling

Here, the prompt/tool will ask the LLM for something (no human in the loop in this call).

## Customer support strategy prompt

We will create a Prompt that will depend on the content of a customer message.

- Create an @McpPrompt `customer-support-strategy` that will `Analyzes the tone of a customer message and prepares a tailored response prompt with specific business instructions.`
- Two parameters:
-- McpSyncRequestContext context
-- String userMessage
- Ask the LLM for a task:
```java
if (context.sampleEnabled()) {
   CreateMessageResult analysis = context.sample(
                "Analyze this Decathlon customer message and respond with ONE WORD in uppercase " +
                "from: COMPLAINT (if very dissatisfied), ADVICE (technical need), RETURN (size/product issue): " + userMessage
            ); 
}
```
- You can get the answer of the LLM via
```
if (analysis.content() instanceof TextContent text) {
    var myValue = text.text();
}
```
Advice: you can trim and "toUpperCase" the answer of the LLM.
- Depending of the value returned, you generated different instructions:
```java
case "COMPLAINT" -> "Apply the 'Satisfied or Satisfied' charter. Offer a 10€ voucher immediately. Maintain a very empathetic tone.";
case "ADVICE" -> "Use the Decathlon technical catalog. Emphasize usage benefits and product durability.";
case "RETURN" -> "Remind the customer they have 365 days to change their mind with the Decathlon card. Guide them to the in-store kiosk.";
default -> "Respond in a cordial and professional manner.";
```
- Return a prompt asking to draft the final response based on the initial userMessage and the category of message

### Test it!

You can use the following user messages to test:

- "C'est inadmissible ! J'ai reçu mon vélo avec trois semaines de retard et le cadre est déjà rayé. Le service client ne répond jamais au téléphone. Je veux un remboursement immédiat !"

- "Bonjour, je prépare mon premier marathon et j'hésite entre les chaussures Kiprun KD900 et les modèles avec plaque carbone. Lesquelles offrent le meilleur amorti pour un coureur de 85kg ?"

- "J'ai commandé une veste de randonnée en taille L mais elle est beaucoup trop serrée au niveau des épaules. Est-ce que je peux l'échanger contre une taille XL dans le magasin de Lille Centre ?"

- "Bonjour, j'aimerais savoir à quelle heure ferme le magasin Decathlon Campus ce soir. Merci."

## Sampling tool

Create a new McpTool `workshop-repair-estimator`.  
Description: `Estimates the cost and parts needed for a repair based on a technician's description.`

- two parameters: context and a String `repairDescription`
- if sampling is enabled, ask for a concise description of the problem:
```java
"From this bike or sports equipment failure description, extract only: " +
"1. Product type, 2. Defective part, 3. Difficulty (1 to 5). " +
"Be very concise. Description: " + repairDescription
```
- We could then query a price database or apply a rate here. Here, we simulate a structured business response:
```java
"""
*** DECATHLON WORKSHOP ESTIMATE ***
Technical Analysis: <the technical analysis of the first step>

Note: This estimate is generated via AI assistance. 
Please check part availability in stock before confirmation.
"""
```

### Test it!

Some repair descriptions:

- "J'ai un VTT Rockrider dont la chaîne saute systématiquement quand je passe sur le petit plateau. En regardant de plus près, le dérailleur arrière semble tordu suite à une chute et les galets sont très encrassés."

- "Le tapis de course Domyos T540 ne s'allume plus. J'ai vérifié le branchement secteur, c'est OK. Par contre, il y a une odeur de brûlé qui vient du carter moteur et la console affiche un code erreur E1."

- "Ma tente Quechua 2 Seconds Fresh&Black a un arceau brisé au niveau de l'entrée principale après un coup de vent. La toile est intacte mais la structure ne tient plus debout."

- "Bonjour, mon fils est tombé avec sa trottinette Oxelo. Maintenant, la roue avant fait un bruit de frottement bizarre dès qu'il tourne le guidon vers la gauche. Je pense qu'il y a un truc de desserré ou que le roulement est mort, mais je n'y connais rien."