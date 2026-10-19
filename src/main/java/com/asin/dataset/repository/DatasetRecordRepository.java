package com.asin.dataset.repository;

import com.asin.dataset.model.DatasetRecord;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DatasetRecordRepository extends JpaRepository<DatasetRecord, Long> {
    List<DatasetRecord> findByDatasetNameOrderByIdAsc(String datasetName);
}
