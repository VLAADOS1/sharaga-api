package com.lokomanako.hack_api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lokomanako.hack_api.store.repo.GoalRepo;
import java.math.BigDecimal;
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
        String email1 = login + "@mail.test";
        String email2 = "x" + login + "@mail.test";
        mvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(j(regBody(login, "Pass123", email1))))
                .andExpect(status().isCreated());

        mvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(j(regBody(login.toUpperCase(), "Pass123", email2))))
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
    void recoverPasswordByEmailAndAnswers() throws Exception {
        String login = "u" + System.nanoTime();
        String email = login + "@mail.test";
        reg(login, "Pass123", email);

        mvc.perform(get("/api/v1/auth/recovery/questions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.question1").isNotEmpty())
                .andExpect(jsonPath("$.question2").isNotEmpty())
                .andExpect(jsonPath("$.question3").isNotEmpty());

        mvc.perform(post("/api/v1/auth/recover-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(j(Map.of(
                                "email", email,
                                "securityAnswer1", "Барсик",
                                "securityAnswer2", "Иван",
                                "securityAnswer3", "Синий",
                                "newPassword", "NewPass123",
                                "newPasswordConfirm", "NewPass123"
                        ))))
                .andExpect(status().isNoContent());

        mvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(j(Map.of(
                                "login", login,
                                "password", "Pass123"
                        ))))
                .andExpect(status().isUnauthorized());

        mvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(j(Map.of(
                                "login", login,
                                "password", "NewPass123"
                        ))))
                .andExpect(status().isOk());
    }

    @Test
    void recoverPasswordFailOnWrongAnswers() throws Exception {
        String login = "u" + System.nanoTime();
        String email = login + "@mail.test";
        reg(login, "Pass123", email);

        mvc.perform(post("/api/v1/auth/recover-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(j(Map.of(
                                "email", email,
                                "securityAnswer1", "wrong",
                                "securityAnswer2", "Иван",
                                "securityAnswer3", "Синий",
                                "newPassword", "NewPass123",
                                "newPasswordConfirm", "NewPass123"
                        ))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error.code").value("AUTH_RECOVERY_FAIL"));
    }

    @Test
    void allowPastTx() throws Exception {
        AuthData a = reg("u" + System.nanoTime(), "Pass123");
        UUID catId = addCat(a.token, "EXP", "Meal", "#11AA22");
        UUID goalId = addGoal(a.token, "Trip", 1000);

        String y = LocalDate.now(ZoneId.of("UTC")).minusDays(1).toString();
        mvc.perform(post("/api/v1/transactions")
                        .header("Authorization", "Bearer " + a.token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(j(Map.of(
                                "type", "exp",
                                "sum", 10.5,
                                "note", "x",
                                "catId", catId,
                                "goalId", goalId,
                                "date", y
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.date").value(y));
    }

    @Test
    void goalRequiredForTx() throws Exception {
        AuthData a = reg("u" + System.nanoTime(), "Pass123");
        UUID catId = addCat(a.token, "EXP", "Meal", "#11AA22");
        String d = LocalDate.now(ZoneId.of("UTC")).toString();

        mvc.perform(post("/api/v1/transactions")
                        .header("Authorization", "Bearer " + a.token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(j(Map.of(
                                "type", "exp",
                                "sum", 10.5,
                                "note", "x",
                                "catId", catId,
                                "date", d
                        ))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("VALIDATION_ERR"));
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
        UUID goalId = addGoal(a.token, "Car", 50000);

        String d = LocalDate.now(ZoneId.of("UTC")).toString();
        mvc.perform(post("/api/v1/transactions")
                        .header("Authorization", "Bearer " + a.token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(j(Map.of(
                                "type", "exp",
                                "sum", 20.0,
                                "note", "ride",
                                "catId", catId,
                                "goalId", goalId,
                                "date", d
                        ))))
                .andExpect(status().isCreated());

        mvc.perform(delete("/api/v1/categories/{id}", catId)
                        .header("Authorization", "Bearer " + a.token))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error.code").value("CAT_IN_USE"));
    }

    @Test
    void manyGoalsSupported() throws Exception {
        AuthData a = reg("u" + System.nanoTime(), "Pass123");

        mvc.perform(post("/api/v1/goal")
                        .header("Authorization", "Bearer " + a.token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(j(Map.of("name", "A", "target", 100))))
                .andExpect(status().isBadRequest());

        UUID g1 = addGoal(a.token, "Goal 1", 100);
        UUID g2 = addGoal(a.token, "Goal 2", 200);

        mvc.perform(get("/api/v1/goal")
                        .header("Authorization", "Bearer " + a.token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        mvc.perform(patch("/api/v1/goal/{id}", g2)
                        .header("Authorization", "Bearer " + a.token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(j(Map.of("name", "Goal 2 Updated", "target", 250))))
                .andExpect(status().isOk());

        mvc.perform(delete("/api/v1/goal/{id}", g1)
                        .header("Authorization", "Bearer " + a.token))
                .andExpect(status().isNoContent());

        mvc.perform(get("/api/v1/goal")
                        .header("Authorization", "Bearer " + a.token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(g2.toString()))
                .andExpect(jsonPath("$[0].name").value("Goal 2 Updated"));

        MvcResult me = mvc.perform(get("/api/v1/auth/me")
                        .header("Authorization", "Bearer " + a.token))
                .andExpect(status().isOk())
                .andReturn();
        String uid = om.readTree(me.getResponse().getContentAsString()).path("id").asText();
        assertThat(goalRepo.countByUsr_Id(UUID.fromString(uid))).isEqualTo(1);
    }

    @Test
    void txUpdatesSelectedGoalBalance() throws Exception {
        AuthData a = reg("u" + System.nanoTime(), "Pass123");
        UUID incCat = addCat(a.token, "INC", "Salary", "#11AA11");
        UUID expCat = addCat(a.token, "EXP", "Food", "#AA1111");
        UUID g1 = addGoal(a.token, "Flat", 5000000);
        UUID g2 = addGoal(a.token, "Vacation", 200000);
        String d = LocalDate.now(ZoneId.of("UTC")).toString();

        mvc.perform(post("/api/v1/transactions")
                        .header("Authorization", "Bearer " + a.token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(j(Map.of(
                                "type", "inc",
                                "sum", 100.0,
                                "note", "salary",
                                "catId", incCat,
                                "goalId", g1,
                                "date", d
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.goalId").value(g1.toString()));

        mvc.perform(post("/api/v1/transactions")
                        .header("Authorization", "Bearer " + a.token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(j(Map.of(
                                "type", "exp",
                                "sum", 25.0,
                                "note", "lunch",
                                "catId", expCat,
                                "goalId", g2,
                                "date", d
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.goalId").value(g2.toString()));

        assertThat(goalCurrent(a.token, g1)).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(goalCurrent(a.token, g2)).isEqualByComparingTo(new BigDecimal("-25.00"));
    }

    private AuthData reg(String login, String pass) throws Exception {
        return reg(login, pass, login + "@mail.test");
    }

    private AuthData reg(String login, String pass, String email) throws Exception {
        MvcResult r = mvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(j(regBody(login, pass, email))))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode n = om.readTree(r.getResponse().getContentAsString());
        return new AuthData(n.path("accessToken").asText());
    }

    private Map<String, Object> regBody(String login, String pass, String email) {
        return Map.of(
                "login", login,
                "email", email,
                "password", pass,
                "passwordConfirm", pass,
                "securityAnswer1", "Барсик",
                "securityAnswer2", "Иван",
                "securityAnswer3", "Синий"
        );
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

    private UUID addGoal(String token, String name, int target) throws Exception {
        MvcResult r = mvc.perform(post("/api/v1/goal")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(j(Map.of(
                                "name", name,
                                "target", target
                        ))))
                .andExpect(status().isCreated())
                .andReturn();
        return UUID.fromString(om.readTree(r.getResponse().getContentAsString()).path("id").asText());
    }

    private BigDecimal goalCurrent(String token, UUID goalId) throws Exception {
        MvcResult r = mvc.perform(get("/api/v1/goal")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode list = om.readTree(r.getResponse().getContentAsString());
        for (JsonNode n : list) {
            if (goalId.toString().equals(n.path("id").asText())) {
                return n.path("current").decimalValue();
            }
        }
        throw new IllegalStateException("Goal not found in response");
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
