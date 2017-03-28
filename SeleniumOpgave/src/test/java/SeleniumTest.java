/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import static com.jayway.restassured.RestAssured.given;
import java.util.List;
import static org.hamcrest.CoreMatchers.is;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 *
 * @author Frederik
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SeleniumTest {

    static WebDriver driver;

    public SeleniumTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\Frederik\\Documents\\Drivers\\chromedriver.exe");
        given().get("http://localhost:3000/reset");
        driver = new ChromeDriver();
        driver.get("http://localhost:3000");
    }

    @AfterClass
    public static void tearDownClass() {
        //for some reason it quits too fast, so i commented it out
        //driver.quit();
        given().get("http://localhost:3000/reset");
    }

    @Test
    public void test1() {
        //make sure the site is loaded
        (new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                WebElement tbody = driver.findElement(By.id("tbodycars"));
                List<WebElement> trows = tbody.findElements(By.tagName("tr"));
                
                //assert that there are 5 elements in the table
                return trows.size() == 5;
            }
        });
    }

    @Test
    public void test2() {
        //make sure the body is updated
        (new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                //type 2002 in the filter
                driver.findElement(By.id("filter")).sendKeys("2002");
                WebElement tbody = driver.findElement(By.id("tbodycars"));
                List<WebElement> trows = tbody.findElements(By.tagName("tr"));
                
                //assert that only 2 elements show up with 2002 in the filter
                return trows.size() == 2;
            }
        });
    }

    @Test
    public void test3() {
        //delete everything in the filter
        driver.findElement(By.id("filter")).sendKeys(Keys.CONTROL + "a");
        driver.findElement(By.id("filter")).sendKeys(Keys.DELETE);
        
        //make sure the body is updated
        (new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                WebElement tbody = driver.findElement(By.id("tbodycars"));
                List<WebElement> trows = tbody.findElements(By.tagName("tr"));
                
                //assert that the size is still 5
                return trows.size() == 5;
            }
        });
    }

    @Test
    public void test4() {
        //click the sort button
        driver.findElement(By.id("h_year")).click();
        
        //make sure the body is updated
        (new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                WebElement tbody = driver.findElement(By.id("tbodycars"));
                List<WebElement> trows = tbody.findElements(By.tagName("tr"));
                //find the first and last element knowing that there should be 5
                WebElement row1 = trows.get(0).findElements(By.tagName("td")).get(0);
                WebElement row5 = trows.get(4).findElements(By.tagName("td")).get(0);

                //assert that the first is 938 and the last is 940
                return row1.getText().equals("938") && row5.getText().equals("940");
            }
        });
    }

    @Test
    public void test5() throws Exception {
        WebElement tbody = driver.findElement(By.id("tbodycars"));
        List<WebElement> trows = tbody.findElements(By.tagName("tr"));
        WebElement row = null;
        //find the row with id 938
        for (int i = 0; i < trows.size(); i++) {
            if (trows.get(i).findElements(By.tagName("td")).get(0).getText().equals("938")) {
                row = trows.get(i);
            }
        }
        //if it couldnt find it. throw an exception, to make the test fail
        if (row == null) {
            throw new Exception();
        }

        //click the edit button
        row.findElements(By.tagName("a")).get(0).click();

        //make sure that body has updatet
        (new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                List<WebElement> details = driver.findElements(By.tagName("legend"));
                String carId = details.get(0).findElement(By.tagName("span")).getText();
                if (carId.substring(1, carId.length() - 1).equals("938")) {
                    return true;
                } else {
                    return false;
                }
            }
        });

        WebElement des = driver.findElements(By.tagName("fieldset")).get(0).findElement(By.id("description"));
        //delete whats already in the description
        des.sendKeys(Keys.CONTROL + "a");
        des.sendKeys(Keys.DELETE);
        //send "Cool car"
        des.sendKeys("Cool car");
        //click save
        driver.findElements(By.tagName("fieldset")).get(0).findElement(By.id("save")).click();

        //make sure the body has updated and complete when the new description
        //is equal to "Cool car"
        (new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                WebElement tbody = driver.findElement(By.id("tbodycars"));
                List<WebElement> trows = tbody.findElements(By.tagName("tr"));
                WebElement row = null;
                //find the row with id 938
                for (int i = 0; i < trows.size(); i++) {
                    if (trows.get(i).findElements(By.tagName("td")).get(0).getText().equals("938")) {
                        row = trows.get(i);
                    }
                }
                if (row.findElements(By.tagName("td")).get(5).getText().equals("Cool car")) {
                    return true;
                } else {
                    return false;
                }

            }
        });

    }

    @Test
    public void test6() {
        //click new car
        driver.findElement(By.id("new")).click();
        //click save
        driver.findElement(By.id("save")).click();

        //make sure the body is updated and error message is as expected
        (new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                if (driver.findElement(By.id("submiterr")).getText().equals("All fields are required")) {
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    @Test
    public void test7() {
        //click new car
        driver.findElement(By.id("new")).click();
        
        //fill form
        driver.findElement(By.id("year")).sendKeys("2008");
        driver.findElement(By.id("registered")).sendKeys("2002-5-5");
        driver.findElement(By.id("make")).sendKeys("Kia");
        driver.findElement(By.id("model")).sendKeys("Rio");
        driver.findElement(By.id("description")).sendKeys("As new");
        driver.findElement(By.id("price")).sendKeys("31000");
        
        //click save car
        driver.findElement(By.id("save")).click();
        
        //make sure the car was added
        (new WebDriverWait(driver, 10)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                WebElement tbody = driver.findElement(By.id("tbodycars"));
                List<WebElement> trows = tbody.findElements(By.tagName("tr"));
                return trows.size() == 6;
            }
        });
    }
}
