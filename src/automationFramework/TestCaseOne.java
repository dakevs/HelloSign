package automationFramework;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;



enum Item {
	   DRILL(100.00, "a0Gf40000005CctEAE", "Drill"), ///
	   WRENCH(15.00, "a0Gf40000005CcuEAE", "Wrench"), ///
	   HAMMER(10.00, "a0Gf40000005CcvEAE", "Hammer"); ///
	
	private final double _price;
	private final String _itemID;
	private final String _itemDisplayName;
	
	Item(double price, String itemID, String itemDisplayName){
		this._price = price;
		this._itemID = itemID;
		this._itemDisplayName = itemDisplayName;
	}
	
	double getItemPrice() {
		return _price;	
	}
	
	String getItemID() {
		return _itemID;		
	}
	
	String getItemName() {
		return _itemDisplayName;			
	}
		
	
}


public class TestCaseOne {

	 static WebDriver driver;
	
	
public static void main(String[] args) {
		
		// TODO Auto-generated method stub


		System.setProperty("webdriver.chrome.driver", "chromedriver.exe");

		driver = new ChromeDriver();	

		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

		Item testItem1 = Item.WRENCH;
		Item testItem2 = Item.DRILL;
		
		driver.get("http://skuid-qa-eval.force.com/WebOrderScreen");

		AddItem(testItem1);
		AddItem(testItem2);
		ChangeItemQuantity(testItem1, 3);		
		ChangeItemQuantity(testItem2, 3);

		driver.findElement(By.xpath("//span[text()='Payment Information']")).click();					
		VerifyOrderSummaryItem(testItem1, 3);
		VerifyOrderSummaryItem(testItem2, 3);
		VerifyPaymentInformationRequiredFields();
		PopulatePaymentInformationFields("1111111111111111", "1221", "123");
		
		driver.findElement(By.xpath("//span[text()='Process Payment']")).click();
		VerifyOrderTotal();
		driver.findElement(By.xpath("//span[text()='New Order']")).click();

		driver.findElement(By.xpath("//div[text()='Catalog']"));
		driver.findElement(By.xpath("//div[text()='Shopping Cart']"));
		 
	}
	

	public static void VerifyOrderTotal() {
	

		double orderTotal = 0;
		WebDriverWait wait = new WebDriverWait(driver, 40);
		wait.until(ExpectedConditions.elementToBeClickable(By.id("sk-PCSzt-246")));

		WebElement orderTotalTable = driver.findElement(By.id("sk-PCSzt-246")).findElement(By.xpath(".//tbody[@class='nx-list-contents']"));


		List<WebElement> orderRows = orderTotalTable.findElements(By.tagName("tr"));
	

		
		for(int i=0; i < orderRows.size(); i++) {

			String rowAmount = orderRows.get(i).findElements(By.tagName("td")).get(3).getText();
			orderTotal = Double.parseDouble(rowAmount.substring(1)) + orderTotal;		
		}
			
		String orderTotalDisplayedAboveList = driver.findElement(By.id("sk-PMW9H-549")).findElement(By.xpath(".//div[@class='nx-fieldtext']")).getText().substring(1);

		
		Assert.assertEquals(orderTotal, Double.parseDouble(orderTotalDisplayedAboveList), 0.0);		
	
	}



	public static void PopulatePaymentInformationFields(String cardNumber, String expirationDate, String securityCode) {
		
		WebElement cardNumberDiv = driver.findElement(By.xpath("//div[text()='Card Number']")).findElement(By.xpath("../.."));
		WebElement expiryDateDiv = driver.findElement(By.xpath("//div[text()='Expiration Date']")).findElement(By.xpath("../.."));
		WebElement securityCodeDiv = driver.findElement(By.xpath("//div[text()='Security Code']")).findElement(By.xpath("../.."));

		cardNumberDiv.findElement(By.tagName("input")).clear();
		cardNumberDiv.findElement(By.tagName("input")).sendKeys(cardNumber);
		expiryDateDiv.findElement(By.tagName("input")).clear();
		expiryDateDiv.findElement(By.tagName("input")).sendKeys(expirationDate);
		securityCodeDiv.findElement(By.tagName("input")).clear();
		securityCodeDiv.findElement(By.tagName("input")).sendKeys(securityCode);


		
	}


