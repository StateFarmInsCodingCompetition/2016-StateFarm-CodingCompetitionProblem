package com.statefarm.codingcomp.bean;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.statefarm.codingcomp.utilities.SFFileReader;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Office {
    private Set<String> languages;
    private String phoneNumber;
    private List<String> officeHours;
    private Address streetAddress;

    public Set<String> getLanguages() {
        return languages;
    }

    public void setLanguages(Set<String> languages) {
        this.languages = languages;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<String> getOfficeHours() {
        return officeHours;
    }

    public void setOfficeHours(List<String> officeHours) {
        this.officeHours = officeHours;
    }

    public Address getAddress() {
        return streetAddress;
    }

    public void setAddress(Address address) {
        this.streetAddress = address;
    }


    public static List<Office> parseOffices(String fileName)
    {
        final Pattern whichFromId = Pattern.compile("locStreetContent_(?<which>[A-Za-z]+\\d*)");
        SFFileReader sfFileReader = new SFFileReader();
        String page = sfFileReader.readFile(fileName);
        Document doc = Jsoup.parse(page);
        Elements addresses = doc.getElementsByAttributeValue("itemprop", "address");
        ArrayList<Office> retval = new ArrayList<>();
        for(Element e : addresses)
        {
            Office currOffice = new Office();
            currOffice.setAddress(Address.parseAddress(e.html()));
            Matcher whichMatcher = whichFromId.matcher(e.getElementsByAttributeValue("itemprop", "streetAddress").first().children().first().id());
            whichMatcher.find();
            String whichOffice = whichMatcher.group("which");
            Pattern languageIds = Pattern.compile("language(?!Label)(?<language>.*?)_" + whichOffice);
            HashSet<String> languages = new HashSet<>();
            for(Element lang : doc.getElementsByAttributeValueMatching("id", languageIds))
            {
                Matcher matcher = languageIds.matcher(lang.id());
                matcher.find();
                languages.add(matcher.group("language"));
            }
            currOffice.setLanguages(languages);

            Pattern phonePattern = Pattern.compile("offNumber_" + whichOffice);
            currOffice.setPhoneNumber(doc.getElementsByAttributeValueMatching("id", phonePattern).first().children().select("span").first().text());

            Pattern openHoursId = Pattern.compile("officeHoursContent_" + whichOffice + "_\\d+");
            List<String> openHours = doc.getElementsByAttributeValueMatching("id", openHoursId).stream().map(el -> el.text()).collect(Collectors.toList());
            currOffice.setOfficeHours(openHours);
            retval.add(currOffice);
        }
        return retval;
    }
}
