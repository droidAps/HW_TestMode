package ru.netology.data;

import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.*;

import java.util.Locale;

import static com.codeborne.selenide.Selenide.$;
import static io.restassured.RestAssured.given;

public class DataGenerator {
    private static final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();
    private static final Faker faker = new Faker(new Locale("en"));

    private DataGenerator() {
    }

    public static class Registration {
        private Registration() {
        }

        public static RegistrationInfo generateUser(boolean isActive) {
            String status = " ";
            if (isActive == true) {
                status = "active";
            } else {
                status = "blocked";
            }
            return new RegistrationInfo(
                    faker.name().firstName(),
                    faker.internet().password(6, 10),
                    status);
        }

        public static void addUserInDataBase(RegistrationInfo user) {
            given() // "дано"
                    .spec(requestSpec) // указываем, какую спецификацию используем
                    .body(user) // передаём в теле объект, который будет преобразован в JSON
                    .when() // "когда"
                    .post("/api/system/users") // на какой путь, относительно BaseUri отправляем запрос
                    .then() // "тогда ожидаем"
                    .statusCode(200); // код 200 OK
        }

        //цель метода - создание невалидного пароля для входа в систему
        public static RegistrationInfo generateWrongPassword(RegistrationInfo user) {
            if (user.password != "wrongPassword") {
                return new RegistrationInfo(
                        user.login,
                        "wrongPassword",
                        user.status);
            } else {
                return new RegistrationInfo(
                        user.login,
                        "passwordWrong",
                        user.status);
            }
        }

        //цель метода - создание невалидного логина для входа в систему
        public static RegistrationInfo generateWrongLogin(RegistrationInfo user) {
            if (user.login != "wrongLogin") {
                return new RegistrationInfo(
                        "wrongLogin",
                        user.password,
                        user.status);
            } else {
                return new RegistrationInfo(
                        "loginWrong",
                        user.password,
                        user.status);
            }
        }

        public static RegistrationInfo changeUserStatus(RegistrationInfo user) {
            if (user.status == "active") {
                return new RegistrationInfo(
                        user.login,
                        user.password,
                        "blocked");
            } else {
                return new RegistrationInfo(
                        user.login,
                        user.password,
                        "active");
            }
        }

        public static void authorization(RegistrationInfo user) {
            $("[data-test-id='login'] input").setValue(user.getLogin());
            $("[data-test-id='password'] input").setValue(user.getPassword());
            $("[data-test-id='action-login']").click();
        }
    }

    @Data
    @RequiredArgsConstructor
    public static class RegistrationInfo {
        private final String login;
        private final String password;
        private final String status;
    }
}
