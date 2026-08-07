/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.example.common;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

@SuppressWarnings({ "HideUtilityClassConstructor" })
public final class ConsoleHelper {

    private ConsoleHelper() {}

    public static String readLine() {
        Scanner scanner = new Scanner(System.in);
        if (scanner.hasNextLine()) {
            return scanner.nextLine();
        }
        return readLine();
    }

    public static String readNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = readLine();
            if (value != null && !value.trim().isEmpty()) {
                return value.trim();
            }
            System.out.println("  Value cannot be empty. Please try again.");
        }
    }

    public static String readExistingPemPath(String prompt) {
        while (true) {
            System.out.print(prompt);
            String privateKeyFilePath = readLine();
            if (privateKeyFilePath == null || privateKeyFilePath.trim().isEmpty()) {
                System.out.println("  Path cannot be empty. Please try again.");
                continue;
            }

            if (Files.exists(Paths.get(privateKeyFilePath.trim()))) {
                return privateKeyFilePath.trim();
            }

            System.out.println("  File not found. Please enter a valid path to an existing .pem file.");
        }
    }

    public static boolean readYesNo(String prompt) {
        System.out.print(prompt);
        return "y".equalsIgnoreCase(readLine());
    }
}
