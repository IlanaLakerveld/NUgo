package com.nedap.go;

import java.util.Scanner;

public final class getPortInput {
    static Scanner scanner = new Scanner(System.in);

    public static int getPort(Boolean clientSide) {
        boolean correctPortInteger = false;
        int port = -1;
        while (!correctPortInteger ) {
            System.out.println("Import a port number, between 1 and 65535. Import 0 only allowed for the server.");
            String number = scanner.nextLine();

            try {
                port = Integer.parseInt(number);
            } catch (NumberFormatException e) {
                System.out.println("This is not a number, please enter a number");

                continue;
            }
            if (clientSide) {
                if (port < 1 || port > 65535) {
                    System.out.println("Port number should be between 1 and 65535. Import 0 only allowed for the server.");

                } else {
                    correctPortInteger  = true;
                }
            } else {
                if (port < 0 || port > 65535) {
                    System.out.println("Port number should be between 1 and 65535 or 0 for a random available port.");
                } else {
                    correctPortInteger  = true;
                }

            }

        }
        return port;
    }
}