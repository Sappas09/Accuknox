package org.selenium.standalone;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.selenium.utils.ConfigLoader;
import org.selenium.utils.OSInfoUtils;

public class KubernetesDeployment {

    public static void main(String[] args) throws IOException, InterruptedException {
        String deploymentFolder = ConfigLoader.getInstance().getDeploymentFolder();
		/*
		 * String url =
		 * executeCommandAndGetOutput("minikube service frontend-service --url",
		 * deploymentFolder); System.out.println("Frontend service URL: " + url);
		 */
        
    	String url1= executeCommandAndGetOutput("minikube status", deploymentFolder);

    	String url = executeCommandAndGetOutput("minikube service frontend-service --url", deploymentFolder);
        System.out.println("Frontend service URL: " + url);
        
    }

    public static void setupENV() {
        String deploymentFolder = ConfigLoader.getInstance().getDeploymentFolder();

        try {
            String os = OSInfoUtils.getOSInfo();
            setupDockerPermissions(os);
            installDependencies(os);

            String minikubeStartOutput = executeCommandAndGetOutput("minikube start", deploymentFolder);
            
            System.out.println(minikubeStartOutput);

            if (isMinikubeRunning()) {
                executeCommand("kubectl apply -f backend-deployment.yaml", deploymentFolder);
                executeCommand("kubectl apply -f frontend-deployment.yaml", deploymentFolder);

                String url = executeCommandAndGetOutput("minikube service frontend-service --url", deploymentFolder);
                System.out.println("Frontend service URL: " + url);

                if (url == null || url.isEmpty()) {
                    System.out.println("Failed to retrieve the frontend service URL.");
                } else if (isSiteUp(url)) {
                    System.out.println("Site is up and running!");
                } else {
                    System.out.println("Site is not accessible.");
                }
            } else {
                System.out.println("Minikube is not running. Aborting deployments.");
                return; // Stops the execution
            }


        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
  private static void setupDockerPermissions(String os) throws IOException, InterruptedException {
    if (os.contains("Linux") || os.contains("Mac_OS")) {
        if (!isUserInDockerGroup()) {
            System.out.println("User is not in the Docker group. Setting up Docker permissions...");
            String sudoPassword = "your_password"; // Replace with the actual super user password

            // Add user to the Docker group
            executeCommandWithSudo(sudoPassword, "usermod -aG docker $USER");

            // Inform the user to log out and back in for changes to take effect
            System.out.println("User has been added to the Docker group. Please log out and log back in for changes to take effect.");
            System.exit(0);
        } else {
            System.out.println("User is already in the Docker group. Skipping Docker permissions setup.");
        }
    } else if (os.contains("Windows")) {
        System.out.println("Setting up Docker permissions for Windows...");
        executeCommand("net localgroup docker-users %USERNAME% /add", null);
        System.out.println("Docker permissions set up successfully.");
    }
}

private static boolean isUserInDockerGroup() throws IOException, InterruptedException {
    String command = "groups $USER | grep -q docker";
    ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command);
    Process process = processBuilder.start();
    int exitCode = process.waitFor();
    return exitCode == 0;
}

private static void executeCommandWithSudo(String password, String command) throws IOException, InterruptedException {
    System.out.println("Executing with sudo: " + command);
    ProcessBuilder processBuilder = new ProcessBuilder("sudo", "-S", "bash", "-c", command);
    Process process = processBuilder.start();

    try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
        writer.write(password);
        writer.newLine();
        writer.flush();
    }

    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

    String line;
    while ((line = reader.readLine()) != null) {
        System.out.println(line);
    }
    while ((line = errorReader.readLine()) != null) {
        System.err.println(line);
    }

    process.waitFor();
    System.out.println("Command execution completed.");
}


