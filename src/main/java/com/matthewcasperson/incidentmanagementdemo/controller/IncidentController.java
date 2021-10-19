package com.matthewcasperson.incidentmanagementdemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IncidentController {

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
}
