package com.practica.serviceb.service;

import com.practica.serviceb.model.Task;
import com.practica.serviceb.model.TaskSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SummaryService {

    private final TaskClientService taskClientService;

    @Autowired
    public SummaryService(TaskClientService taskClientService) {
        this.taskClientService = taskClientService;
    }

    public TaskSummary buildSummary() {
        List<Task> tasks = taskClientService.fetchAllTasks();

        Map<String, Long> countByStatus = tasks.stream()
                .collect(Collectors.groupingBy(Task::getStatus, Collectors.counting()));

        return new TaskSummary(tasks.size(), countByStatus);
    }
}
