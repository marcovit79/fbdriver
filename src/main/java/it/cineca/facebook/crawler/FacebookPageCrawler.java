package it.cineca.facebook.crawler;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class FacebookPageCrawler implements Iterable<WebElement> {

	private final WebDriver driver;
	
	public FacebookPageCrawler(WebDriver driver)  {
		this.driver = driver;
	}
	
	public FacebookPageIterator iterator() {
		return new FacebookPageIterator(driver);
	}
	
	private static class FacebookPageIterator implements Iterator<WebElement> {
		
		private final WebDriver driver;
		
		private Iterator<WebElement> postSectionWrappersElements = null; 
		private WebElement currentPostSection = null;
		private WebElement currentPost = null;
		
		public FacebookPageIterator(WebDriver driver)  {
			this.driver = driver;
				
			WebElement mainElement = driver.findElement(By.id("pagelet_timeline_main_column"));
			WebElement contentElement = mainElement.findElement(By.xpath("./div[starts-with(@id, 'PagePostsPagelet_')]"));
			
			this.postSectionWrappersElements = contentElement.findElements(By.xpath(".//div[starts-with(@id, 'PagePostsSectionPagelet-')]")).iterator();
			
			initNextSession(); // init first section
		}

		private void initNextSession() {
			if(!this.postSectionWrappersElements.hasNext()) {
				this.currentPostSection = null;
				this.currentPost = null;
			}
			else {
				this.currentPostSection = this.postSectionWrappersElements.next();
				this.currentPost = getFirstPostInSection(currentPostSection);
			}
		}
		
		private void consumePostInSection() {
			WebElement postWrapperElement = getNextSibling(this.currentPost);
			
			if(postWrapperElement != null) {
				String dateTime = postWrapperElement.getAttribute("data-time");
				System.out.println("Data time " + dateTime);
				if(dateTime==null) {
					postWrapperElement = getFirstPostInSection(postWrapperElement);
				}
			}
			this.currentPost = postWrapperElement;
		}
		
		private void consumeCurrentPost() {
			consumePostInSection();
			if(currentPost == null) {
				initNextSession();
			}
		}
		
		@Override
		public WebElement next() {
			if(currentPost == null) {
				throw new NoSuchElementException("Iterator ended");
			}
			WebElement toRet = currentPost;
			
			consumeCurrentPost();
			scrollToBottom(driver);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			return toRet;
		}

		@Override
		public boolean hasNext() {
			return currentPost!=null;
		}
		
	}

	
	private static WebElement getFirstPostInSection(WebElement postSectionWrapperElement) {
		List<WebElement> wrappedPosts = postSectionWrapperElement.findElements(By.xpath(".//div[@data-time]"));
		return wrappedPosts.isEmpty() ? null : wrappedPosts.get(0);
	}

	private static WebElement getNextSibling(WebElement postWrapperElement) {
		List<WebElement> siblings = postWrapperElement.findElements(By.xpath("following-sibling::*"));
		WebElement result = siblings.isEmpty() ? null : siblings.get(0);
		
		return result;
	}

	private static void scrollToBottom(WebDriver driver) {
		((JavascriptExecutor)driver).executeScript(" window.scrollTo(0, document.body.scrollHeight)");
	}
}
