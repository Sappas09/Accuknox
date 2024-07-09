package org.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.selenium.enums.WaitStrategy;

public class HomePage extends BasePage {

	public HomePage(WebDriver driver) {
		super(driver);
	}
	
	private final By  greetingMessage = By.xpath("//html/body/h1");


	public HomePage loadPage() {
	    BasePage page = new BasePage(driver);
	    page.load("/");
	    wait.until(ExpectedConditions.presenceOfElementLocated(greetingMessage));
	    return new HomePage(driver);
	}




	public String getBackendString() {
		
		return getString(greetingMessage , WaitStrategy.PRESENCE , "Back end String");
	}

}