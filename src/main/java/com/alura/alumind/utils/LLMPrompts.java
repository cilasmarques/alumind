package com.alura.alumind.utils;

public class LLMPrompts {
  public static final String FEEDBACK_ANALYSIS_PROMPT = """
      Analyze the following user feedback for the AluMind app (a mental health and wellness application):
      %s
      Return the analysis in JSON format with the following structure:
      {
        "sentiment": "[POSITIVO/NEGATIVO/INCONCLUSIVO]",
        "requestedFeatures": [
          {
            "code": "[UNIQUE_FEATURE_CODE]",
            "reason": "[REASON WHY THE FEATURE IS IMPORTANT]"
          }
        ]
      }

      Rules for analysis:
      1. The sentiment must be classified as "POSITIVO", "NEGATIVO", or "INCONCLUSIVO" based on the overall tone of the feedback.
      2. Identify possible requested features in the feedback and, for each one, create a unique code in UPPERCASE_WITH_UNDERSCORES format (e.g., "EDIT_PROFILE").
      3. For each feature, briefly explain why implementing it would be important from the user's perspective.
      4. If there are no requested features, return an empty list for "requestedFeatures".
      5. Ensure the JSON is well-formed and valid.
      6. Return the sentiment, code and reason in portuguese and in the infinitive form.
      """;
}