package com.example.demo.filters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import com.example.demo.filters.RequestLoggingInterceptor;
import com.example.demo.filters.ResponseLoggingInterceptor;
import com.example.demo.filters.UniqueIDGenerator;
import reactor.core.publisher.Mono;

import java.util.List;


public class ReactiveSpringLoggingFilter implements WebFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReactiveSpringLoggingFilter.class);
	private UniqueIDGenerator generator;
	private String ignorePatterns;
	private boolean logHeaders;
	private boolean useContentLength;

	public ReactiveSpringLoggingFilter(UniqueIDGenerator generator, String ignorePatterns, boolean logHeaders, boolean useContentLength) {
		this.generator = generator;
		this.ignorePatterns = ignorePatterns;
		this.logHeaders = logHeaders;
		this.useContentLength = useContentLength;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		if (ignorePatterns != null && exchange.getRequest().getURI().getPath().matches(ignorePatterns)) {
			return chain.filter(exchange);
		} else {
			generator.generateAndSetMDC(exchange.getRequest());
			final long startTime = System.currentTimeMillis();
			List<String> header = exchange.getRequest().getHeaders().get("Content-Length");
			if (useContentLength && (header == null || header.get(0).equals("0"))) {
				if (logHeaders)
					LOGGER.info("Request: method={}, uri={}, headers={}", exchange.getRequest().getMethod(),
							exchange.getRequest().getURI().getPath(), exchange.getRequest().getHeaders());
				else
					LOGGER.info("Request: method={}, uri={}", exchange.getRequest().getMethod(),
							exchange.getRequest().getURI().getPath());
			}
			ServerWebExchangeDecorator exchangeDecorator = new ServerWebExchangeDecorator(exchange) {
				@Override
				public ServerHttpRequest getRequest() {
					return new RequestLoggingInterceptor(super.getRequest(), logHeaders);
				}

				@Override
				public ServerHttpResponse getResponse() {
					return new ResponseLoggingInterceptor(super.getResponse(), startTime, logHeaders);
				}
			};
			return chain.filter(exchangeDecorator)
				.doOnSuccess(aVoid -> {
					logResponse(startTime, exchangeDecorator.getResponse(), exchangeDecorator.getResponse().getStatusCode().value());
				})
				.doOnError(throwable -> {
					logResponse(startTime, exchangeDecorator.getResponse(), 500);
				});
		}
	}

	private void logResponse(long startTime, ServerHttpResponse response, int overriddenStatus) {
		final long duration = System.currentTimeMillis() - startTime;
		List<String> header = response.getHeaders().get("Content-Length");
		if (useContentLength && (header == null || header.get(0).equals("0"))) {
			if (logHeaders)
				LOGGER.info("Response({} ms): status={}, headers={}", duration,
						overriddenStatus, response.getHeaders());
			else
				LOGGER.info("Response({} ms): status={}", duration,
						 overriddenStatus);
		}
	}

}