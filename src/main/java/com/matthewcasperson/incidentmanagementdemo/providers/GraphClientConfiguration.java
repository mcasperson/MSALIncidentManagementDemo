package com.matthewcasperson.incidentmanagementdemo.providers;

import com.azure.spring.autoconfigure.aad.AADAuthenticationProperties;
import com.microsoft.graph.requests.GraphServiceClient;
import java.util.Set;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GraphClientConfiguration {

  @Autowired
  AADAuthenticationProperties azureAd;

  @Bean
  public GraphServiceClient<Request> getClient() {
    return GraphServiceClient.builder()
        .authenticationProvider(new OboAuthenticationProvider(
            Set.of("https://graph.microsoft.com/Channel.Create",
                "https://graph.microsoft.com/ChannelSettings.Read.All",
                "https://graph.microsoft.com/ChannelMember.ReadWrite.All",
                "https://graph.microsoft.com/ChannelMessage.Send",
                "https://graph.microsoft.com/Team.ReadBasic.All",
                "https://graph.microsoft.com/TeamMember.ReadWrite.All",
                "https://graph.microsoft.com/User.ReadBasic.All"),
            azureAd.getTenantId(),
            azureAd.getClientId(),
            azureAd.getClientSecret()))
        .buildClient();
  }
}