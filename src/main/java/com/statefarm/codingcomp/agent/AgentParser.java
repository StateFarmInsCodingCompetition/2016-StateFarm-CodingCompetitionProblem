package com.statefarm.codingcomp.agent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import java.util.HashSet;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import com.statefarm.codingcomp.bean.Agent;
import com.statefarm.codingcomp.bean.Office;
import com.statefarm.codingcomp.utilities.SFFileReader;
import com.statefarm.codingcomp.bean.Product;

@Component
public class AgentParser {
    @Autowired
    private SFFileReader sfFileReader = new SFFileReader();

    @Cacheable(value = "agents")
        public Agent parseAgent(String fileName) {
            String page = sfFileReader.readFile(fileName);
            Document doc = Jsoup.parse(page);
            Elements spans = doc.getElementsByTag("span");
            Elements names = spans.select("[itemprop=name]");
            if(names.size() != 1)
            {
                throw new RuntimeException("Something bad happened; there are too many or not enough names!");
            }
            String name = names.first().text();
            Agent out = new Agent();
            out.setName(name);
            Elements serviceHolders = doc.getElementsByAttributeValue("aria-label", "Products Offered/Serviced by This Agent").select("li");
            HashSet<Product> products = new HashSet<>();
            serviceHolders.stream().map(e -> e.text()).forEach(s -> products.add(Product.fromValue(s)));
            out.setProducts(products);

            out.setOffices(Office.parseOffices(fileName));
            return out;
        }
}
