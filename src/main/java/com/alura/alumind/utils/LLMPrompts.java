package com.alura.alumind.utils;

public class LLMPrompts {
  public static final String SPAM_ANALYSIS_PROMPT = """
      Analyze the following feedback for the AluMind app (a mental health and wellness application)
      and determine if it is legitimate feedback or spam/inappropriate content.

      Feedback to analyze:
      %s

      Consider as spam or inappropriate content:
      1. Promotional messages unrelated to the app
      2. Links to unrelated external websites
      3. Offensive, abusive, or inappropriate content
      4. Nonsensical or automatically generated text
      5. Messages that don't appear to be related to feedback about the app

      Return your analysis in JSON format with the following structure:
      {
        "isSpam": true/false,
        "reason": "Brief explanation of why the feedback is considered legitimate or spam"
      }

      Your response should contain only the JSON, with no additional text.
      """;
  
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

  public static final String WEEKLY_REPORT_PROMPT = """
      You are an assistant specialized in feedback reports for the AluMind application (a mental health and wellness app).

      Generate a weekly feedback report email for AluMind stakeholders, using the following information:

      Report period: %s
      Total feedback received: %d
      Percentage of positive feedback: %.2f%%
      Percentage of negative feedback: %.2f%%

      Top requested features (in JSON format):
      %s

      Email requirements:
      1. Use HTML to format the email professionally and in a visually appealing way
      2. Include a header with the AluMind logo/name
      3. Highlight positive and negative feedback statistics
      4. List the top requested features, explaining why each one is important to users
      5. End with a conclusion that encourages the team to continue improving the app
      6. Use a professional but friendly tone
      7. Respond only with the complete HTML for the email, ready to be sent
      8, Return the email in portuguese
      """;
}