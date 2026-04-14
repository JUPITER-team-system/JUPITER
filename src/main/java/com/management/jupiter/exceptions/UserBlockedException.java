package com.management.jupiter.exceptions;

public class UserBlockedException extends Exception{

    private final long timer;

    public UserBlockedException(long timer){
        super("The access was denied temporarily for secure");
        this.timer = timer;
    }

    public long remaningTime() {
        long current = System.currentTimeMillis();
        long remaining = (timer - current) / 1000;

        return Math.max(0, remaining);
    }
}
