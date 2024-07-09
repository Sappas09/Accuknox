package org.selenium.tests;

import org.selenium.annotations.FrameworkAnnotation;
import org.selenium.base.BaseTest;
import org.selenium.constants.PageConstants;
import org.selenium.enums.AuthorType;
import org.selenium.enums.CategoryType;
import org.selenium.pages.HomePage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class HomePageTest extends BaseTest {

	@FrameworkAnnotation(author = { AuthorType.APPAS }, category = { CategoryType.SANITY,
			CategoryType.BVT,CategoryType.REGRESSION })
	@Test(groups = { "SANITY", "BVT", "REGRESSION" })
	public void validateBackendConnection() {

		HomePage homePage = new HomePage(getDriver());
		
		Assert.assertEquals(homePage.loadPage().getBackendString(),PageConstants.homePageString);

	}

}
