package com.TaskManager.storage;

import com.TaskManager.model.Task;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TaskStorage {
    private static final String FILE_PATH = "tasks.json";
    private final ObjectMapper objectMapper;

    public TaskStorage() {
        this.objectMapper = new ObjectMapper();
        // Register JavaTimeModule to handle LocalDate serialization properly
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    // Save tasks to tasks.json file
    public void saveTasks(List<Task> tasks) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), tasks);
        } catch (IOException e) {
            System.err.println("Error saving tasks: " + e.getMessage());
        }
    }

    // Load tasks from tasks.json file
    public List<Task> loadTasks() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return new ArrayList<>(); // Return empty list if file doesn't exist yet
        }
        try {
            return objectMapper.readValue(file, new TypeReference<List<Task>>() {});
        } catch (IOException e) {
            System.err.println("Error loading tasks, starting with an empty list: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}