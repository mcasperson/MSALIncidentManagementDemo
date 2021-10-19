package com.matthewcasperson.incidentmanagementdemo.providers;

import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.IClientSecret;
import com.microsoft.aad.msal4j.OnBehalfOfParameters;
import com.microsoft.aad.msal4j.UserAssertion;
import com.microsoft.graph.authentication.BaseAuthenticationProvider;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

public class OboAuthenticationProvider extends BaseAuthenticationProvider {

  private static final Logger LOGGER = LoggerFactory.getLogger(OboAuthenticationProvider.class);

  private final String tenantId;
  private final String clientId;
  private final String clientSecret;
  private final Set<String> scopes;
  private final OAuth2AuthorizedClientService clientService;

  public OboAuthenticationProvider(
      final Set<String> scopes,
      final String tenantId,
      final String clientId,
      final String clientSecret,
      final OAuth2AuthorizedClientService clientService) {
    this.scopes = scopes;
    this.tenantId = tenantId;
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.clientService = clientService;
  }

  @Nonnull
  @Override
  public CompletableFuture<String> getAuthorizationTokenAsync(@Nonnull final URL url) {
    if (!shouldAuthenticateRequestWithUrl(url)) {
      return CompletableFuture.completedFuture(null);
    }

    final OnBehalfOfParameters parameters = OnBehalfOfParameters
        .builder(scopes, new UserAssertion(getAccessToken()))
        .build();

    return createApp()
        .map(a -> a.acquireToken(parameters).thenApply(IAuthenticationResult::accessToken))
        .orElse(CompletableFuture.failedFuture(new Exception("Failed to generate obo token.")));
  }

  private String getAccessToken() {
    final OAuth2AuthenticationToken authentication =
        (OAuth2AuthenticationToken) SecurityContextHolder
            .getContext()
            .getAuthentication();

    final OAuth2AuthorizedClient client =
        clientService.loadAuthorizedClient(
            authentication.getAuthorizedClientRegistrationId(),
            authentication.getName());

    return client.getAccessToken().getTokenValue();
  }

  private Optional<ConfidentialClientApplication> createApp() {
    final String authority = "https://login.microsoftonline.com/" + tenantId;
    final IClientSecret clientCredential = ClientCredentialFactory.createFromSecret(clientSecret);
    try {
      return Optional
          .of(ConfidentialClientApplication.builder(clientId, clientCredential)
              .authority(authority)
              .build());
    } catch (final MalformedURLException e) {
      LOGGER.error("Failed to create ConfidentialClientApplication", e);
    }
    return Optional.empty();
  }
}
