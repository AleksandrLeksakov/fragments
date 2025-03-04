package ru.netology.nmedia.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nmedia.dto.Post
import java.io.File
import java.io.FileOutputStream

class PostRepositoryJsonImpl(private val context: Context) : PostRepository {

    private val gson = Gson()
    private val type = object : TypeToken<List<Post>>() {}.type
    private val fileName = "posts.json"

    private val dataFile: File
        get() = File(context.filesDir, fileName)

    private val _data = MutableLiveData<List<Post>>()

    init {
        if (!dataFile.exists()) {
            // Инициализация начальных данных
            val initialPosts = listOf(
                Post(
                    id = 1,
                    author = "Нетология. Университет интернет-профессий будущего",
                    content = "Привет, это первый пост!",
                    published = "21 мая в 18:36",
                    likedByMe = false,
                    likes = 999,
                    shareById = false,
                    shares = 999
                ),
                Post(
                    id = 2,
                    author = "Нетология. Университет интернет-профессий будущего",
                    content = "Привет, это второй пост!",
                    published = "22 мая в 18:36",
                    likedByMe = false,
                    likes = 999,
                    shareById = false,
                    shares = 999,
                    videoUrl = "https://www.youtube.com/watch?v=example1"
                )
            )
            writeData(initialPosts)
        } else {
            // Чтение данных из файла
            _data.value = readData()
        }
    }

    override fun getAll(): LiveData<List<Post>> = _data

    private fun readData(): List<Post> {
        return if (dataFile.exists()) {
            val json = dataFile.readText()
            gson.fromJson(json, type) ?: emptyList()
        } else {
            emptyList()
        }
    }

    private fun writeData(posts: List<Post>) {
        val json = gson.toJson(posts)
        FileOutputStream(dataFile).use {
            it.write(json.toByteArray())
        }
        _data.value = posts
    }

    override fun save(post: Post) {
        val posts = _data.value?.toMutableList() ?: mutableListOf()
        if (post.id == 0L) {
            // Новый пост
            val newPost = post.copy(
                id = (posts.maxOfOrNull { it.id } ?: 0) + 1,
                author = "Me",
                published = "now"
            )
            posts.add(newPost)
        } else {
            // Редактирование существующего поста
            val index = posts.indexOfFirst { it.id == post.id }
            if (index != -1) {
                posts[index] = post
            }
        }
        writeData(posts)
    }

    override fun likeById(id: Long) {
        val posts = _data.value?.toMutableList() ?: return
        val index = posts.indexOfFirst { it.id == id }
        if (index != -1) {
            val post = posts[index]
            val updatedPost = post.copy(
                likedByMe = !post.likedByMe,
                likes = if (post.likedByMe) post.likes - 1 else post.likes + 1
            )
            posts[index] = updatedPost
            writeData(posts)
        }
    }

    override fun removeById(id: Long) {
        val posts = _data.value?.toMutableList() ?: return
        val index = posts.indexOfFirst { it.id == id }
        if (index != -1) {
            posts.removeAt(index)
            writeData(posts)
        }
    }

    override fun shareById(id: Long) {
        val posts = _data.value?.toMutableList() ?: return
        val index = posts.indexOfFirst { it.id == id }
        if (index != -1) {
            val post = posts[index]
            val updatedPost = post.copy(shares = post.shares + 1)
            posts[index] = updatedPost
            writeData(posts)
        }
    }
}