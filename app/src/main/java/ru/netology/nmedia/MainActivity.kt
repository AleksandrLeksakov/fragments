package ru.netology.nmedia

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.adapter.PostAction
import ru.netology.nmedia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding // ViewBinding для доступа к элементам макета
    private var currentEditPost: Post? = null // Переменная для хранения редактируемого поста
    private var isNewPostAdded: Boolean = false // Флаг для отслеживания добавления нового поста

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater) // Инициализация ViewBinding
        setContentView(binding.root) // Установка корневого View
        val viewModel: PostViewModel by viewModels() // Получение ViewModel
        val adapter = PostsAdapter { action ->
            // Обработка действий, полученных из адаптера
            when (action) {
                is PostAction.Like -> viewModel.likeById(action.postId) // Лайк
                is PostAction.Share -> viewModel.shareById(action.postId) // Поделиться
                is PostAction.Remove -> viewModel.removeById(action.postId) // Удалить
                is PostAction.Edit -> {
                    // Редактирование: получаем пост, устанавливаем текст в поле ввода, показываем поле ввода
                    currentEditPost = viewModel.getPostById(action.postId)
                    binding.content.setText(currentEditPost?.content)
                    binding.inputGroup.visibility = View.VISIBLE
                    binding.content.requestFocus() // Фокус на поле ввода
                }
                is PostAction.CancelEdit -> {
                    // Отмена редактирования: сбрасываем редактируемый пост, скрываем поле ввода
                    currentEditPost = null
                    binding.inputGroup.visibility = View.GONE
                    binding.content.text.clear() // Очистка поля ввода
                    viewModel.cancelEdit() // Вызов cancelEdit()
                }
            }
        }
        binding.container.adapter = adapter // Установка адаптера для RecyclerView
        viewModel.data.observe(this) { posts ->
            // Наблюдение за изменениями списка постов
            adapter.submitList(posts) // Обновление списка в адаптере
            binding.inputGroup.visibility = View.GONE // Скрываем поле ввода

            // Прокрутка к началу только если добавлен новый пост
            if (isNewPostAdded && posts.isNotEmpty()) {
                binding.container.smoothScrollToPosition(0) // Прокрутка
                isNewPostAdded = false // Сбрасываем флаг после прокрутки
            }
        }

        binding.save.setOnClickListener {
            // Обработка нажатия на кнопку "Сохранить"
            val text = binding.content.text.toString() // Получаем текст из поля ввода
            if (text.isNotBlank()) {
                if (currentEditPost == null) {
                    isNewPostAdded = true // Устанавливаем флаг, что был добавлен новый пост
                    viewModel.save(text) // Сохранение нового поста
                    Toast.makeText(this, "Post saved", Toast.LENGTH_SHORT).show() // Уведомление
                } else {
                    viewModel.update(currentEditPost!!.copy(content = text)) // Обновление существующего поста
                    Toast.makeText(this, "Post edited", Toast.LENGTH_SHORT).show() // Уведомление
                    currentEditPost = null // Сбрасываем редактируемый пост
                }
                binding.content.text.clear() // Очищаем поле ввода
                binding.inputGroup.visibility = View.GONE // Скрываем поле ввода
                hideKeyboard() // Скрытие клавиатуры
            } else {
                Toast.makeText(this, "Cannot save empty post", Toast.LENGTH_SHORT).show() // Уведомление
            }
        }

        binding.cancellation.setOnClickListener {
            // Обработка нажатия на кнопку "Отмена"
            if (currentEditPost != null)
                viewModel.cancelEdit() // Вызов cancelEdit()
            binding.inputGroup.visibility = View.GONE // Скрываем поле ввода
            currentEditPost = null // Сбрасываем редактируемый пост
            binding.content.text.clear() // Очищаем поле ввода
            hideKeyboard() // Скрытие клавиатуры
        }
        binding.fab.setOnClickListener {
            // Обработка нажатия на FAB (кнопка добавления)
            currentEditPost = null // Сбрасываем редактируемый пост
            binding.inputGroup.visibility = View.VISIBLE // Показываем поле ввода
            binding.content.requestFocus() // Установка фокуса на поле ввода
        }
    }

    private fun hideKeyboard() {
        // Метод для скрытия клавиатуры
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager // Получаем InputMethodManager
        imm.hideSoftInputFromWindow(binding.content.windowToken, 0) // Скрытие клавиатуры
    }
}