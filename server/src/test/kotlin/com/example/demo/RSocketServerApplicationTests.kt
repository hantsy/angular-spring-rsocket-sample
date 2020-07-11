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
import java.time.Duration

@SpringBootTest
class RSocketServerApplicationTests {

    @Autowired
    lateinit var rSocketRequester: RSocketRequester;

    @Test
    fun contextLoads() {

        val verifier= rSocketRequester.route("messages")
                .retrieveFlux(Message::class.java)
                .log()
                .`as` { StepVerifier.create(it) }
                .consumeNextWith { it -> assertThat(it.body).isEqualTo("test message") }
                .consumeNextWith { it -> assertThat(it.body).isEqualTo("test message2") }
                .thenCancel()
                .verifyLater()
        rSocketRequester.route("send").data("test message").send().then().block()
        rSocketRequester.route("send").data("test message2").send().then().block()

        verifier.verify(Duration.ofSeconds(5))
    }

    @TestConfiguration
    class TestConfig {

        @Bean
        fun rSocketRequester(builder: RSocketRequester.Builder) = builder.dataMimeType(MimeTypeUtils.APPLICATION_JSON)
                .connectWebSocket(URI.create("ws://localhost:8080/rsocket")).block()
    }

}

