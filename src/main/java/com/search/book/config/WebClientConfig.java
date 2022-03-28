package com.search.book.config;

import com.search.book.exception.ThrowingConsumer;
import com.search.book.model.response.BookInfo;
import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.LoggingCodecSupport;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Slf4j
@Configuration
public class WebClientConfig {
    @Value("${kakao.rest-api-key}")
    private String restApiKey;

    @Bean
    public WebClient webClient() {
        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024 * 50))
                .build();
        exchangeStrategies
                .messageWriters().stream()
                .filter(LoggingCodecSupport.class::isInstance)
                .forEach(writer -> ((LoggingCodecSupport) writer).setEnableLoggingRequestDetails(true));

        WebClient webClient = WebClient.builder()
                .clientConnector(
                        new ReactorClientHttpConnector(
                                HttpClient
                                        .create()
                                        .secure(
                                                ThrowingConsumer.unchecked(
                                                        sslContextSpec -> sslContextSpec.sslContext(
                                                                SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build()
                                                        )
                                                )
                                        )
                                        .tcpConfiguration(
                                                client -> client.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 120_000)
                                                        .doOnConnected(conn -> conn.addHandlerLast(new ReadTimeoutHandler(180))
                                                                .addHandlerLast(new WriteTimeoutHandler(180))
                                                        )
                                        )
                        )
                )
                .exchangeStrategies(exchangeStrategies)
                .filter(ExchangeFilterFunction.ofRequestProcessor(
                        clientRequest -> {
                            log.debug("Request: {} {}", clientRequest.method(), clientRequest.url());
                            clientRequest.headers().forEach((name, values) -> values.forEach(value -> log.debug("{} : {}", name, value)));
                            return Mono.just(clientRequest);
                        }
                ))
                .filter(ExchangeFilterFunction.ofResponseProcessor(
                        clientResponse -> {
                            clientResponse.headers().asHttpHeaders().forEach((name, values) -> values.forEach(value -> log.debug("{} : {}", name, value)));
                            return Mono.just(clientResponse);
                        }
                ))
                .baseUrl("https://dapi.kakao.com/v3/search/book")
                .defaultHeader("Authorization", "KakaoAK " + restApiKey)
                .build();

        webClient.get()
                .uri(baseUrl -> baseUrl.queryParam("query", "미움받을 용기").queryParam("page", "1").build())
                .retrieve()
                .bodyToMono(BookInfo.class)
                .block();

        return webClient;
    }

}
