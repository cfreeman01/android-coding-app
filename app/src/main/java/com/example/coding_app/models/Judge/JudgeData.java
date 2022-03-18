package com.example.coding_app.models.Judge;

/**
 * Parameters sent and received by judge
 */
public class JudgeData {
    public String source_code = "";
    public int language_id = 0;
    public String stdin = "";
    public String expected_output = "";
    public String stdout = "";
    public String stderr = "";
    public String compile_output = "";
    public String message = "";
    public int exit_code = 0;
    public int exit_signal = 0;
    public int status_id = 0;
    public String token = "";
}
