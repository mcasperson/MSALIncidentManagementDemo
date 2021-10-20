package com.matthewcasperson.incidentmanagementdemo.providers;

import com.azure.spring.autoconfigure.aad.AADAuthenticationProperties;
import com.microsoft.graph.requests.GraphServiceClient;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.annotation.RequestScope;

@Configuration
public class GraphClientConfiguration {

  @Autowired
  AADAuthenticationProperties azureAd;

  @Autowired
  OAuth2AuthorizedClientRepository clientRepository;

  @Bean
  @RequestScope
  public GraphServiceClient<Request> getClient(HttpServletRequest request) {
    return GraphServiceClient.builder()
        .authenticationProvider(new OboAuthenticationProvider(
            Set.of("https://graph.microsoft.com/Channel.Create",
                "https://graph.microsoft.com/ChannelMember.ReadWrite.All",
                "https://graph.microsoft.com/ChannelMessage.Send"),
            azureAd.getTenantId(),
            azureAd.getClientId(),
            azureAd.getClientSecret(),
            clientRepository,
            request))
        .buildClient();
  }
}