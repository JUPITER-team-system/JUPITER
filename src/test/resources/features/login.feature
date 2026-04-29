Feature: Login
  RF-01: The system must allow users to log in by role and protect access after repeated failures.

  Scenario: Successful login with valid credentials
    Given a registered user exists with email "login.success@jupiter.test" and password "12345"
    When the user tries to log in with email "login.success@jupiter.test" and password "12345"
    Then the login should authenticate the user with email "login.success@jupiter.test"
    And the user should have 3 login attempts available for email "login.success@jupiter.test"

  Scenario: Rejected login with invalid password
    Given the password for email "login.invalid-password@jupiter.test" is incorrect
    When the user tries to log in with email "login.invalid-password@jupiter.test" and password "wrong-password"
    Then the login should fail with the message "Invalid credentials"
    And the user should have 2 login attempts available for email "login.invalid-password@jupiter.test"

  Scenario: Failed login when the email is not registered
    Given no registered user exists with email "login.unknown@jupiter.test"
    When the user tries to log in with email "login.unknown@jupiter.test" and password "12345"
    Then the login should fail with the message "Invalid credentials"
    And the user should have 2 login attempts available for email "login.unknown@jupiter.test"

  Scenario: Block access after three failed login attempts
    Given the password for email "login.blocked@jupiter.test" is incorrect
    When the user tries to log in with email "login.blocked@jupiter.test" and password "wrong-password" 3 times
    Then the login should fail with the message "The access was denied temporarily for secure"
    And the user should have 0 login attempts available for email "login.blocked@jupiter.test"

  Scenario: Reset failed attempts after a successful login
    Given a registered user exists with email "login.reset@jupiter.test" and password "12345"
    And the password for email "login.reset@jupiter.test" is incorrect
    When the user tries to log in with email "login.reset@jupiter.test" and password "wrong-password"
    And the user tries to log in with email "login.reset@jupiter.test" and password "12345"
    Then the login should authenticate the user with email "login.reset@jupiter.test"
    And the user should have 3 login attempts available for email "login.reset@jupiter.test"
