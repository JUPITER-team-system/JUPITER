package stepsdefinitions;

import com.management.jupiter.models.User;
import com.management.jupiter.services.UserServices;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class LoginSteps {

    private String email;
    private String password;
    private User authenticatedUser;
    private Exception loginException;

    @Given("the user enters the email {string}")
    public void theUserEntersTheEmail(String email) {
        this.email = email;
    }

    @Given("the user enters the password {string}")
    public void theUserEntersThePassword(String password) {
        this.password = password;
    }

    @When("the login is submitted")
    public void theLoginIsSubmitted() {
        authenticatedUser = null;
        loginException = null;

        try {
            authenticatedUser = UserServices.LoginService(email, password);
        } catch (Exception exception) {
            loginException = exception;
        }
    }

    @Then("the login should authenticate the user")
    public void theLoginShouldAuthenticateTheUser() {
        assertNotNull(authenticatedUser);
        assertEquals(email, authenticatedUser.getEmail());
        assertNull(loginException);
    }

    @Then("the login should be rejected due to invalid credentials")
    public void theLoginShouldBeRejectedDueToInvalidCredentials() {
        assertNull(authenticatedUser);
        assertNull(loginException);
    }

    @Then("the login should fail with the message {string}")
    public void theLoginShouldFailWithTheMessage(String expectedMessage) {
        assertNull(authenticatedUser);
        assertNotNull(loginException);
        assertEquals(expectedMessage, loginException.getMessage());
    }
}
