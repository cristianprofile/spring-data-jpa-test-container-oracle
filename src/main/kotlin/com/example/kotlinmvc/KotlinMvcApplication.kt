package com.example.kotlinmvc

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service


// data classes

@Entity
data class MessageModel(
    @Id @GeneratedValue var id: Long? = null,
    @Column(nullable = false)
    val text: String = ""
)

data class MessageOut(
    val id: Long,
    val text: String
)

data class MessageCreate(
    val text: String?
)

// layer's mappers

fun MessageCreate.convertToMessageModel() = MessageModel(
    text = this.text?.uppercase() ?: "emptyText"
)

fun MessageModel.covertToMessage() = MessageOut(
    id = this.id ?: -1,
    text = this.text
)

// service

interface MessageService {
    fun save(messageOut: MessageCreate): MessageOut
    fun findAll(excludeText: String?): List<MessageOut>
}


@Service
class MessageServiceImpl(val messageRepository: MessageRepository) : MessageService {

    override fun save(messageCreate: MessageCreate): MessageOut {
        val save = messageRepository.save(messageCreate.convertToMessageModel())
        return save.covertToMessage()
    }

    override fun findAll(excludeText: String?): List<MessageOut> {

        return if (excludeText != null) {
            messageRepository.findByTextNot(excludeText).map { it.covertToMessage() }
        } else {
            messageRepository.findAll().map { it.covertToMessage() }
        }
    }

}

// repository
@Repository
interface MessageRepository : JpaRepository<MessageModel, Long> {
    fun findByTextNot(text: String): List<MessageModel>
}


@SpringBootApplication
class KotlinWebApplication

fun main(args: Array<String>) {
    runApplication<KotlinWebApplication>(*args)
}

