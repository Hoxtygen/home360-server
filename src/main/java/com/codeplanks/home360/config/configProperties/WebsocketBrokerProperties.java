package com.codeplanks.home360.config.configProperties;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("websocket.broker")
public record WebsocketBrokerProperties(
        @NonNull String relayHost,
        int brokerPort,
        @NonNull String clientLogin,
        @NonNull String clientPasscode
) {
}
