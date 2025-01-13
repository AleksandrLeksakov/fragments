package ru.netology.nmedia.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryInMemory
import kotlin.random.Random

private val emty = Post(
    id = 0,
    content = "",
    published = "",
    author = "",
    likedByMe = false,
    shareByMe = false,
    likes = 0,
    shares = 0
)

class PostViewModel() : ViewModel() {

    private val repository: PostRepository = PostRepositoryInMemory()
    private val _data = MutableLiveData(repository.getAll().value)
    val LiveData<List<Post>> = _data

    private var currentPost: Post? = null;

    init {
        _data.value = repository.getAll().value
    }

    fun getPostById(id: Long): Post? {
        return  repository.getAll().value?.find { it.id == id}
    }
    fun likeById(id: Long) {
        repository.likeById(id)
        _data.value = repository.getAll().value
    }
    fun shareById(id: Long) {
        repository.shareById(id)
        _data.value = repository.getAll().value
    }
    fun removeById(id: Long){
        repository.removeById(id)
        _data.value = repository.getAll().value
    }
    fun save(content: String) {
        val post =  Post(
            id = Random.nextLong(),
            author = "Me",
            published = "Now",
            content = content,
            likedByMe = false,
            shareByMe = false,
            likes = 0,
            shares = 0
        )
        repository.save(post)
        _data.value = repository.getAll().value
    }
    fun update(post: Post){
        repository.update(post)
        _data.value = repository.getAll().value
    }
    fun cancelEdit() {
        _data.value = repository.getAll().value
    }
}