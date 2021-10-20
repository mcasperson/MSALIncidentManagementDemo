package com.matthewcasperson.incidentmanagementdemo.controller;

import com.microsoft.graph.models.Team;
import com.microsoft.graph.requests.GraphServiceClient;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IncidentController {

  @Autowired
  GraphServiceClient<Request> client;


  @GetMapping("/")
  public String getIndex() {
    return "index";
  }

  @GetMapping("/update")
  public String getUpdate() {
    return "update";
  }

  @GetMapping("/create")
  public ModelAndView getCreate(HttpServletRequest request) {

    /*final OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) SecurityContextHolder
        .getContext()
        .getAuthentication();

    final OAuth2AuthorizedClient client = clientRepository
        .loadAuthorizedClient(oauth2Token.getAuthorizedClientRegistrationId(), oauth2Token, request);

    final String accessToken = client.getAccessToken().getTokenValue();*/


    final ModelAndView mav = new ModelAndView("create");
    mav.addObject("teams", getTeams());
    return mav;
  }

  @PostMapping("/create")
  public String postCreate(@RequestParam final String channel) {
    createChannel(channel);
    return "update";
  }

  private List<Team> getTeams() {
    return client
        .teams()
        .buildRequest()
        .get()
        .getCurrentPage();
  }

  private void createChannel(final String channel) {

  }
}
