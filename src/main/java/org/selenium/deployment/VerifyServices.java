package org.selenium.deployment;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class VerifyServices {
    public static void main(String[] args) {
        try (KubernetesClient client = new DefaultKubernetesClient()) {
            // Get the frontend service
            Service frontendService = client.services().inNamespace("default").withName("frontend-service").get();
            if (frontendService != null) {
                // Get Minikube IP
                String minikubeIp = getMinikubeIp();

                // Get NodePort of the frontend service
                int nodePort = frontendService.getSpec().getPorts().get(0).getNodePort();
                String frontendUrl = "http://" + minikubeIp + ":" + nodePort;

                // Verify the frontend displays the greeting message from the backend
                String greeting = fetchGreetingMessage(frontendUrl);
                System.out.println("Greeting message: " + greeting);
            } else {
                System.out.println("Frontend service not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getMinikubeIp() throws IOException, InterruptedException {
        Process process = new ProcessBuilder("minikube", "ip").start();
        process.waitFor();
        Scanner scanner = new Scanner(process.getInputStream());
        return scanner.nextLine();
    }

    private static String fetchGreetingMessage(String frontendUrl) throws IOException {
        URL url = new URL(frontendUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        Scanner scanner = new Scanner(connection.getInputStream());
        StringBuilder response = new StringBuilder();
        while (scanner.hasNext()) {
            response.append(scanner.nextLine());
        }
        scanner.close();
        return response.toString();
    }
}
