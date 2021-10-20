package com.matthewcasperson.incidentmanagementdemo.controller;

import com.microsoft.graph.models.Team;
import com.microsoft.graph.requests.GraphServiceClient;
import java.util.List;
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
    return client.teams().buildRequest().get().getCurrentPage();
  }
}
