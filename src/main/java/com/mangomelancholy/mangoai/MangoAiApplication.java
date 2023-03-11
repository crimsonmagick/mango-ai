package com.mangomelancholy.mangoai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

@EnableWebFlux
@SpringBootApplication
public class MangoAiApplication {

  public static void main(String[] args) {
    SpringApplication.run(MangoAiApplication.class, args);
  }

}

