package ru.netology.nmedia.dto



data class Post(
    val id: Long,
    val author: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Int,
    val shareById: Boolean,
    var shares: Int,
    val videoUrl: String? = null // необязательное поле для ссылки на YouTube
)
