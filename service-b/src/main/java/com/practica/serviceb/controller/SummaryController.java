package com.practica.serviceb.controller;

import com.practica.serviceb.model.TaskSummary;
import com.practica.serviceb.service.SummaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/summary")
public class SummaryController {

    private static final Logger log = LoggerFactory.getLogger(SummaryController.class);

    private final SummaryService summaryService;

    @Autowired
    public SummaryController(SummaryService summaryService) {
        this.summaryService = summaryService;
    }

    @GetMapping
    public TaskSummary getSummary() {
        log.info("Calculando resumo de tarefas a partir do service-a");
        TaskSummary summary = summaryService.buildSummary();
        log.info("Resumo calculado: total={} porStatus={}", summary.getTotal(), summary.getCountByStatus());
        return summary;
    }
}
