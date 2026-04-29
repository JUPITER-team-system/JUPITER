package stepsdefinitions;

import com.management.jupiter.controllers.UserController;
import com.management.jupiter.models.Admin;
import com.management.jupiter.models.User;
import com.management.jupiter.models.enums.Role;
import com.management.jupiter.services.UserService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LoginSteps {

    private final UserService userService = mock(UserService.class);
    private final UserController userController = new UserController(userService);

    private User authenticatedUser;
    private Exception loginException;

    @Given("a registered user exists with email {string} and password {string}")
    public void aRegisteredUserExistsWithEmailAndPassword(String email, String password) throws Exception {
        User user = new Admin("test-admin-id", "Login Test User", email, password, Role.ADMIN);
        when(userService.authenticate(email, password)).thenReturn(user);
    }

    @Given("no registered user exists with email {string}")
    public void noRegisteredUserExistsWithEmail(String email) throws Exception {
        when(userService.authenticate(email, "12345"))
                .thenThrow(new Exception("User not found in the system"));
    }

    @Given("the password for email {string} is incorrect")
    public void thePasswordForEmailIsIncorrect(String email) throws Exception {
        when(userService.authenticate(email, "wrong-password"))
                .thenThrow(new Exception("Incorrect password"));
    }

    @When("the user tries to log in with email {string} and password {string}")
    public void theUserTriesToLogInWithEmailAndPassword(String email, String password) {
        this.authenticatedUser = null;
        this.loginException = null;

        try {
            authenticatedUser = userController.login(email, password);
        } catch (Exception exception) {
            loginException = exception;
        }
    }

    @Then("the login should authenticate the user with email {string}")
    public void theLoginShouldAuthenticateTheUserWithEmail(String expectedEmail) {
        assertNotNull(authenticatedUser);
        assertEquals(expectedEmail, authenticatedUser.getEmail());
        assertNull(loginException);
    }

    @Then("the login should fail with the message {string}")
    public void theLoginShouldFailWithTheMessage(String expectedMessage) {
        assertNull(authenticatedUser);
        assertNotNull(loginException);
        assertEquals(expectedMessage, loginException.getMessage());
    }
}
