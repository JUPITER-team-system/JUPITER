package com.management.jupiter.security;

import com.management.jupiter.models.User;

public sealed interface LoginSession permits UserSession {

    User loggedUser();
    boolean isAdmin();

}
