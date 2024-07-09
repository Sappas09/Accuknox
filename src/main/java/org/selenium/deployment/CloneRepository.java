/*
 * package org.selenium.deployment;
 * 
 * import org.eclipse.jgit.api.Git; import
 * org.eclipse.jgit.api.errors.GitAPIException;
 * 
 * import java.io.File;
 * 
 * public class CloneRepository { public static void main(String[] args) { try {
 * Git.cloneRepository() .setURI("https://github.com/Vengatesh-m/qa-test")
 * .setDirectory(new File("qa-test")) .call();
 * System.out.println("Repository cloned successfully."); } catch
 * (GitAPIException e) { e.printStackTrace(); } } }
 */