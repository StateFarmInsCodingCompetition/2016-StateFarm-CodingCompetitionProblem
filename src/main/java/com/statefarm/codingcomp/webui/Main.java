package com.statefarm.codingcomp.webui;

import com.statefarm.codingcomp.agent.AgentLocator;
import com.statefarm.codingcomp.agent.AgentParser;
import com.statefarm.codingcomp.bean.Agent;
import com.statefarm.codingcomp.configuration.CodingCompetitionConfiguration;
import com.statefarm.codingcomp.utilities.SFFileReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.get;


@Component
public class Main {
    public static void main(String[] args) {

        ApplicationContext ctx =
                new AnnotationConfigApplicationContext(CodingCompetitionConfiguration.class);
        AgentLocator agentLocator = ctx.getBean(AgentLocator.class);
        SFFileReader sfFileReader = ctx.getBean(SFFileReader.class);
        AgentParser agentParser = ctx.getBean(AgentParser.class);

        get("/", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            return new ModelAndView(model, "webui/search.vm");
        }, new VelocityTemplateEngine());

        get("/search", (req, res) -> {
            String firstName = req.queryParams("firstName");
            String lastName = req.queryParams("lastName");

            List<Agent> agents = agentLocator.getAgentsByName(firstName, lastName);

            Map<String, Object> model = new HashMap<>();
            model.put("agents", agents);
            return new ModelAndView(model, "webui/list.vm");
        }, new VelocityTemplateEngine());
    }
}