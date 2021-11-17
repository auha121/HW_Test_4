package tests;

import io.qameta.allure.Story;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.MultiPartSpecification;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tests.dto.ImageResponse;

import java.util.Base64;

import static io.restassured.RestAssured.given;
import static tests.Endpoints.*;

@Story("Image Update API tests")

public class ImageUpdateTests extends BaseTest {
    static String encodedFile;
    String deleteHashImage;
    String imageId;
    Response response;
    private MultiPartSpecification base64MultiPartSpec;
    private RequestSpecification requestSpecificationWithAuthWithBase64;

    @BeforeEach
    void setUpUp() {
        byte[] byteArray = getFileContent(PATH_TO_IMAGE);
        encodedFile = Base64.getEncoder().encodeToString(byteArray);

        base64MultiPartSpec = new MultiPartSpecBuilder(encodedFile)
                .controlName("image")
                .build();

        requestSpecificationWithAuthWithBase64 = new RequestSpecBuilder()
                .addHeader("Authorization", token)
                .addMultiPart(base64MultiPartSpec)
                .build();

        response = given(requestSpecificationWithAuthWithBase64, positiveResponseSpecification)
                .post(UPLOAD_IMAGE)
                .prettyPeek()
                .then()
                .extract()
                .response();

        imageId = response.as(ImageResponse.class).getData().getId();
        deleteHashImage = response.as(ImageResponse.class).getData().getDeletehash();
    }

    @DisplayName("Изменение title с авторизацией")
    @Test
    void updateFileTitleAuthTest() {
        given(requestSpecificationWithAuth)
                .param("title", "Heart")
                .expect()
                .statusCode(200)
                .when()
                .post(GET_ACCOUNT_ID, imageId)
                .prettyPeek();
    }

    @DisplayName("Изменение title без авторизации")
    @Test
    void updateFileTitleNoAuthTest() {
        given()
                .param("title", "Heart")
                .expect()
                .statusCode(401)
                .when()
                .post(GET_ACCOUNT_ID, imageId)
                .prettyPeek();
    }

    @DisplayName("Изменение description с авторизацией")
    @Test
    void updateFileDescriptionAuthTest() {
        given(requestSpecificationWithAuth)
                .param("description", "This is an image of a heart outline.")
                .expect()
                .statusCode(200)
                .when()
                .post(GET_ACCOUNT_ID, imageId)
                .prettyPeek();
    }

    @DisplayName("Изменение description без авторизации")
    @Test
    void updateFileDescriptionNoAuthTest() {
        given()
                .param("description", "This is an image of a heart outline.")
                .expect()
                .statusCode(401)
                .when()
                .post(GET_ACCOUNT_ID, imageId)
                .prettyPeek();
    }

    @DisplayName("Изменение name с авторизацией")
    @Test
    void updateFileNameAuthTest() {
        given(requestSpecificationWithAuth)
                .param("name", "NewName")
                .expect()
                .statusCode(200)
                .when()
                .post(GET_ACCOUNT_ID, imageId)
                .prettyPeek();
    }

    @DisplayName("Изменение name без авторизации")
    @Test
    void updateFileNameNoAuthTest() {
        given()
                .param("name", "NewName")
                .expect()
                .statusCode(401)
                .when()
                .post(GET_ACCOUNT_ID, imageId)
                .prettyPeek();
    }

    @AfterEach
    void tearDown() {
        if (deleteHashImage != null)
        {
            given(requestSpecificationWithAuth)
                    .when()
                    .delete(GET_ACCOUNT_DEL, deleteHashImage)
                    .prettyPeek()
                    .then()
                    .statusCode(200);
        }
    }
}
