package com.example.demo

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.data.mongodb.repository.Tailable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import java.time.Instant

@SpringBootApplication
class RSocketServerApplication {

    @Bean
    fun runner(template: ReactiveMongoTemplate) = CommandLineRunner {
        println("running CommandLineRunner...")
        template.insert(Message(body="test")).then().block()
        template.executeCommand("{\"convertToCapped\": \"messages\", size: 100000}").log().subscribe(::println)
    }
}

fun main(args: Array<String>) {
    runApplication<RSocketServerApplication>(*args)
}

@Controller
class MessageController(private val messages: MessageRepository) {
    @MessageMapping("send")
    fun hello(p: String) = this.messages.save(Message(body = p, sentAt = Instant.now())).log().then()

    @MessageMapping("messages")
    fun messageStream(): Flux<Message> = this.messages.getMessagesBy().log()
}

interface MessageRepository : ReactiveMongoRepository<Message, String> {
    @Tailable
    fun getMessagesBy(): Flux<Message>
}

@Document(collection = "messages")
data class Message(@Id var id: String? = null, var body: String, var sentAt: Instant = Instant.now())
