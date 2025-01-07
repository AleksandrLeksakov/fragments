package ru.netology.nmedia.dto

data class Post(
    val id: Long,
    val content: String,
    val published: String,
    val author: String,
    val likedByMe: Boolean,
    val shareByMe:Boolean,
    val likes: Int,
    var shares: Int

    )
