package org.selenium.standalone;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class FrontendTest {
    public static void main(String[] args) {
        WebDriver driver = new ChromeDriver();

        try {
            driver.get("http://192.168.49.2:30572/"); 

            Thread.sleep(5000);

            WebElement greetingMessage = driver.findElement(By.xpath("//html/body/h1"));
            		
            assert greetingMessage.getText().equals("Hello from the Backend!");

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }
}

