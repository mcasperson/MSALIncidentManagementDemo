package com.matthewcasperson.incidentmanagementdemo.controller;

import com.microsoft.graph.requests.GraphServiceClient;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IncidentController {

  @Autowired
  GraphServiceClient<Request> client;

  @GetMapping("/")
  public String getIndex() {
    return "index";
  }

  @GetMapping("/update")
  public String getUpdate() {
    return "update";
  }

  @GetMapping("/create")
  public String getCreate() {
    return "create";
  }

  @PostMapping("/create")
  public String postCreate(@RequestParam final String sourceText) {
    createChannel(sourceText);
    return "update";
  }

  private void createChannel(final String sourceText) {

  }
}
