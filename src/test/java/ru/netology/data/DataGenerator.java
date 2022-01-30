package ru.netology.data;

import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.*;

import java.util.Locale;

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

         // метод на "вход" принимает только 2 значения переменной status:
         // "active" - пользователь активен
         // "blocked" - пользователь заблокирован
         public static RegistrationInfo generateUser(String status) {
            return new RegistrationInfo(
                    faker.name().firstName(),
                    faker.internet().password(5, 7),
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
                return new RegistrationInfo(
                        user.login,
                        faker.internet().password(8, 10),
                        user.status);
        }

        //цель метода - создание невалидного логина для входа в систему
        public static RegistrationInfo generateWrongLogin(RegistrationInfo user) {
                return new RegistrationInfo(
                        faker.name().username(),
                        user.password,
                        user.status);
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
