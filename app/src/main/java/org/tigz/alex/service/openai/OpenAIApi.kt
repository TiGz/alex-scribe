package org.tigz.alex.service.openai

import kotlinx.serialization.Serializable

@Serializable
data class ChatCompletionRequest(
    val model: String,
    val messages: List<Message>,
    val max_tokens: Int
)

@Serializable
data class Message(
    val role: String,
    val content: List<Content>
)

@Serializable
data class Content(
    val type: String,
    val text: String? = null,
    val image_url: ImageUrl? = null
)

@Serializable
data class ImageUrl(
    val url: String
)

@Serializable
data class ChatCompletionResponse(
    val id: String,
    val `object`: String,
    val created: Long,
    val model: String,
    val usage: Usage,
    val choices: List<Choice>
)

@Serializable
data class Usage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)

@Serializable
data class ChoiceMessage(
    val role: String,
    val content: String
)

@Serializable
data class Choice(
    val message: ChoiceMessage,
    val finish_details: FinishDetails,
    val index: Int
)

@Serializable
data class FinishDetails(
    val type: String,
    val stop: String
)