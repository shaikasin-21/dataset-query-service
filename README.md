# Dataset Query Service

Spring Boot 3 / Java 17 REST API to store JSON records per dataset and query them with dynamic **groupBy** and **sortBy**.
Relational DB: H2 (file) by default, PostgreSQL via env vars. Swagger UI: `/swagger-ui.html`

## Run
```
mvn test
mvn spring-boot:run
```
App starts on http://localhost:8080 (override with `PORT`).

Optional PostgreSQL: set `DB_URL` (e.g. `jdbc:postgresql://host:5432/db`), `DB_USERNAME`, `DB_PASSWORD`.

## APIs
### Insert record - `POST /api/dataset/{datasetName}/record`
```
curl -X POST localhost:8080/api/dataset/employee_dataset/record -H "Content-Type: application/json" -d '{"id":1,"name":"John Doe","age":30,"department":"Engineering"}'
```
`201` -> `{"message":"Record added successfully","dataset":"employee_dataset","recordId":1}`

### Query - `GET /api/dataset/{datasetName}/query`
| Param | Description |
|-------|-------------|
| `groupBy` | field to group by, response `groupedRecords` |
| `sortBy` | field to sort by, response `sortedRecords` |
| `order` | `asc` (default) or `desc`; requires `sortBy` |

- `?groupBy=department`
- `?sortBy=age&order=asc`
- `?groupBy=department&sortBy=age&order=desc` sorts records inside each group
- no params returns all records under `records`

Notes: numbers sort numerically, text case-insensitively; records missing the field sort last or group under `_ungrouped`.

### Errors
`400` invalid dataset name / bad JSON / bad `order`, `404` unknown dataset, `500` unexpected. Body:
`{"timestamp":"...","status":404,"error":"Not Found","message":"Dataset 'x' not found"}`

## Design
Controller -> Service -> Repository. `RecordGrouper` and `RecordSorter` implement a `RecordOperation` strategy interface so new operations can be added without changing the service.

## Live Link
[Click here to view Live Project](https://dataset-query-service.onrender.com/swagger-ui/index.html)

