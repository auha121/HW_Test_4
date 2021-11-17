package tests;

import io.qameta.allure.Story;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.MultiPartSpecification;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tests.dto.ImageResponse;
import java.io.File;
import java.util.Base64;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;
import static tests.Endpoints.*;

@Story("Image Upload API tests")

public class ImageUploadTests extends BaseTest {
    static String encodedFile;
    String deleteHashImage;
    MultiPartSpecification base64MultiPartSpec;
    MultiPartSpecification multiPartSpecWithFile;
    static RequestSpecification requestSpecificationWithAuthAndMultipartImage;
    static RequestSpecification requestSpecificationWithAuthWithBase64;

    @BeforeEach
    void beforeTest() {
        byte[] byteArray = getFileContent(PATH_TO_IMAGE);
        encodedFile = Base64.getEncoder().encodeToString(byteArray);

        base64MultiPartSpec = new MultiPartSpecBuilder(encodedFile)
                .controlName("image")
                .build();

        multiPartSpecWithFile = new MultiPartSpecBuilder(new File("src/test/resources/luca_02.jpeg"))
                .controlName("image")
                .build();

        requestSpecificationWithAuthAndMultipartImage = new RequestSpecBuilder()
                .addHeader("Authorization", token)
                .addFormParam("title", "Picture")
                .addFormParam("type", "gif")
                .addMultiPart(multiPartSpecWithFile)
                .build();

        requestSpecificationWithAuthWithBase64 = new RequestSpecBuilder()
                .addHeader("Authorization", token)
                .addMultiPart(base64MultiPartSpec)
                .build();
    }

    @DisplayName("Загрузка файла в формате base64")
    @Test
    void uploadFileTest() {
        deleteHashImage = given(requestSpecificationWithAuthWithBase64, positiveResponseSpecification)
                .post(UPLOAD_IMAGE)
                .prettyPeek()
                .then()
                .extract()
                .response()
                .body()
                .as(ImageResponse.class)
                .getData().getDeletehash();
    }

    @DisplayName("Загрузка файла в формате jpg")
    @Test
    void uploadFileImageTest() {
        deleteHashImage = given(requestSpecificationWithAuthAndMultipartImage)
                .post(UPLOAD_IMAGE)
                .prettyPeek()
                .then()
                .extract()
                .body()
                .as(ImageResponse.class)
                .getData().getDeletehash();
    }

    @Test
    void uploadWithMultiPart() {
        deleteHashImage = given(requestSpecificationWithAuthAndMultipartImage)
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
                .then()
                .extract()
                .body()
                .as(ImageResponse.class)
                .getData().getDeletehash();
    }

    @DisplayName("Загрузка файла без файла")
    @Test
    void uploadFileWithoutImageTest() {
        deleteHashImage = given(requestSpecificationWithAuth, negativeNoFileResponseSpecification)
                .post(UPLOAD_IMAGE)
                .prettyPeek()
                .then()
                .extract()
                .body()
                .as(ImageResponse.class)
                .getData().getDeletehash();
    }

    @DisplayName("Загрузка файла в без авторизации")
    @Test
    void uploadFileWithoutAuthTest() {
        deleteHashImage = given()
                .expect()
                .statusCode(401)
                .when().post(UPLOAD_IMAGE)
                .prettyPeek()
                .then()
                .extract()
                .response()
                .body()
                .as(ImageResponse.class)
                .getData().getDeletehash();
    }

    @DisplayName("Загрузка файла в формате base64, проверка поля type")
    @Test
    void uploadFileTestType() {
        deleteHashImage = given(requestSpecificationWithAuthWithBase64)
                .expect()
                .body("data.type", equalTo("image/jpeg"))
                .when()
                .post(UPLOAD_IMAGE)
                .prettyPeek()
                .then()
                .extract()
                .response()
                .body()
                .as(ImageResponse.class)
                .getData().getDeletehash();
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