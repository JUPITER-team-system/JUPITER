package com.management.jupiter.security;

import com.management.jupiter.models.User;
import com.management.jupiter.models.enums.Role;

public final class UserSession implements LoginSession {

    private final User user;

    public UserSession(User user) {
        this.user = user;
    }

    @Override
    public User loggedUser () {
        return this.user;
    }

    @Override
    public boolean isAdmin () {
        return user.getRole() == Role.ADMIN;
    }

}
