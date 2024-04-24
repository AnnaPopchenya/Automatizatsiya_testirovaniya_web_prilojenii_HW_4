package org.example.pom;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.support.FindBy;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;

public class ProfilePage {

    @FindBy(xpath = "//h3/following-sibling::div" +
            "//div[contains(text(), 'Full name')]/following-sibling::div")
    private SelenideElement fullNameInAdditionalInfo;

    @FindBy(css="div.mdc-card h2")
    private SelenideElement fullNameInAvatarSection;

    @FindBy(css = "div.mdc-card div.mdc-card__action-icons button[title='More options']")
    private SelenideElement editIconInAvatarSection;

    @FindBy(xpath = "//form[@id='update-item']//span[contains(text(), 'Birthdate')]/following-sibling::input")
    public SelenideElement newBirthdate;

    @FindBy(css = "div.mdc-button__label")
    private SelenideElement saveProfileButton;
    @FindBy(xpath = "//div[contains(text(), 'Date of Birth')]/following-sibling::div")
    private SelenideElement dateOfBirthAdditionalInfo;


    public void clickEditIconInAvatarSection() {
        editIconInAvatarSection.shouldBe(visible).click();
    }

    public String getFullNameFromAdditionalInfo() {
        return fullNameInAdditionalInfo.shouldBe(visible).text();
    }

    public String getFullNameFromAvatarSection() {
        return fullNameInAvatarSection.shouldBe(visible).text();
    }

    public void setNewBirthdate(String birthdate) {
        newBirthdate.setValue(birthdate);
    }

    public void clickSaveProfileButton() {
        saveProfileButton.click();
    }

    public void checkDateOfBirthInAdditionalInfo(String expectedDateOfBirth) {
        dateOfBirthAdditionalInfo.shouldHave(visible).shouldHave(text(expectedDateOfBirth));
    }
}
