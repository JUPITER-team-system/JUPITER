Feature: Create user
  RF-03: As the administrator, I want to create CODER, TL, and ADMIN users to manage who can use the application.

  @create-user
  Scenario Outline: Successfully create a user with different roles
    Given an administrator is authenticated
    And a valid username "<username>"
    And a valid email "<email>"
    And a valid password "<password>"
    And the role "<role>"
    And the target clan is "<clan>"
    And the TL type is "<tlType>"
    When the administrator submits the user creation request
    Then the user should be created successfully
    And the user should have the role "<role>"
    And the user repository should save the created user

    Examples:
      | username | email          | password | role  | clan     | tlType       |
      | test 1   | coder@test.com | 12345    | CODER | HAMILTON | PROGRAMACION |
      | test 2   | tl@test.com    | 12345    | TL    | HAMILTON | INGLES       |
      | test 3   | admin@test.com | 12345    | ADMIN | NONE     | NONE         |

  Scenario: Create user with email already exists
    Given an administrator is authenticated
    And a valid username "test 1"
    And a valid email "juan@gmail.com"
    And a valid password "12345"
    And the role "CODER"
    And the target clan is "HAMILTON"
    And the email "juan@gmail.com" already exists
    When the administrator submits the user creation request
    Then the creation fails with error message "Email already exists"
    And the user repository should not save any user

  Scenario: Reject coder creation without clan
    Given an administrator is authenticated
    And a valid username "test coder"
    And a valid email "missing-clan@test.com"
    And a valid password "12345"
    And the role "CODER"
    And the target clan is "NONE"
    When the administrator submits the user creation request
    Then the creation fails with error message "Clan is required for TL and CODER"
    And the user repository should not save any user
