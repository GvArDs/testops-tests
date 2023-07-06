package tests;

import com.codeborne.selenide.Configuration;
import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import models.CreateTestBody;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byName;
import static com.codeborne.selenide.Selenide.*;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class CreateTestcasseTests {

    static String login = "allure8",
            password = "allure8",
            projectId = "2220";

    @BeforeAll
    static void setUp() {
        Configuration.baseUrl = "https://allure.autotests.cloud";
        Configuration.holdBrowserOpen = true;

        RestAssured.baseURI = "https://allure.autotests.cloud";
    }

    @Test
    void createWitUIOnlyTest() {
        Faker faker = new Faker();
        String testCaseName = faker.name().fullName();

        step("Authorize", () -> {
            open("/login");
            $(byName("username")).setValue(login);
            $(byName("password")).setValue(password);
            $("button[type='submit']").click();
            sleep(1000);
        });
        step("Go to project", () -> {
            open("/project/2220/test-cases");
        });

        step("Create testcase", () -> {
            $("[data-testid=input__create_test_case]").setValue(testCaseName)
                    .pressEnter();
        });

        step("Verify testcase name", () -> {
            $(".LoadableTree__view").shouldHave(text(testCaseName));
            System.out.println(testCaseName);
        });
    }

    @Test
    void createWitApiOnlyTest() {
        Faker faker = new Faker();
        String testCaseName = faker.name().fullName();

//        step("Authorize", () -> {
//            open("/login");
//            $(byName("username")).setValue(login);
//            $(byName("password")).setValue(password);
//            $("button[type='submit']").click();
//            sleep(1000);
//        });
//        step("Go to project", () -> {
//            open("/project/2220/test-cases");
//        });

        step("Create testcase", () -> {

            CreateTestBody testCaseBody = new CreateTestBody();
            testCaseBody.setName(testCaseName);

            given()
                    .log().all()
                    .header("X-XSRF-TOKEN", "2569fd76-1caf-46b1-8fc8-3b4254a31a7c")
                    .cookies("XSRF-TOKEN", "2569fd76-1caf-46b1-8fc8-3b4254a31a7c;",
                            "ALLURE_TESTOPS_SESSION", "2d59945c-3808-4337-9cbb-dc20f0c42cea;")
                    .contentType("application/json;charset=UTF-8")
                    .body(testCaseBody)
                    .queryParam("projectId", projectId)
                    .when()
                    .post("/api/rs/testcasetree/leaf")
                    .then()
                    .log().status()
                    .log().body()
                    .statusCode(200)
                    .body("statusName", is("Draft"))
                    .body("name", is(testCaseName));
        });

//        step("Verify testcase name", () -> {
//            $(".LoadableTree__view").shouldHave(text(testCaseName));
//            System.out.println(testCaseName);
//        });
    }
}