	public static void VerifyPaymentInformationRequiredFields() {
	
		
		driver.findElement(By.xpath("//span[text()='Process Payment']")).click();					


		WebElement cardNumberDiv = driver.findElement(By.xpath("//div[text()='Card Number']")).findElement(By.xpath("../.."));
		Assert.assertEquals("This Field is Required", cardNumberDiv.findElement(By.xpath(".//div[@class='nx-error-inline']")).getText());	
		System.out.println("\"This Field is Required\" error message found for missing card number");
		
		WebElement expiryDateDiv = driver.findElement(By.xpath("//div[text()='Expiration Date']")).findElement(By.xpath("../.."));
		Assert.assertEquals("This Field is Required", expiryDateDiv.findElement(By.xpath(".//div[@class='nx-error-inline']")).getText());
		System.out.println("\"This Field is Required\" error message found for missing expiration date");

		
		WebElement securityCodeDiv = driver.findElement(By.xpath("//div[text()='Security Code']")).findElement(By.xpath("../.."));
		Assert.assertEquals("This Field is Required", securityCodeDiv.findElement(By.xpath(".//div[@class='nx-error-inline']")).getText());
		System.out.println("\"This Field is Required\" error message found for missing security code number");
		
		driver.findElement(By.className("nx-error"));
		System.out.println("General error message found: " + driver.findElement(By.className("nx-error")).getText());
		

		
	}

	
	public static void AddItem(Item item) {
		
		
		WebDriverWait wait = new WebDriverWait(driver, 40);
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='(sum)']")));

		
		
		
		//first find the link to the item we want
		WebElement itemLink = driver.findElement(By.xpath(String.format("//a[(@href='/%s')]", item.getItemID())));
		WebElement itemParentRow = itemLink.findElement(By.xpath("../../../.."));

		itemParentRow.findElement(By.tagName("i")).click(); //now that we found the parent row of the item we want, click the button to add it
		


		driver.findElement(By.id("shopping-cart")).findElement(By.xpath(String.format(".//a[(@href='/%s')]", item.getItemID())));
		System.out.println("Item ENUM found in shopping cart: " + item);
		
		///for debugging 
		/*
		System.out.println(item.getItemID());
		System.out.println(item.getItemPrice());
		System.out.println(itemParentRow.getText());
		*/
		

		
		
	}
	
	
	public static void ChangeItemQuantity(Item item, Integer quantity)  {
		
		
		
		WebDriverWait wait = new WebDriverWait(driver, 40);
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='(sum)']")));
		
		
		
		WebElement itemLink = 		driver.findElement(By.id("shopping-cart")).findElement(By.xpath(String.format(".//a[(@href='/%s')]", item.getItemID())));
		WebElement itemParentRow = itemLink.findElement(By.xpath("../../../.."));


		System.out.println("Old item count: " + itemParentRow.findElement((By.xpath(".//input[@inputmode='numeric']"))).getAttribute("value"));
		itemParentRow.findElement((By.xpath(".//input[@inputmode='numeric']"))).clear();
		itemParentRow.findElement((By.xpath(".//input[@inputmode='numeric']"))).sendKeys(quantity.toString());		
		System.out.println("New item count: " + itemParentRow.findElement((By.xpath(".//input[@inputmode='numeric']"))).getAttribute("value"));
		
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@id='shopping-cart']//td[@class='nx-summary']")));

	}
	
	
	public static void VerifyOrderSummaryItem(Item item, Integer quantity) {
		

		WebElement itemLink = 		driver.findElement(By.id("order-summary")).findElement(By.xpath(String.format(".//a[(@href='/%s')]", item.getItemID())));
		WebElement itemParentRow = itemLink.findElement(By.xpath("../../../../.."));
		System.out.println("Item row in order summary: " + itemParentRow.getText());		

		//name check
		Assert.assertTrue(itemParentRow.getText().contains(item.getItemName()));
		System.out.println("Item name found in order summary row: "+ item.getItemName());

		//item total check
		Assert.assertTrue(itemParentRow.getText().contains("$"+(item.getItemPrice()* quantity)));
		System.out.println("Item total found in order summary row: $"+(item.getItemPrice()* quantity));		
		//item quantity check
		WebElement quantityCell = itemParentRow.findElements(By.tagName("td")).get(3);
		Assert.assertEquals(itemParentRow.findElements(By.tagName("td")).get(3).getText(), quantity.toString());
		System.out.println("Item quantify found in order summary row: " + quantityCell.getText());
		
		
	}
	
	
}
