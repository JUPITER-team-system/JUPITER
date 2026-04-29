package stepsdefinitions;

import com.management.jupiter.controllers.UserController;
import com.management.jupiter.models.Admin;
import com.management.jupiter.models.Attempts;
import com.management.jupiter.models.User;
import com.management.jupiter.models.enums.Role;
import com.management.jupiter.services.UserService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.lang.reflect.Field;
import java.util.Map;

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
        when(userService.authenticate(email, "12345")).thenThrow(new Exception("User not found in the system"));
    }

    @Given("the password for email {string} is incorrect")
    public void thePasswordForEmailIsIncorrect(String email) throws Exception {
        when(userService.authenticate(email, "wrong-password")).thenThrow(new Exception("Incorrect password"));
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

    @When("the user tries to log in with email {string} and password {string} {int} times")
    public void theUserTriesToLogInSeveralTimes(String email, String password, int attempts) {
        this.authenticatedUser = null;
        this.loginException = null;

        for (int i = 0; i < attempts; i++) {
            try {
                authenticatedUser = userController.login(email, password);
                loginException = null;
            } catch (Exception exception) {
                authenticatedUser = null;
                loginException = exception;
            }
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

    @Then("the user should have {int} login attempts available for email {string}")
    public void theUserShouldHaveLoginAttemptsAvailableForEmail(int expectedAttempts, String email) {
        assertEquals(expectedAttempts, getAvailableAttempts(email));
    }

    @SuppressWarnings("unchecked")
    private int getAvailableAttempts(String email) {
        try {
            Field attemptsField = UserController.class.getDeclaredField("attemptsPerUser");
            attemptsField.setAccessible(true);

            Map<String, Attempts> attemptsPerUser = (Map<String, Attempts>) attemptsField.get(userController);
            Attempts attempts = attemptsPerUser.get(email);
            if (attempts != null && System.currentTimeMillis() < attempts.blockedUntil) {
                return 0;
            }

            return userController.getLeftAttempts(email);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("Could not inspect login attempts state.", e);
        }
    }
}
