package com.matthewcasperson.incidentmanagementdemo.controller;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;

import com.microsoft.graph.models.Channel;
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

  @GetMapping("/create")
  public ModelAndView getCreate(@RegisteredOAuth2AuthorizedClient("api") final OAuth2AuthorizedClient client) {

    final ModelAndView mav = new ModelAndView("create");

    try {
      final List teams = webClient
          .get()
          .uri("http://localhost:8080/api/teams/")
          .attributes(oauth2AuthorizedClient(client))
          .retrieve()
          .bodyToMono(List.class)
          .block();

      final List users = webClient
          .get()
          .uri("http://localhost:8080/api/users/")
          .attributes(oauth2AuthorizedClient(client))
          .retrieve()
          .bodyToMono(List.class)
          .block();

      mav.addObject("teams", teams);
      mav.addObject("users", users);
    } catch (final Exception ex) {
      mav.addObject("teams", List.of());
      mav.addObject("users", List.of());
    }
    return mav;
  }

  @PostMapping("/create")
  public ModelAndView postCreate(
      @RegisteredOAuth2AuthorizedClient("api") final OAuth2AuthorizedClient client,
      @RequestParam final String channel,
      @RequestParam final String team,
      @RequestParam final List<String> users) {

    final ModelAndView mav = new ModelAndView("update");

    final Channel newChannel = webClient
        .post()
        .uri("http://localhost:8080/api/teams/" + team + "/channel")
        .bodyValue(new IncidentRestController.NewChannelBody(channel, users))
        .attributes(oauth2AuthorizedClient(client))
        .retrieve()
        .bodyToMono(Channel.class)
        .block();

    mav.addObject("channel", newChannel);
    return mav;
  }
}
