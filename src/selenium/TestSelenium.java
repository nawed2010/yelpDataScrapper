package selenium;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import org.openqa.selenium.firefox.FirefoxDriver;

public class TestSelenium {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	
	WebDriver driver=new FirefoxDriver();
	driver.get("https://www.google.com/");

	List<WebElement> list = driver.findElements(By.tagName("img"));
	System.out.println(list.size());
	
	}

}
