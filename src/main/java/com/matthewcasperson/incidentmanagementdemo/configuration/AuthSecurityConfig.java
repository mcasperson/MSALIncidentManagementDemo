package com.matthewcasperson.incidentmanagementdemo.configuration;

import com.azure.spring.aad.webapi.AADResourceServerWebSecurityConfigurerAdapter;
import com.azure.spring.aad.webapp.AADWebSecurityConfigurerAdapter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@EnableWebSecurity
public class AuthSecurityConfig {

  @Order(1)
  @Configuration
  public static class ApiWebSecurityConfigurationAdapter extends
      AADResourceServerWebSecurityConfigurerAdapter {
    protected void configure(HttpSecurity http) throws Exception {
      super.configure(http);
      // All the paths that match `/api/**`(configurable) work as the resource server.
      // Other paths work as  the web application.
      // @formatter:off
      http
        .antMatcher("/api/**")
        .authorizeRequests().anyRequest().authenticated();
      // @formatter:on
    }
  }

  @Configuration
  public static class HtmlWebSecurityConfigurerAdapter extends AADWebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
      super.configure(http);
      // @formatter:off
      http
        .authorizeRequests()
          .antMatchers("/login", "/*.js", "/*.css").permitAll()
          .anyRequest().authenticated()
        .and()
          .csrf()
          .disable();
      // @formatter:on
    }
  }
}