package com.matthewcasperson.incidentmanagementdemo.providers;


import com.azure.spring.autoconfigure.aad.AADAuthenticationProperties;
import com.matthewcasperson.onenotebackend.providers.OboAuthenticationProvider;
import com.microsoft.graph.requests.GraphServiceClient;
import java.util.Set;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;

@Configuration
public class GraphClientConfiguration {

  @Autowired
  AADAuthenticationProperties azureAd;

  @Autowired
  OAuth2AuthorizedClientService clientService;

  @Bean
  public GraphServiceClient<Request> getClient() {
    return GraphServiceClient.builder()
        .authenticationProvider(new OboAuthenticationProvider(
            Set.of("https://graph.microsoft.com/Channel.Create",
                "https://graph.microsoft.com/ChannelMember.ReadWrite.All",
                "https://graph.microsoft.com/ChannelMessage.Send"),
            azureAd.getTenantId(),
            azureAd.getClientId(),
            azureAd.getClientSecret(),
            clientService))
        .buildClient();
  }
}