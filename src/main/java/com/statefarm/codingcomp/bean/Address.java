package com.statefarm.codingcomp.bean;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Address {
	private String line1;
	private String line2;
	private String city;
	private USState state;
	private String postalCode;

	public String getLine1() {
		return line1;
	}

	public void setLine1(String line1) {
		this.line1 = line1;
	}

	public String getLine2() {
		return line2;
	}

	public void setLine2(String line2) {
		this.line2 = line2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public USState getState() {
		return state;
	}

	public void setState(USState state) {
		this.state = state;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}


        public static Address parseAddress(String addressHtml)
        {
            Address retval = new Address();

            Document doc = Jsoup.parse(addressHtml);
            Pattern firstLine = Pattern.compile("locStreetContent_.");
                                                                                                // We need to capture the <br> if
                                                                                                // it's there
            String streetNumber = doc.getElementsByAttributeValueMatching("id", firstLine)
                .first()
                .html();
            if(streetNumber.indexOf("<br>") != -1)
            {
                String[] lines = streetNumber.replace(",", "").split("<br>");
                retval.setLine1(lines[0]);
                retval.setLine2(lines[1]);
            }
            else
            {
                retval.setLine1(streetNumber);
            }

            retval.setCity(doc.getElementsByAttributeValue("itemprop", "addressLocality").first().text().replace(",",""));

            retval.setState(USState.fromValue(doc.getElementsByAttributeValue("itemprop", "addressRegion").first().text()));
            
            retval.setPostalCode(doc.getElementsByAttributeValue("itemprop", "postalCode").first().text());
            return retval;
        }
}
