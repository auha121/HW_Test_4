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

@Story("Image Favorite API tests")

public class ImageFavoriteTests extends BaseTest {
    static String encodedFile;
    String deleteHashImage;
    String imageId;
    Response response;
    private MultiPartSpecification base64MultiPartSpec;
    private RequestSpecification requestSpecificationWithAuthWithBase64;

    @BeforeEach
    void setUp() {
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

    @DisplayName("Файл в избранные")
    @Test
    void addFavoriteFileTest() {
        given(requestSpecificationWithAuthWithBase64, positiveResponseSpecification)
                .post(GET_ACCOUNT_FAVORITE, imageId)
                .prettyPeek();
    }

    @DisplayName("Удаленный файл в избранные")
    @Test
    void addFavoriteDelFileTest() {
        given(requestSpecificationWithAuth)
                .delete(GET_ACCOUNT_DEL, deleteHashImage)
                .prettyPeek()
                .then()
                .statusCode(200);
        given(requestSpecificationWithAuth)
                .post(GET_ACCOUNT_FAVORITE, imageId)
                .prettyPeek()
                .then()
                .statusCode(404);
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
