package tests;

import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static tests.Endpoints.GET_ACCOUNT;

@Story("Account API tests")

public class AccountTests extends BaseTest{

    @DisplayName("Авторизация в API")
    @Test
    void getAccountInfoTest() {
        given(requestSpecificationWithAuth)
                .when()
                .get(GET_ACCOUNT, username)
                .then()
                .statusCode(200);
    }

    @DisplayName("Проверка полей success, status, ContentType")
    @Test
    void getAccountInfoWithAssertionsInGivenTest() {
        given(requestSpecificationWithAuth, positiveResponseSpecification)
                .get(GET_ACCOUNT, username)
                .prettyPeek();
    }

    @Test
    void getAccountInfoWithAssertionsAfterTest() {
        Response response = given(requestSpecificationWithAuth)
                .log()
                .method()
                .log()
                .uri()
                .when()
                .get(GET_ACCOUNT, username)
                .prettyPeek();
        assertThat(response.jsonPath().get("data.url"), equalTo(username));
    }

    @DisplayName("Авторизация в API без авторизации")
    @Test
    void getAccountWithoutAuthInfoTest() {
        given()
                .expect()
                .statusCode(401)
                .when()
                .get(GET_ACCOUNT, username)
                .prettyPeek()
                .then()
                .statusCode(401);
    }
}