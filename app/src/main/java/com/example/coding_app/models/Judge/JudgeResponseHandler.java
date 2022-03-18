package com.example.coding_app.models.Judge;

/**
 * Interface to be implemented by any class that needs to process
 * results of Judge code execution
 */
public interface JudgeResponseHandler {
    public void handleJudgeResponse(JudgeData[] response);
}
