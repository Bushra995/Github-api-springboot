package com.optum.repository;

import java.util.List;

public class GHCodeSearchResult {
    private int total_count;
    private boolean incomplete_results;
    private List<Item> items;

    public int getTotalCount() {
        return total_count;
    }

    public void setTotalCount(int total_count) {
        this.total_count = total_count;
    }

    public boolean getIncompleteResults() {
        return incomplete_results;
    }

    public void setIncompleteResults(boolean incomplete_results) {
        this.incomplete_results = incomplete_results;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}
