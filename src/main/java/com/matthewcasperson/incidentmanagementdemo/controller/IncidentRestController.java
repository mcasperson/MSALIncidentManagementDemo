package com.matthewcasperson.incidentmanagementdemo.controller;

import com.microsoft.graph.http.BaseCollectionPage;
import com.microsoft.graph.models.ConversationMember;
import com.microsoft.graph.models.Team;
import com.microsoft.graph.models.User;
import com.microsoft.graph.requests.ConversationCollectionPage;
import com.microsoft.graph.requests.ConversationMemberCollectionPage;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.TeamCollectionPage;
import com.microsoft.graph.requests.UserCollectionRequestBuilder;
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
  public List<Team> getTeams() {
    return Optional.ofNullable(client
        .me()
        .joinedTeams()
        .buildRequest()
        .get())
        .map(BaseCollectionPage::getCurrentPage)
        .orElse(List.of());
  }

  @GetMapping("/api/users")
  public List<User> getMembers(final String team) {
    return Optional.ofNullable(client
            .users()
            .buildRequest()
            .get())
        .map(BaseCollectionPage::getCurrentPage)
        .orElse(List.of());
  }
}
