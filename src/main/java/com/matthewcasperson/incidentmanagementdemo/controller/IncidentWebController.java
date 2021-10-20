package com.matthewcasperson.incidentmanagementdemo.controller;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IncidentWebController {

  @Autowired
  WebClient webClient;

  @GetMapping("/")
  public String getIndex() {
    return "index";
  }

  @GetMapping("/update")
  public String getUpdate() {
    return "update";
  }

  @GetMapping("/create")
  public ModelAndView getCreate(@RegisteredOAuth2AuthorizedClient("api") final OAuth2AuthorizedClient client) {

    final List teams = webClient
        .get()
        .uri("http://localhost:8080/api/teams/")
        .attributes(oauth2AuthorizedClient(client))
        .retrieve()
        .bodyToMono(List.class)
        .block();

    final ModelAndView mav = new ModelAndView("create");
    mav.addObject("teams", teams);
    return mav;
  }

  @PostMapping("/create")
  public String postCreate(@RequestParam final String channel) {
    return "update";
  }
}
