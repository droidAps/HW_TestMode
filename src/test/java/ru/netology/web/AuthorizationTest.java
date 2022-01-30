package ru.netology.web;

import org.junit.jupiter.api.*;
import ru.netology.data.DataGenerator;
import ru.netology.page.LoginPage;

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
        var loginPage = new LoginPage();
        DataGenerator.RegistrationInfo activeUser = DataGenerator.Registration.generateUser("active");
        DataGenerator.Registration.addUserInDataBase(activeUser);
        loginPage.authorization(activeUser);
        $("#root h2").shouldBe(visible, Duration.ofSeconds(5))
                .shouldHave(text("Личный кабинет"));
    }

    @Test
    @DisplayName("Should get error message if login with not registered user")
    void shouldGetErrorIfNotRegisteredUser() {
        var loginPage = new LoginPage();
        DataGenerator.RegistrationInfo unregisteredUser = DataGenerator.Registration.generateUser("active");
        loginPage.authorization(unregisteredUser);
        $("[data-test-id='error-notification'] .notification__content").shouldBe(visible, Duration.ofSeconds(5))
                .shouldHave(exactText("Ошибка!  " + "Неверно указан логин или пароль"));
    }

    @Test
    @DisplayName("Should get error message if login with blocked registered user")
    void shouldGetErrorIfBlockedUser() {
        var loginPage = new LoginPage();
        DataGenerator.RegistrationInfo blockedUser = DataGenerator.Registration.generateUser("blocked");
        DataGenerator.Registration.addUserInDataBase(blockedUser);
        loginPage.authorization(blockedUser);
        $("[data-test-id='error-notification'] .notification__content").shouldBe(visible, Duration.ofSeconds(5))
                .shouldHave(exactText("Ошибка!  " + "Пользователь заблокирован"));
    }

    @Test
    @DisplayName("Should get error message if login with wrong login")
    void shouldGetErrorIfWrongLogin() {
        var loginPage = new LoginPage();
        DataGenerator.RegistrationInfo activeUser = DataGenerator.Registration.generateUser("active");
        DataGenerator.Registration.addUserInDataBase(activeUser);
        DataGenerator.RegistrationInfo activeUserWithWrongLogin = DataGenerator.Registration.generateWrongLogin(activeUser);
        loginPage.authorization(activeUserWithWrongLogin);
        $("[data-test-id='error-notification'] .notification__content").shouldBe(visible, Duration.ofSeconds(5))
                .shouldHave(exactText("Ошибка!  " + "Неверно указан логин или пароль"));
    }

    @Test
    @DisplayName("Should get error message if login with wrong password")
    void shouldGetErrorIfWrongPassword() {
        var loginPage = new LoginPage();
        DataGenerator.RegistrationInfo activeUser = DataGenerator.Registration.generateUser("active");
        DataGenerator.Registration.addUserInDataBase(activeUser);
        DataGenerator.RegistrationInfo activeUserWithWrongPassword = DataGenerator.Registration.generateWrongPassword(activeUser);
        loginPage.authorization(activeUserWithWrongPassword);
        $("[data-test-id='error-notification'] .notification__content").shouldBe(visible, Duration.ofSeconds(5))
                .shouldHave(exactText("Ошибка!  " + "Неверно указан логин или пароль"));
    }
}

