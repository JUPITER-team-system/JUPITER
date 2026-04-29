Feature: Login
  The login flow should react correctly to the authentication result.

  Scenario: Successful login with valid credentials
    Given a registered user exists with email "login.success@jupiter.test" and password "12345"
    When the user tries to log in with email "login.success@jupiter.test" and password "12345"
    Then the login should authenticate the user with email "login.success@jupiter.test"

  Scenario: Rejected login with invalid password
    Given a registered user exists with email "login.invalid-password@jupiter.test" and password "12345"
    And the password for email "login.invalid-password@jupiter.test" is incorrect
    When the user tries to log in with email "login.invalid-password@jupiter.test" and password "wrong-password"
    Then the login should fail with the message "Invalid credentials"

  Scenario: Failed login when the email is not registered
    Given no registered user exists with email "login.unknown@jupiter.test"
    When the user tries to log in with email "login.unknown@jupiter.test" and password "12345"
    Then the login should fail with the message "Invalid credentials"
