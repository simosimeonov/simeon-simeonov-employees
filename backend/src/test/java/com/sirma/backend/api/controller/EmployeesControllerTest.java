package com.sirma.backend.api.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class EmployeesControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private JsonNode upload(String resource) throws Exception {
        byte[] bytes = new ClassPathResource("csv/" + resource).getInputStream().readAllBytes();
        MockMultipartFile file = new MockMultipartFile("file", resource, "text/csv", bytes);
        MvcResult result = mockMvc.perform(multipart("/api/employees/upload").file(file))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }

    @Test
    void parse_3_valid_employees() throws Exception {
        JsonNode json = upload("valid-employees.csv");
        assertThat(json.get("invalidRows")).isEmpty();
        assertThat(json.get("stats").get("validRows").asLong()).isEqualTo(3L);
        assertThat(json.get("topPairs")).isEmpty();
    }

    @Test
    void parse_4_invalid_employees() throws Exception {
        JsonNode json = upload("invalid-employees.csv");
        assertThat(json.get("invalidRows")).hasSize(4);
        assertThat(json.get("stats").get("validRows").asLong()).isEqualTo(1L);
    }

    @Test
    void parse_multi_date_format_employees() throws Exception {
        JsonNode json = upload("multi-date-format.csv");
        assertThat(json.get("invalidRows")).isEmpty();
        assertThat(json.get("stats").get("validRows").asLong()).isEqualTo(4L);
    }

    @Test
    void parse_2_winners() throws Exception {
        JsonNode json = upload("multiple-winners.csv");
        JsonNode topPairs = json.get("topPairs");
        assertThat(topPairs).hasSize(2);
        long days0 = topPairs.get(0).get("totalDays").asLong();
        long days1 = topPairs.get(1).get("totalDays").asLong();
        assertThat(days0).isEqualTo(days1);
    }

}
