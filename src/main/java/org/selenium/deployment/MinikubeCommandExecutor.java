package org.selenium.deployment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MinikubeCommandExecutor {

    public static void main(String[] args) {
        executeMinikubeCommand();
    }

    public static void executeMinikubeCommand() {
        try {
            // Command to execute
            String[] command = {"minikube", "service", "frontend-service", "--url"};

            // Create ProcessBuilder instance
            ProcessBuilder processBuilder = new ProcessBuilder(command);

            // Redirect error stream to output stream
            processBuilder.redirectErrorStream(true);

            // Start the process
            Process process = processBuilder.start();

            // Read output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder output = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            // Wait for the process to complete
            int exitCode = process.waitFor();

            // Print output
            System.out.println("Command output:");
            System.out.println(output.toString());

            // Check exit code
            if (exitCode == 0) {
                System.out.println("Command executed successfully.");
            } else {
                System.err.println("Command failed with error code: " + exitCode);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

