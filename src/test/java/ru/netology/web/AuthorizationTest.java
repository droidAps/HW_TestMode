package ru.netology.web;

import org.junit.jupiter.api.*;
import ru.netology.data.DataGenerator;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class AuthorizationTest {

    @BeforeEach
    void setup() {
        open("http://localhost:9999");
    }

    @Test
    @DisplayName("Should successfully login with active registered user")
    void shouldSuccessfulLoginIfRegisteredActiveUser() {
        DataGenerator.RegistrationInfo activeUser = DataGenerator.Registration.generateUser(true);
        DataGenerator.Registration.addUserInDataBase(activeUser);
        DataGenerator.Registration.authorization(activeUser);
        $("#root h2").shouldBe(visible, Duration.ofSeconds(5))
                .shouldHave(text("Личный кабинет"));
    }

    @Test
    @DisplayName("Should get error message if login with not registered user")
    void shouldGetErrorIfNotRegisteredUser() {
        DataGenerator.RegistrationInfo unregisteredUser = DataGenerator.Registration.generateUser(true);
        DataGenerator.Registration.authorization(unregisteredUser);
        $("[data-test-id='error-notification'] .notification__content").shouldBe(visible, Duration.ofSeconds(5))
                .shouldHave(exactText("Ошибка!  " + "Неверно указан логин или пароль"));
    }

    @Test
    @DisplayName("Should get error message if login with blocked registered user")
    void shouldGetErrorIfBlockedUser() {
        DataGenerator.RegistrationInfo blockedUser = DataGenerator.Registration.generateUser(false);
        DataGenerator.Registration.addUserInDataBase(blockedUser);
        DataGenerator.Registration.authorization(blockedUser);
        $("[data-test-id='error-notification'] .notification__content").shouldBe(visible, Duration.ofSeconds(5))
                .shouldHave(exactText("Ошибка!  " + "Пользователь заблокирован"));
    }

    @Test
    @DisplayName("Should get error message if login with wrong login")
    void shouldGetErrorIfWrongLogin() {
        DataGenerator.RegistrationInfo activeUser = DataGenerator.Registration.generateUser(true);
        DataGenerator.Registration.addUserInDataBase(activeUser);
        DataGenerator.RegistrationInfo activeUserWithWrongLogin = DataGenerator.Registration.generateWrongLogin(activeUser);
        DataGenerator.Registration.authorization(activeUserWithWrongLogin);
        $("[data-test-id='error-notification'] .notification__content").shouldBe(visible, Duration.ofSeconds(5))
                .shouldHave(exactText("Ошибка!  " + "Неверно указан логин или пароль"));
    }

    @Test
    @DisplayName("Should get error message if login with wrong password")
    void shouldGetErrorIfWrongPassword() {
        DataGenerator.RegistrationInfo activeUser = DataGenerator.Registration.generateUser(true);
        DataGenerator.Registration.addUserInDataBase(activeUser);
        DataGenerator.RegistrationInfo activeUserWithWrongPassword = DataGenerator.Registration.generateWrongPassword(activeUser);
        DataGenerator.Registration.authorization(activeUserWithWrongPassword);
        $("[data-test-id='error-notification'] .notification__content").shouldBe(visible, Duration.ofSeconds(5))
                .shouldHave(exactText("Ошибка!  " + "Неверно указан логин или пароль"));
    }
}

