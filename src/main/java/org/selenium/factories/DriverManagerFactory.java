package org.selenium.factories;

import org.selenium.driver.*;
import org.selenium.enums.DriverType;

public class DriverManagerFactory {

	public static DriverManager_OC getManager(DriverType driverType) {

		switch (driverType) {

		case CHROME: {
			return new DriverManagerChrome();
		}
		
		default:
			throw new IllegalArgumentException("Invalid Driver: " + driverType);
		}

	}

}
