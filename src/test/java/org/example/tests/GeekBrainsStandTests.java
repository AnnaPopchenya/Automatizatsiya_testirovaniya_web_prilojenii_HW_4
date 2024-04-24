package org.example.tests;


import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import org.example.pom.LoginPage;
import org.example.pom.MainPage;
import org.example.pom.ProfilePage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Пример использования самых базовых методов библиотеки Selenium.
 */
public class GeekBrainsStandTests {

    private WebDriver driver;
    private WebDriverWait wait;
    private LoginPage loginPage;
    private MainPage mainPage;

    private static String USERNAME;
    private static String PASSWORD;

    @BeforeAll
    public static void setupClass() {
        Configuration.remote = "http://localhost:4444/wd/hub";
        Configuration.browser = "chrome";
        Configuration.browserVersion = "123";
        Map<String, Object> options = new HashMap<>();
        options.put("enableVNC", true);
        options.put("enableLog", true);
        Configuration.browserCapabilities.setCapability("selenoid:options", options);

        // mvn clean test -Dgeekbrains_username=USER -Dgeekbrains_password=PASS
        USERNAME = "GB202310790786";
        PASSWORD = "559bf6f5d1";
    }

    @BeforeEach
    public void setupTest() {
        //Создаем экземпляр драйвера
        Selenide.open("https://test-stand.gb.ru/login");
        driver = WebDriverRunner.getWebDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        // навигация на https://test-stand.gb.ru/login
        driver.get("https://test-stand.gb.ru/login");
        //Объект созданного Page Object
        loginPage = Selenide.page(LoginPage.class);
    }


    @Test
    public void testAddingGroupOnMainPage()  {
        //Логин в систему с помощью метода из класса Page Object
        loginPage.login(USERNAME, PASSWORD);
        //Инициализация объекта класса MainPage
        mainPage = Selenide.page(MainPage.class);
        assertTrue(mainPage.getUsernameLabelText().contains(USERNAME));
        //Создание группы. Даем ей имя, чтобы в каждом запуске была проверка нового имени
        String groupTestName = "New Test Group" + System.currentTimeMillis();
        mainPage.createGroup(groupTestName);
        // Проверка, что группа создана и находится в таблице
        assertTrue(mainPage.waitAndGetGroupTitleByText(groupTestName).isDisplayed());
    }

    @Test
    public void testLoginWithEmptyFields() {
        // Клик на кнопку LOGIN без ввода данных в поля
        loginPage.clickLoginButton();
        // Проверка, что появился блок с ожидаемой ошибкой
        assertEquals("401 Invalid credentials.", loginPage.getErrorBlockText());
    }


    @Test
    void testArchiveGroupOnMainPage() {
        // Обычный логин + создание группы
        loginPage.login(USERNAME, PASSWORD);
        mainPage = Selenide.page(MainPage.class);
        assertTrue(mainPage.getUsernameLabelText().contains(USERNAME));
        String groupTestName = "New Test Group " + System.currentTimeMillis();
        mainPage.createGroup(groupTestName);
        // Требуется закрыть модальное окно
        mainPage.closeCreateGroupModalWindow();
        // Изменение созданной группы с проверками
        assertEquals("active", mainPage.getStatusOfGroupWithTitle(groupTestName));
        mainPage.clickTrashIconOnGroupWithTitle(groupTestName);
        assertEquals("inactive", mainPage.getStatusOfGroupWithTitle(groupTestName));
        mainPage.clickRestoreFromTrashIconOnGroupWithTitle(groupTestName);
        assertEquals("active", mainPage.getStatusOfGroupWithTitle(groupTestName));
    }

    @Test
    void testBlockingStudentInTableOnMainPage() throws InterruptedException {
        // Обычный логин + создание группы
        loginPage.login(USERNAME, PASSWORD);
        mainPage = Selenide.page(MainPage.class);
        assertTrue(mainPage.getUsernameLabelText().contains(USERNAME));
        String groupTestName = "New Test Group " + System.currentTimeMillis();
        mainPage.createGroup(groupTestName);
        // Требуется закрыть модальное окно
        mainPage.closeCreateGroupModalWindow();
        // Добавление студентов
        mainPage.clickAddStudentsIconOnGroupWithTitle(groupTestName);
        mainPage.typeAmountOfStudentsInCreateStudentsForm(3);
        mainPage.clickSaveButtonOnCreateStudentsForm();
        mainPage.closeCreateStudentsModalWindow();
        mainPage.clickZoomInIconOnGroupWithTitle(groupTestName);
        // Проверка переходов статуса первого студента из таблицы
        String firstGeneratedStudentName = mainPage.getFirstGeneratedStudentName();
        assertEquals("active", mainPage.getStatusOfStudentWithName(firstGeneratedStudentName));
        mainPage.clickTrashIconOnStudentWithName(firstGeneratedStudentName);
        assertEquals("block", mainPage.getStatusOfStudentWithName(firstGeneratedStudentName));
        mainPage.clickRestoreFromTrashIconOnStudentWithName(firstGeneratedStudentName);
        assertEquals("active", mainPage.getStatusOfStudentWithName(firstGeneratedStudentName));
    }

    @Test
    void testFullNameOnProfilePage() {
        //огин в систему с помощью метода из класса Page Object
        loginPage.login(USERNAME,PASSWORD);
        //Инициализация объекта класса Main Page
        mainPage = Selenide.page(MainPage.class);
        assertTrue(mainPage.getUsernameLabelText().contains(USERNAME));
        //Навигация на Profile page
        mainPage.clickUsernameLabel();
        mainPage.clickProfileLink();
        //Инициализация ProfilePage с помощью Selenide
        ProfilePage profilePage = Selenide.page(ProfilePage.class);
        assertEquals("GB202310 790786", profilePage.getFullNameFromAdditionalInfo());
        assertEquals("GB202310 790786", profilePage.getFullNameFromAvatarSection());
    }


    @Test
    public void testProfilePageBirthdate() {
        //огин в систему с помощью метода из класса Page Object
        loginPage.login(USERNAME,PASSWORD);
        //Инициализация объекта класса Main Page
        mainPage = Selenide.page(MainPage.class);
        assertTrue(mainPage.getUsernameLabelText().contains(USERNAME));
        //Навигация на Profile page
        mainPage.clickUsernameLabel();
        mainPage.clickProfileLink();
        //Инициализация ProfilePage с помощью Selenide
        ProfilePage profilePage = Selenide.page(ProfilePage.class);
        // Открыть модальное окно Editing
        profilePage.clickEditIconInAvatarSection();
        // Изменить Birthdate значение
        String newBirthdateValue = "01/01/1990";
        profilePage.newBirthdate.setValue(newBirthdateValue);
        // Нажать на кнопку Save
        profilePage.clickSaveProfileButton();
        // Проверить, что изменения применились в поле Date of Birth в секции Additional Info
        profilePage.checkDateOfBirthInAdditionalInfo(newBirthdateValue);
    }
    @AfterEach
    public void teardown() {
        //Закрываем все окна браузера и процесс драйвера
        WebDriverRunner.closeWebDriver();
    }

}
