package com.asin.dataset.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.asin.dataset.repository.DatasetRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1")
class DatasetControllerIntegrationTest {

    @Autowired MockMvc mvc;
    @Autowired DatasetRecordRepository repository;

    @BeforeEach
    void clean() throws Exception {
        repository.deleteAll();
        insert("employees", "{\"id\":1,\"name\":\"John Doe\",\"age\":30,\"department\":\"Engineering\"}");
        insert("employees", "{\"id\":2,\"name\":\"Jane Smith\",\"age\":25,\"department\":\"Engineering\"}");
        insert("employees", "{\"id\":3,\"name\":\"Alice Brown\",\"age\":28,\"department\":\"Marketing\"}");
    }

    private void insert(String dataset, String json) throws Exception {
        mvc.perform(post("/api/dataset/{d}/record", dataset)
                .contentType(MediaType.APPLICATION_JSON).content(json));
    }

    @Test
    void insertReturnsCreatedWithRecordId() throws Exception {
        mvc.perform(post("/api/dataset/{d}/record", "staff")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":7,\"name\":\"Sam\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Record added successfully"))
                .andExpect(jsonPath("$.dataset").value("staff"))
                .andExpect(jsonPath("$.recordId").value(7));
    }

    @Test
    void insertRejectsEmptyObject() throws Exception {
        mvc.perform(post("/api/dataset/{d}/record", "staff")
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void insertRejectsMalformedJson() throws Exception {
        mvc.perform(post("/api/dataset/{d}/record", "staff")
                        .contentType(MediaType.APPLICATION_JSON).content("{not json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void insertRejectsInvalidDatasetName() throws Exception {
        mvc.perform(post("/api/dataset/{d}/record", "bad name!")
                        .contentType(MediaType.APPLICATION_JSON).content("{\"a\":1}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void groupByDepartment() throws Exception {
        mvc.perform(get("/api/dataset/employees/query").param("groupBy", "department"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.groupedRecords.Engineering", hasSize(2)))
                .andExpect(jsonPath("$.groupedRecords.Marketing", hasSize(1)));
    }

    @Test
    void sortByAgeAscending() throws Exception {
        mvc.perform(get("/api/dataset/employees/query").param("sortBy", "age").param("order", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sortedRecords[0].age").value(25))
                .andExpect(jsonPath("$.sortedRecords[2].age").value(30));
    }

    @Test
    void sortByAgeDescending() throws Exception {
        mvc.perform(get("/api/dataset/employees/query").param("sortBy", "age").param("order", "desc"))
                .andExpect(jsonPath("$.sortedRecords[0].age").value(30));
    }

    @Test
    void groupByAndSortByTogetherSortsInsideEachGroup() throws Exception {
        mvc.perform(get("/api/dataset/employees/query")
                        .param("groupBy", "department").param("sortBy", "age").param("order", "asc"))
                .andExpect(jsonPath("$.groupedRecords.Engineering[0].age").value(25))
                .andExpect(jsonPath("$.groupedRecords.Engineering[1].age").value(30));
    }

    @Test
    void queryWithoutOperatorsReturnsAllRecords() throws Exception {
        mvc.perform(get("/api/dataset/employees/query"))
                .andExpect(jsonPath("$.records", hasSize(3)));
    }

    @Test
    void unknownDatasetReturnsNotFound() throws Exception {
        mvc.perform(get("/api/dataset/ghost/query").param("groupBy", "x"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void invalidOrderReturnsBadRequest() throws Exception {
        mvc.perform(get("/api/dataset/employees/query").param("sortBy", "age").param("order", "up"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void orderWithoutSortByReturnsBadRequest() throws Exception {
        mvc.perform(get("/api/dataset/employees/query").param("order", "asc"))
                .andExpect(status().isBadRequest());
    }
}
