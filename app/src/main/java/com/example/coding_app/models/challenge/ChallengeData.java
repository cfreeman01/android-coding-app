package com.example.coding_app.models.challenge;

import java.util.HashMap;

/**
 * Contains serializable data for a coding challenge
 */
public class ChallengeData {
    public boolean completed;
    public String description_html;
    public String name;
    public String difficulty_level;
    public HashMap<String, String> solutions;
    public TestCase[] test_cases;
}
