package com.example.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.data.mongodb.repository.Tailable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import java.time.Instant

@SpringBootApplication
class RSocketServerApplication

fun main(args: Array<String>) {
    runApplication<RSocketServerApplication>(*args)
}

@Controller
class MessageController(private val messages: MessageRepository) {
    @MessageMapping("send")
    fun hello(p: ChatMessage) = this.messages.save(p).then()

    @MessageMapping("messages")
    fun messageStream(): Flux<ChatMessage> = this.messages.getMessagesBy()
}

interface MessageRepository : ReactiveMongoRepository<ChatMessage, String> {
    @Tailable
    fun getMessagesBy(): Flux<ChatMessage>
}

@Document
data class ChatMessage(@Id var id: String? = null, var body: String, var sentAt: Instant = Instant.now())