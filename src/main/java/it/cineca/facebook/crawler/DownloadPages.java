package it.cineca.facebook.crawler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class DownloadPages {

	public static final String CROWLED_DIR = "/home/mvit/fb_crawl/";

	public static void main(String[] args) throws InterruptedException, IOException {
		WebDriver driver = new FirefoxDriver();
		FacebookLogin loginPage = new FacebookLogin(driver);
		FacebookPageCrawler crawler = new FacebookPageCrawler(driver);
		
		driver.get("https://www.facebook.com/");
		Thread.sleep(2 * 1000);
		loginPage.login();
		Thread.sleep(2 * 1000);
		
		dumpFbPage(driver, crawler, "TetraPak");
			
		//driver.quit();

	}

	private static void dumpFbPage(WebDriver driver, FacebookPageCrawler crawler, String page) throws InterruptedException, IOException {
		FacebookPagePostExtractor postExtractor = new FacebookPagePostExtractor(driver);
		
		driver.get("https://www.facebook.com/" + page + "/");
		Thread.sleep(5 * 1000);
		
		for(WebElement postWrapperElement : crawler) {
			savePost(CROWLED_DIR + page + "/", postExtractor.extractInfo(postWrapperElement));
		}
	}

	private static void savePost(String dirPath, FacebookPostInfo extractInfo) throws IOException {
		
		(new File(dirPath)).mkdirs();
		
		String filePath = dirPath + extractInfo.getId() + ".html";
		
		try(FileWriter fw = new FileWriter(filePath)) {
			fw.append(extractInfo.getAll());
		};
	}

	
}
