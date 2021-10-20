package com.matthewcasperson.incidentmanagementdemo.controller;

import com.microsoft.graph.models.Team;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.TeamCollectionPage;
import java.util.List;
import java.util.Optional;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IncidentRestController {

  @Autowired
  GraphServiceClient<Request> client;

  @GetMapping("/api/teams")
  public List<Team> getUpdate() {
    return Optional.ofNullable(client
        .me()
        .joinedTeams()
        .buildRequest()
        .get())
        .map(TeamCollectionPage::getCurrentPage)
        .orElse(List.of());
  }
}
