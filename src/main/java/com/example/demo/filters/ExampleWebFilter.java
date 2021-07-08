package com.example.demo.filters;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Component
@Slf4j
@Profile("e2")
public class ExampleWebFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {

        ObjectMapper mapper = new ObjectMapper();
        log.info("ExampleWebFilter");
        return serverWebExchange
                .getRequest()
                .getBody()
                .next()
                .flatMap(body -> {
                    try {
                        return Mono.just(mapper.readValue(body.asInputStream(), String.class));
                    } catch (IOException e) {
                        return Mono.error(e);
                    }
                })
                .flatMap((String s) -> {
                    log.info(s);
                    return webFilterChain.filter(serverWebExchange);
                });
    }
}

//https://gist.github.com/pgilad/e2d77ea2be972589f8ac475a88484dc5