Feature: Login
  The login service should validate registered users against the persisted data.

  Scenario: Successful login with valid credentials
    Given the user enters the email "juan@gmail.com"
    And the user enters the password "12345"
    When the login is submitted
    Then the login should authenticate the user

  Scenario: Rejected login with invalid password
    Given the user enters the email "juan@gmail.com"
    And the user enters the password "wrong-password"
    When the login is submitted
    Then the login should be rejected due to invalid credentials

  Scenario: Failed login when the email is not registered
    Given the user enters the email "unknown@gmail.com"
    And the user enters the password "12345"
    When the login is submitted
    Then the login should fail with the message "Some of the fields do not match"