    private static void installDependencies(String os) throws IOException, InterruptedException {
        if (!isCommandAvailable("docker")) {
            if (os.contains("Windows")) {
                executeCommand("choco install docker-desktop", null);
            } else if (os.contains("Mac_OS")) {
                executeCommand("brew install --cask docker", null);
            } else if (os.contains("Linux")) {
                executeCommand("curl -fsSL https://get.docker.com -o get-docker.sh && sh get-docker.sh", null);
            }
            System.out.println("Docker installed successfully.");
        } else {
            System.out.println("Docker is already installed.");
        }

        if (!isCommandAvailable("minikube")) {
            if (os.contains("Windows")) {
                executeCommand("choco install minikube", null);
            } else if (os.contains("Mac_OS")) {
                executeCommand("brew install minikube", null);
            } else if (os.contains("Linux")) {
                executeCommand("curl -Lo minikube https://storage.googleapis.com/minikube/releases/latest/minikube-linux-amd64 && chmod +x minikube && sudo mv minikube /usr/local/bin/", null);
            }
            System.out.println("Minikube installed successfully.");
        } else {
            System.out.println("Minikube is already installed.");
        }
    }

    private static boolean isCommandAvailable(String command) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (OSInfoUtils.getOSInfo().contains("Windows")) {
            processBuilder.command("cmd.exe", "/c", "where " + command);
        } else {
            processBuilder.command("bash", "-c", "command -v " + command);
        }
        Process process = processBuilder.start();
        process.waitFor();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = reader.readLine();
        return line != null && !line.isEmpty();
    }

    private static void executeCommand(String command, String workingDirectory) throws IOException, InterruptedException {
        System.out.println(command + "..... Executing");
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (OSInfoUtils.getOSInfo().contains("Windows")) {
            processBuilder.command("cmd.exe", "/c", command);
        } else {
            processBuilder.command("bash", "-c", command);
        }
        if (workingDirectory != null) {
            processBuilder.directory(new File(workingDirectory));
        }
        Process process = processBuilder.start();
        process.waitFor();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        while ((line = errorReader.readLine()) != null) {
            System.err.println(line);
        }
        System.out.println(command + "..... Execution done");
    }



        private static String executeCommandAndGetOutput(String command, String workingDirectory) throws IOException, InterruptedException {
            System.out.println(command + "..... Executing");
            ProcessBuilder processBuilder = new ProcessBuilder();
            
            // Detecting the operating system and setting the appropriate command
            if (OSInfoUtils.getOSInfo().contains("Windows")) {
                processBuilder.command("cmd.exe", "/c", command);
            } else {
            	processBuilder.command("sudo", "bash", "-c", command);
            }
            
            // Setting the working directory if provided
            if (workingDirectory != null) {
                processBuilder.directory(new File(workingDirectory));
            }
            
            // Starting the process
            Process process = processBuilder.start();
            
            // Waiting for the process to finish
            process.waitFor();
            
            // Reading standard output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
            }
            
            // Reading error output
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            StringBuilder errorOutput = new StringBuilder();
            while ((line = errorReader.readLine()) != null) {
                errorOutput.append(line).append(System.lineSeparator());
            }
            
            // Printing the outputs for debugging purposes
            if (output.length() > 0) {
                System.out.println("Standard Output: " + output.toString());
            }
            if (errorOutput.length() > 0) {
                System.err.println("Error Output: " + errorOutput.toString());
            }
            
            System.out.println(command + "..... Execution done");
            
            // Return the combined output and error streams
            return (output.toString() + errorOutput.toString()).trim();
        }

        

    private static boolean isMinikubeRunning() throws IOException, InterruptedException {
        String minikubeStatus = executeCommandAndGetOutput("minikube status", null);
        return minikubeStatus.contains("host: Running") && 
        	   minikubeStatus.contains("kubelet: Running") && 
        	   minikubeStatus.contains("apiserver: Running");
    }

    private static boolean isSiteUp(String url) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            CloseableHttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String responseBody = EntityUtils.toString(entity);
                return responseBody.contains("expected content or keyword");
            }
        } catch (ClientProtocolException e) {
            System.out.println("Protocol error: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void stopCluster(String workingDirectory) throws IOException, InterruptedException {
        System.out.println("Stopping Minikube cluster and cleaning up deployments...");
        executeCommand("kubectl delete -f backend-deployment.yaml", workingDirectory);
        executeCommand("kubectl delete -f frontend-deployment.yaml", workingDirectory);
        executeCommand("minikube stop", workingDirectory);

        System.out.println("TRING TRING...! Hope you enjoyed the trip. Happy testing!");
    }
}
