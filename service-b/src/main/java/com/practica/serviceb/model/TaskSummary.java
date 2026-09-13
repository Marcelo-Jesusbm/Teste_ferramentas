package com.practica.serviceb.model;

import java.util.Map;

public class TaskSummary {

    private long total;
    private Map<String, Long> countByStatus;

    public TaskSummary() {
    }

    public TaskSummary(long total, Map<String, Long> countByStatus) {
        this.total = total;
        this.countByStatus = countByStatus;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public Map<String, Long> getCountByStatus() {
        return countByStatus;
    }

    public void setCountByStatus(Map<String, Long> countByStatus) {
        this.countByStatus = countByStatus;
    }
}
