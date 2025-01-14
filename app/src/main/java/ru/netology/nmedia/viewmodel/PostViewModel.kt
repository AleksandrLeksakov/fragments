package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryInMemory
import kotlin.random.Random

class PostViewModel : ViewModel() {

    // Создаем репозиторий для работы с данными
    private val repository: PostRepository = PostRepositoryInMemory()
    // Создаем MutableLiveData, которая содержит список всех постов. Инициализируем пустой список.
    private val _data = MutableLiveData<List<Post>>(emptyList())
    // Создаем LiveData для предоставления доступа к данным, но запрещаем изменять эти данные напрямую
    val data: LiveData<List<Post>> = _data // Переименовали свойство

    // Инициализация ViewModel
    init {
        // Когда ViewModel создается, мы обновляем _data со всеми значениями из репозитория.
        loadPosts()
    }


    private fun loadPosts(){
        try{
            _data.value = repository.getAll().value ?: emptyList()
        } catch(e: Exception){
            println("Error loading posts ${e.message}")
            _data.value = emptyList()
        }
    }
    fun getPostById(id: Long): Post? {
        // Получаем все посты из репозитория и ищем тот, чей id равен заданному.
        return repository.getAll().value?.find { it.id == id }
    }
    fun likeById(id: Long) {
        // Вызываем метод репозитория, чтобы обновить данные поста.
        repository.likeById(id)
        // Обновляем LiveData, чтобы UI получил уведомление об изменениях.
        loadPosts()
    }


    fun shareById(id: Long) {
        // Вызываем метод репозитория, чтобы обновить данные поста.
        repository.shareById(id)
        // Обновляем LiveData, чтобы UI получил уведомление об изменениях.
        loadPosts()
    }

    fun removeById(id: Long) {
        // Вызываем метод репозитория, чтобы удалить пост.
        repository.removeById(id)
        // Обновляем LiveData, чтобы UI получил уведомление об изменениях.
        loadPosts()
    }

    fun save(content: String) {
        // Создаем новый пост с рандомным id
        val post = Post(
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
        loadPosts()
    }
    fun update(post: Post) {

        repository.update(post)
        loadPosts()
    }
    fun cancelEdit() {
        loadPosts()
    }
}