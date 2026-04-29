package com.management.jupiter.models;

import java.time.OffsetDateTime;

public class Information {
    private final String id;
    private final String title;
    private final String message;
    private final String clanId;
    private final OffsetDateTime createdAt;

    public Information(String id, String title, String message, String clanId, OffsetDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.clanId = clanId;
        this.createdAt = createdAt;
    }

    public Information(String title, String message, String clanId) {
        this(null, title, message, clanId, null);
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getClanId() {
        return clanId;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
