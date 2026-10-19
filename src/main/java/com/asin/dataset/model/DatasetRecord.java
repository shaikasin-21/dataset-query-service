package com.asin.dataset.model;

import jakarta.persistence.*;

/** One JSON record belonging to a named dataset. The JSON payload is stored as text. */
@Entity
@Table(name = "dataset_records", indexes = @Index(name = "idx_dataset_name", columnList = "datasetName"))
public class DatasetRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String datasetName;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String jsonData;

    protected DatasetRecord() {}

    public DatasetRecord(String datasetName, String jsonData) {
        this.datasetName = datasetName;
        this.jsonData = jsonData;
    }

    public Long getId() { return id; }
    public String getDatasetName() { return datasetName; }
    public String getJsonData() { return jsonData; }
}
