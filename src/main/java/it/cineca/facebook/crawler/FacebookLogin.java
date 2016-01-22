package it.cineca.facebook.crawler;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class FacebookLogin {
	
	private final WebDriver driver;
	
	private String user = System.getProperty("fb.user");
	private String password = System.getProperty("gb.pwd");;
	
	public FacebookLogin(WebDriver driver) {
		this.driver = driver;
	}
	
	public void login() {
		driver.findElement(By.id("email")).sendKeys(user);
		driver.findElement(By.id("pass")).sendKeys(password);
		driver.findElement(By.id("loginbutton")).click();
	}
}
