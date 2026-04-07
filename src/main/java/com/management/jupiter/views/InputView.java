package com.management.jupiter.views;

import java.util.Scanner;

public final class InputView {
    private static final Scanner SCANNER = new Scanner(System.in);

    private InputView() {
    }

    public static Scanner getScanner() {
        return SCANNER;
    }
}
