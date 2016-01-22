package it.cineca.facebook.crawler;

import java.util.Date;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class FacebookPagePostExtractor {

	private final WebDriver driver;
	
	public FacebookPagePostExtractor(WebDriver driver)  {
		this.driver = driver;
	}
	
	public FacebookPostInfo extractInfo(WebElement postWrapperElement) {
		return extractPostInformation(driver, postWrapperElement);
	}
	
	
	private static FacebookPostInfo extractPostInformation(WebDriver driver, WebElement postWrapperElement) {
		String id = postWrapperElement.getAttribute("id");
		String date = postWrapperElement.getAttribute("data-time");
		String likes;
		try {
			likes = postWrapperElement.findElement(By.className("UFILikeSentenceText")).getText();
		}
		catch(RuntimeException exc) {
			likes = null;
			exc.printStackTrace();
		}
		
		expandComments(driver, postWrapperElement);
		
		FacebookPostInfo result = new FacebookPostInfo();
		result.setId(id);
		result.setLike(likes);
		result.setDate( (new Date( Long.parseLong(date) * 1000 )).toString() );
		
		String postBodyString = (String) ((JavascriptExecutor)driver).executeScript("return document.getElementById('" + id + "').outerHTML");
		result.setAll(postBodyString);
		
		return result;
	}

	private static void expandComments(WebDriver driver, WebElement postWrapperElement) {
		WebElement commentExpander = getOneCommentExpander(postWrapperElement);
		while(commentExpander != null) {
			
			((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true); window.scrollBy(0, -100)", commentExpander);
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			System.out.println("######### lastCommentRole:" + commentExpander.getText());
			commentExpander.click();
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			commentExpander = getOneCommentExpander(postWrapperElement);
		}
		
	}

	private static WebElement getOneCommentExpander(WebElement postWrapperElement) {
		WebElement commentExpander;
		try {
			commentExpander = postWrapperElement.findElement(By.xpath(".//div[@data-ft='{\"tn\":\"Q\"}']//a[@role='button']"));
		}
		catch(RuntimeException exc) {
			commentExpander = null;
		}
		return commentExpander;
	}

}
