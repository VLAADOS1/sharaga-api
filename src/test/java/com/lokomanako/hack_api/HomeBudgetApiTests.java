package com.lokomanako.hack_api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lokomanako.hack_api.store.repo.GoalRepo;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class HomeBudgetApiTests {

    @Autowired
    private MockMvc mvc;

    private final ObjectMapper om = new ObjectMapper();

    @Autowired
    private GoalRepo goalRepo;

    @Test
    void noDupLogin() throws Exception {
        String login = "u" + System.nanoTime();
        mvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(j(Map.of(
                                "login", login,
                                "password", "Pass123",
                                "passwordConfirm", "Pass123"
                        ))))
                .andExpect(status().isCreated());

        mvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(j(Map.of(
                                "login", login.toUpperCase(),
                                "password", "Pass123",
                                "passwordConfirm", "Pass123"
                        ))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error.code").value("AUTH_LOGIN_EXISTS"));
    }

    @Test
    void noLoginAfter5Fail() throws Exception {
        String login = "u" + System.nanoTime();
        reg(login, "Pass123");

        for (int i = 0; i < 5; i++) {
            mvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(j(Map.of(
                                    "login", login,
                                    "password", "Fail123"
                            ))))
                    .andExpect(status().isUnauthorized());
        }

        mvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(j(Map.of(
                                "login", login,
                                "password", "Pass123"
                        ))))
                .andExpect(status().isLocked())
                .andExpect(jsonPath("$.error.code").value("AUTH_LOCKED"));
    }

    @Test
    void allowPastTx() throws Exception {
        AuthData a = reg("u" + System.nanoTime(), "Pass123");
        UUID catId = addCat(a.token, "EXP", "Meal", "#11AA22");

        String y = LocalDate.now(ZoneId.of("UTC")).minusDays(1).toString();
        mvc.perform(post("/api/v1/transactions")
                        .header("Authorization", "Bearer " + a.token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(j(Map.of(
                                "type", "exp",
                                "sum", 10.5,
                                "note", "x",
                                "catId", catId,
                                "date", y
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.date").value(y));
    }

    @Test
    void allowPastRec() throws Exception {
        AuthData a = reg("u" + System.nanoTime(), "Pass123");
        UUID catId = addCat(a.token, "EXP", "Gym", "#22AA44");

        String y = LocalDate.now(ZoneId.of("UTC")).minusDays(1).toString();
        mvc.perform(post("/api/v1/recurring")
                        .header("Authorization", "Bearer " + a.token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(j(Map.of(
                                "type", "exp",
                                "sum", 15.0,
                                "note", "sub",
                                "catId", catId,
                                "freq", "month",
                                "startDate", y
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.startDate").value(y))
                .andExpect(jsonPath("$.nextDate").value(y));
    }

    @Test
    void allowDayAndYearRec() throws Exception {
        AuthData a = reg("u" + System.nanoTime(), "Pass123");
        String nm = "Trip" + (System.nanoTime() % 100000);
        UUID catId = addCat(a.token, "EXP", nm, "#3388AA");
        String d = LocalDate.now(ZoneId.of("UTC")).toString();

        mvc.perform(post("/api/v1/recurring")
                        .header("Authorization", "Bearer " + a.token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(j(Map.of(
                                "type", "exp",
                                "sum", 12.0,
                                "note", "d",
                                "catId", catId,
                                "freq", "day",
                                "startDate", d
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.freq").value("day"));

        mvc.perform(post("/api/v1/recurring")
                        .header("Authorization", "Bearer " + a.token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(j(Map.of(
                                "type", "exp",
                                "sum", 13.0,
                                "note", "y",
                                "catId", catId,
                                "freq", "year",
                                "startDate", d
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.freq").value("year"));
    }

    @Test
    void noForeignAccess() throws Exception {
        AuthData a1 = reg("u" + System.nanoTime(), "Pass123");
        AuthData a2 = reg("u" + (System.nanoTime() + 1), "Pass123");
        UUID catId = addCat(a1.token, "EXP", "Rent", "#112233");

        mvc.perform(get("/api/v1/categories")
                        .header("Authorization", "Bearer " + a2.token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(14));

        mvc.perform(patch("/api/v1/categories/{id}", catId)
                        .header("Authorization", "Bearer " + a2.token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(j(Map.of("name", "Other"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("CAT_NOT_FOUND"));
    }

    @Test
    void hasDefaultsAndCanDel() throws Exception {
        AuthData a = reg("u" + System.nanoTime(), "Pass123");

        MvcResult list1 = mvc.perform(get("/api/v1/categories")
                        .header("Authorization", "Bearer " + a.token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(14))
                .andReturn();

        JsonNode n1 = om.readTree(list1.getResponse().getContentAsString());
        String id = n1.path("items").get(0).path("id").asText();

        mvc.perform(delete("/api/v1/categories/{id}", id)
                        .header("Authorization", "Bearer " + a.token))
                .andExpect(status().isNoContent());

        mvc.perform(get("/api/v1/categories")
                        .header("Authorization", "Bearer " + a.token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(13));
    }

    @Test
    void noDelUsedCat() throws Exception {
        AuthData a = reg("u" + System.nanoTime(), "Pass123");
        UUID catId = addCat(a.token, "EXP", "Taxi", "#445566");

        String d = LocalDate.now(ZoneId.of("UTC")).toString();
        mvc.perform(post("/api/v1/transactions")
                        .header("Authorization", "Bearer " + a.token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(j(Map.of(
                                "type", "exp",
                                "sum", 20.0,
                                "note", "ride",
                                "catId", catId,
                                "date", d
                        ))))
                .andExpect(status().isCreated());

        mvc.perform(delete("/api/v1/categories/{id}", catId)
                        .header("Authorization", "Bearer " + a.token))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error.code").value("CAT_IN_USE"));
    }

    @Test
    void oneGoalOnly() throws Exception {
        AuthData a = reg("u" + System.nanoTime(), "Pass123");

        mvc.perform(put("/api/v1/goal")
                        .header("Authorization", "Bearer " + a.token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(j(Map.of("name", "A", "target", 100))))
                .andExpect(status().isBadRequest());

        mvc.perform(put("/api/v1/goal")
                        .header("Authorization", "Bearer " + a.token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(j(Map.of("name", "Goal 1", "target", 100))))
                .andExpect(status().isOk());

        mvc.perform(put("/api/v1/goal")
                        .header("Authorization", "Bearer " + a.token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(j(Map.of("name", "Goal 2", "target", 200))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Goal 2"));

        MvcResult me = mvc.perform(get("/api/v1/auth/me")
                        .header("Authorization", "Bearer " + a.token))
                .andExpect(status().isOk())
                .andReturn();
        String uid = om.readTree(me.getResponse().getContentAsString()).path("id").asText();
        assertThat(goalRepo.countByUsr_Id(UUID.fromString(uid))).isEqualTo(1);
    }

    private AuthData reg(String login, String pass) throws Exception {
        MvcResult r = mvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(j(Map.of(
                                "login", login,
                                "password", pass,
                                "passwordConfirm", pass
                        ))))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode n = om.readTree(r.getResponse().getContentAsString());
        return new AuthData(n.path("accessToken").asText());
    }

    private UUID addCat(String token, String kind, String name, String color) throws Exception {
        MvcResult r = mvc.perform(post("/api/v1/categories")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(j(Map.of(
                                "kind", kind.toLowerCase(),
                                "name", name,
                                "color", color
                        ))))
                .andExpect(status().isCreated())
                .andReturn();
        return UUID.fromString(om.readTree(r.getResponse().getContentAsString()).path("id").asText());
    }

    private String j(Object v) throws Exception {
        return om.writeValueAsString(v);
    }

    private static class AuthData {

        private final String token;

        private AuthData(String token) {
            this.token = token;
        }
    }
}
