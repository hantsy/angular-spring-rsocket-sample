package com.example.demo

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.util.MimeTypeUtils
import reactor.test.StepVerifier
import java.net.URI

@SpringBootTest
class RSocketServerApplicationTests {

    @Autowired
    lateinit var rSocketRequester: RSocketRequester;

    @Test
    fun contextLoads() {

        rSocketRequester.route("messages")
                .retrieveFlux(ChatMessage::class.java)
                .log()
                .`as` { StepVerifier.create(it) }
                .consumeNextWith { it ->  assertThat(it.body).isEqualTo("test message")}
                .consumeNextWith { it ->  assertThat(it.body).isEqualTo("test message2")}
                .thenCancel()
        rSocketRequester.route("send").data(ChatMessage(body = "test message")).send().then()
        rSocketRequester.route("send").data(ChatMessage(body = "test message2")).send().then()
    }

    @TestConfiguration
    class TestConfig {

        @Bean
        fun rSocketRequester(builder: RSocketRequester.Builder) = builder.dataMimeType(MimeTypeUtils.APPLICATION_JSON)
                .connectWebSocket(URI.create("ws://localhost:8080/rsocket")).block()
    }

}

