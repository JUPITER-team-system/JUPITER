Feature: Create user
  As the administrator, I want to create CODER, TL, and ADMIN users to manage who can use the application.

  @create-user
  Scenario Outline: Successfully create a user with different roles
    Given an administrator is authenticated
    And a valid username "<username>"
    And a valid email "<email>"
    And a valid password "<password>"
    And the role "<role>"
    When the administrator submits the user creation request
    Then the user should be created successfully
    And the user should have the role "<role>"

    Examples:
      | username | email          | password | role  |
      | test 1   | coder@test.com | 12345    | CODER |
      | test 2   | tl@test.com    | 12345    | TL    |
      | test 3   | admin@test.com | 12345    | ADMIN |


  Scenario: Create user with email already exists
    Given an administrator is authenticated
    And a valid username "test 1"
    And a valid email "juan@gmail.com"
    And a valid password "12345"
    And the role "CODER"
    When the administrator submits the user creation request
    Then the creation fails with error message "Email already exists"

