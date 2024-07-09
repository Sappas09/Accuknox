package org.selenium.deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@SuppressWarnings("deprecation")
public class DeployServices {
    @SuppressWarnings("deprecation")
	public static void main(String[] args) {
        try (@SuppressWarnings("deprecation")
		KubernetesClient client = new DefaultKubernetesClient()) {
            File deploymentDir = new File("qa-test/Deployment");
            File[] yamlFiles = deploymentDir.listFiles((dir, name) -> name.endsWith(".yaml") || name.endsWith(".yml"));

            if (yamlFiles != null) {
                for (File yamlFile : yamlFiles) {
                    try (InputStream is = new FileInputStream(yamlFile)) {
                        client.load(is).createOrReplace();
                        System.out.println("Applied: " + yamlFile.getName());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                System.out.println("No YAML files found in the Deployment directory.");
            }
        } catch (KubernetesClientException e) {
            e.printStackTrace();
        }
    }
}
