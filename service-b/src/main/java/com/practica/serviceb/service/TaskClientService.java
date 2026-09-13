package com.practica.serviceb.service;

import com.practica.serviceb.model.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class TaskClientService {

    private final RestTemplate restTemplate;

    @Value("${service-a.url}")
    private String serviceAUrl;

    @Autowired
    public TaskClientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Task> fetchAllTasks() {
        Task[] tasks = restTemplate.getForObject(serviceAUrl + "/tasks", Task[].class);
        return tasks == null ? List.of() : List.of(tasks);
    }
}
