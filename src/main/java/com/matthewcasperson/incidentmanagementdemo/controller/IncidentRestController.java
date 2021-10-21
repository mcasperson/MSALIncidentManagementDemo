package com.matthewcasperson.incidentmanagementdemo.controller;

import com.google.gson.JsonPrimitive;
import com.microsoft.graph.http.BaseCollectionPage;
import com.microsoft.graph.models.Channel;
import com.microsoft.graph.models.ConversationMember;
import com.microsoft.graph.models.Team;
import com.microsoft.graph.models.User;
import com.microsoft.graph.requests.GraphServiceClient;
import java.util.List;
import java.util.Optional;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IncidentRestController {

  public static class NewChannelBody {

    public final String channelName;
    public final List<String> members;

    public NewChannelBody(
        final String channelName,
        final List<String> members) {
      this.channelName = channelName;
      this.members = members;
    }
  }

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
  public List<User> getMembers() {
    return Optional.ofNullable(client
            .users()
            .buildRequest()
            .get())
        .map(BaseCollectionPage::getCurrentPage)
        .orElse(List.of());
  }

  @GetMapping("/api/teams/{team}/channels")
  public List<Channel> getChannels(@PathVariable("team") final String teamId) {
    return Optional.ofNullable(client
            .teams(teamId)
            .channels()
            .buildRequest()
            .get())
        .map(BaseCollectionPage::getCurrentPage)
        .orElse(List.of());
  }

  @PostMapping("/api/teams/{team}/channel")
  public Channel createChannel(
      @PathVariable("team") final String team,
      @RequestBody final NewChannelBody newChannelBody) {
    final Channel channel = new Channel();
    channel.displayName = newChannelBody.channelName;

    final Channel newChannel = client
        .teams(team)
        .channels()
        .buildRequest()
        .post(channel);

    for (final String memberId : newChannelBody.members) {
      final ConversationMember member = new ConversationMember();
      //member.id = "https://graph.microsoft.com/v1.0/users('" + memberId + "')";
      member.oDataType = "#microsoft.graph.aadUserConversationMember";
      member.additionalDataManager().put(
          "user@odata.bind",
          new JsonPrimitive("https://graph.microsoft.com/v1.0/users('" + memberId + "')"));

      client
          .teams(team)
          .channels(newChannel.id)
          .members()
          .buildRequest()
          .post(member);
    }

    return newChannel;
  }
}
