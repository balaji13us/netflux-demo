package com.example.demo.filters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class LogFilter implements WebFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        long startTime = System.currentTimeMillis();
        String path = exchange.getRequest().getURI().getPath();
        LOGGER.info("Serving '{}'", path);
        //exchange.getRequest().getBody().flatMap(null)
        return chain.filter(exchange).doAfterTerminate(() -> {
                    exchange.getResponse().getHeaders().entrySet().forEach(e ->
                            LOGGER.info("Response header '{}': {}", e.getKey(), e.getValue()));

                    LOGGER.info("Served '{}' as {} in {} msec",
                            path,
                            exchange.getResponse().getStatusCode(),
                            System.currentTimeMillis() - startTime);
                }
        );
    }
}

//https://github.com/fbeaufume/webflux-filter-sample/tree/master/src/main/java/com/adeliosys/sample