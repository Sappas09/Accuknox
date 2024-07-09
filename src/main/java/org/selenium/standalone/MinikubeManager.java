package org.selenium.standalone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MinikubeManager {

    public static void main(String[] args) {
        try {
            executeBasedOnTerminalResponse();

            applyKubernetesDeployments();

            String frontendServiceURL = getMinikubeServiceURL("frontend-service");
            System.out.println("Frontend service URL: " + frontendServiceURL);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void executeBasedOnTerminalResponse() throws IOException, InterruptedException {
        String response = getTerminalResponse(); // Implement this method to get the terminal response

        if (response.contains("permission denied while trying to connect to the Docker daemon socket")) {
            addUserToDockerGroup();
            restartDockerService();
        }

        if (response.contains("docker only has 0MiB available")) {
            deleteAndStartMinikube();
        }
    }

    private static String getTerminalResponse() throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("echo", "dummy_command_to_get_terminal_response");
        pb.redirectErrorStream(true);
        Process process = pb.start();

        InputStream inputStream = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder terminalOutput = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            terminalOutput.append(line).append("\n");
        }
        reader.close();

        process.waitFor();

        return terminalOutput.toString();
    }

    private static void addUserToDockerGroup() throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("sudo", "usermod", "-aG", "docker", "$USER");
        pb.inheritIO();
        Process process = pb.start();
        process.waitFor();
    }

    private static void restartDockerService() throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("sudo", "systemctl", "restart", "docker");
        pb.inheritIO();
        Process process = pb.start();
        process.waitFor();
    }

    private static void deleteAndStartMinikube() throws IOException, InterruptedException {
        ProcessBuilder pbDelete = new ProcessBuilder("minikube", "delete");
        pbDelete.inheritIO();
        Process processDelete = pbDelete.start();
        processDelete.waitFor();

        ProcessBuilder pbStart = new ProcessBuilder("minikube", "start", "--driver=docker", "--force");
        pbStart.inheritIO();
        Process processStart = pbStart.start();
        processStart.waitFor();
    }

    private static void applyKubernetesDeployments() throws IOException, InterruptedException {
        ProcessBuilder pbBackend = new ProcessBuilder("kubectl", "apply", "-f", "backend-deployment.yaml", "--validate=false");
        pbBackend.inheritIO();
        Process processBackend = pbBackend.start();
        processBackend.waitFor();

        ProcessBuilder pbFrontend = new ProcessBuilder("kubectl", "apply", "-f", "frontend-deployment.yaml", "--validate=false");
        pbFrontend.inheritIO();
        Process processFrontend = pbFrontend.start();
        processFrontend.waitFor();
    }

    private static String getMinikubeServiceURL(String serviceName) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("minikube", "service", serviceName, "--url");
        Process process = pb.start();
        process.waitFor();

        InputStream inputStream = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String serviceURL = reader.readLine();
        reader.close();

        return serviceURL;
    }
}


