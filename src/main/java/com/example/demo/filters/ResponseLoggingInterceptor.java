package com.example.demo.filters;
import org.apache.commons.io.IOUtils;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;

public class ResponseLoggingInterceptor extends ServerHttpResponseDecorator {

	private static final Logger LOGGER = LoggerFactory.getLogger(ResponseLoggingInterceptor.class);

	private long startTime;
	private boolean logHeaders;

	public ResponseLoggingInterceptor(ServerHttpResponse delegate, long startTime, boolean logHeaders) {
		super(delegate);
		this.startTime = startTime;
		this.logHeaders = logHeaders;
	}

	@Override
	public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
		Flux<DataBuffer> buffer = Flux.from(body);
		return super.writeWith(buffer.doOnNext(dataBuffer -> {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				Channels.newChannel(baos).write(dataBuffer.asByteBuffer().asReadOnlyBuffer());
				String bodyRes = IOUtils.toString(baos.toByteArray(), "UTF-8");
				if (logHeaders)
					LOGGER.info("Response({} ms): status={}, payload={}", 
							System.currentTimeMillis() - startTime,
							getStatusCode().value(), 
							bodyRes);
				else
					LOGGER.info("Response({} ms): status={}, payload={}", 
							System.currentTimeMillis() - startTime,
							getStatusCode().value(), 
							bodyRes);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					baos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}));
	}
}