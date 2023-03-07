package com.mangomelancholy.mangoai.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@EnableWebFlux
public class WebFluxConfig {

  @Bean
  public RouterFunction<ServerResponse> routerFunction() {
    return RouterFunctions.route(RequestPredicates.GET("/hello"), request -> {
      String name = request.queryParam("name").orElse("world");
      return ServerResponse.ok().bodyValue("Hello, " + name + "!");
    });
  }
}
