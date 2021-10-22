package com.matthewcasperson.incidentmanagementdemo.controller;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;

import com.azure.core.annotation.QueryParam;
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
  public ModelAndView getCreate(
      @RegisteredOAuth2AuthorizedClient("api") final OAuth2AuthorizedClient client) {

    final ModelAndView mav = new ModelAndView("create");

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

    return mav;
  }

  @PostMapping("/create")
  public ModelAndView postCreate(
      @RegisteredOAuth2AuthorizedClient("api") final OAuth2AuthorizedClient client,
      @RequestParam final String channelName,
      @RequestParam final String team,
      @RequestParam final List<String> users) {

    final ModelAndView mav = new ModelAndView("redirect:/update");

    final Channel newChannel = webClient
        .post()
        .uri("http://localhost:8080/api/teams/" + team + "/channel")
        .bodyValue(new IncidentRestController.NewChannelBody(channelName, users))
        .attributes(oauth2AuthorizedClient(client))
        .retrieve()
        .bodyToMono(Channel.class)
        .block();

    mav.addObject("channelId", newChannel.id);
    mav.addObject("channelName", newChannel.displayName);
    mav.addObject("team", team);
    return mav;
  }

  @GetMapping("/update")
  public ModelAndView getUpdate(
      @QueryParam("team") final String team,
      @QueryParam("channel") final String channelId,
      @QueryParam("channel") final String channelName) {
    final ModelAndView mav = new ModelAndView("update");
    mav.addObject("team", team);
    mav.addObject("channelId", channelId);
    mav.addObject("channelName", channelName);
    return mav;
  }

  @PostMapping("/update")
  public ModelAndView postUpdate(
      @RegisteredOAuth2AuthorizedClient("api") final OAuth2AuthorizedClient client,
      @RequestParam final String channelName,
      @RequestParam final String channelId,
      @RequestParam final String team,
      @RequestParam final String customMessage,
      @RequestParam final String status) {

    final ModelAndView mav = new ModelAndView("update");

    webClient
        .post()
        .uri("http://localhost:8080/api/teams/" + team + "/channel/" + channelId + "/message")
        .bodyValue(status + "\nMessage: " + customMessage)
        .attributes(oauth2AuthorizedClient(client))
        .retrieve()
        .toBodilessEntity()
        .block();

    mav.addObject("channelName", channelName);
    mav.addObject("channelId", channelId);
    mav.addObject("team", team);
    return mav;
  }
}
