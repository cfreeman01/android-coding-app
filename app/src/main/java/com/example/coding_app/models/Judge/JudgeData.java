package com.example.coding_app.models.Judge;

public class JudgeData {
    public String source_code;
    public int language_id;
    public String compiler_options;
    public String command_line_arguments;
    public String stdin;
    public String expected_output;
    public float cpu_time_limit;
    public float cpu_extra_time;
    public float wall_time_limit;
    public float memory_limit;
    public int stack_limit;
    public int max_processes_and_or_threads;
    public boolean enable_per_process_and_thread_time_limit;
    public boolean enable_per_process_and_thread_memory_limit;
    public int max_file_size;
    public boolean redirect_stderr_to_stdout;
    public boolean enable_network;
    public int number_of_runs;
    public String additional_files;
    public String callback_url;
    public String stdout;
    public String stderr;
    public String compile_output;
    public String message;
    public int exit_code;
    public int exit_signal;
    public int status_id;
    public String token;
    public float time;
    public float wall_time;
    public float memory;
}
