package com.matthewcasperson.incidentmanagementdemo.controller;

import com.google.gson.JsonPrimitive;
import com.microsoft.graph.http.BaseCollectionPage;
import com.microsoft.graph.models.BodyType;
import com.microsoft.graph.models.Channel;
import com.microsoft.graph.models.ChannelMembershipType;
import com.microsoft.graph.models.ChatMessage;
import com.microsoft.graph.models.ConversationMember;
import com.microsoft.graph.models.ItemBody;
import com.microsoft.graph.models.Team;
import com.microsoft.graph.models.User;
import com.microsoft.graph.requests.GraphServiceClient;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
  public List<User> getUsers() {
    return Optional.ofNullable(client
            .users()
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
    channel.membershipType = ChannelMembershipType.PRIVATE;

    final List<Channel> existingChannel = Optional.ofNullable(client
            .teams(team)
            .channels()
            .buildRequest()
            .filter("displayName eq '" + newChannelBody.channelName + "'")
            .get())
        .map(BaseCollectionPage::getCurrentPage)
        .orElse(List.of());

    final Channel newChannel = existingChannel.isEmpty()
        ? client
          .teams(team)
          .channels()
          .buildRequest()
          .post(channel)
        : existingChannel.get(0);

    for (final String memberId : newChannelBody.members) {
      final ConversationMember member = new ConversationMember();
      member.oDataType = "#microsoft.graph.aadUserConversationMember";
      member.additionalDataManager().put(
          "user@odata.bind",
          new JsonPrimitive("https://graph.microsoft.com/v1.0/users('" +
              URLEncoder.encode(memberId, StandardCharsets.UTF_8) + "')"));

      try {
        // add the user to the team
        client
            .teams(team)
            .members()
            .buildRequest()
            .post(member);

        // add the user to the channel
        client
            .teams(team)
            .channels(newChannel.id)
            .members()
            .buildRequest()
            .post(member);
      } catch (final Exception ex) {
        System.out.println(ex);
        ex.printStackTrace();
      }
    }

    return newChannel;
  }

  @PostMapping("/api/teams/{team}/channel/{channel}/message")
  public void createMessage(
      @PathVariable("team") final String team,
      @PathVariable("channel") final String channel,
      @RequestBody final String message) {

    final ChatMessage chatMessage = new ChatMessage();
    chatMessage.body = new ItemBody();
    chatMessage.body.content = message.replaceAll("\n", "<br/>");
    chatMessage.body.contentType = BodyType.HTML;

    client
        .teams(team)
        .channels(channel)
        .messages()
        .buildRequest()
        .post(chatMessage);
  }
}
