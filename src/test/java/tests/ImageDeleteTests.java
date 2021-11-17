package tests;

import io.qameta.allure.Story;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.MultiPartSpecification;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tests.dto.ImageResponse;
import java.util.Base64;

import static io.restassured.RestAssured.given;
import static tests.Endpoints.UPLOAD_IMAGE;
import static tests.Endpoints.GET_ACCOUNT_DEL;
import static tests.Endpoints.PATH_TO_IMAGE;

@Story("Image Delete API tests")

public class ImageDeleteTests extends BaseTest {
    static String encodedFile;
    String deleteHashImage;
    private MultiPartSpecification base64MultiPartSpec;
    private RequestSpecification requestSpecificationWithAuthWithBase64;

    @BeforeEach
    void setUpDel() {
        byte[] byteArray = getFileContent(PATH_TO_IMAGE);
        encodedFile = Base64.getEncoder().encodeToString(byteArray);

        base64MultiPartSpec = new MultiPartSpecBuilder(encodedFile)
                .controlName("image")
                .build();

        requestSpecificationWithAuthWithBase64 = new RequestSpecBuilder()
                .addHeader("Authorization", token)
                .addMultiPart(base64MultiPartSpec)
                .build();

        deleteHashImage = given(requestSpecificationWithAuthWithBase64, positiveResponseSpecification)
                .post(UPLOAD_IMAGE)
                .prettyPeek()
                .then()
                .extract()
                .body()
                .as(ImageResponse.class)
                .getData().getDeletehash();
    }

    @DisplayName("Удаление файла с авторизацией")
    @Test
    void deleteFileImageTest() {
        given(requestSpecificationWithAuth)
                .when()
                .delete(GET_ACCOUNT_DEL, deleteHashImage)
                .prettyPeek()
                .then()
                .statusCode(200);
    }

    @DisplayName("Удаление файла без авторизации")
    @Test
    void deleteFileUnAuthorizeTest() {
        given()
                .when()
                .delete(GET_ACCOUNT_DEL, deleteHashImage)
                .prettyPeek()
                .then()
                .statusCode(401);
    }

    @DisplayName("Удаление файла без файла")
    @Test
    void deleteFileWithoutImageTest() {
        given(requestSpecificationWithAuth, negativeNoFileResponseSpecification)
                .delete(GET_ACCOUNT_DEL, "")
                .prettyPeek();
    }
}