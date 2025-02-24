package ru.netology.nmedia.activity

// EditPostActivity.kt
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.databinding.ActivityEditPostBinding

class EditPostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditPostBinding // ViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val postId = intent.getLongExtra("postId", 0)
        val postContent = intent.getStringExtra("postContent")

        binding.editContentEditText.setText(postContent) // Установите текст в EditText

        // Обработчик нажатия на кнопку "Сохранить"
        binding.editSaveButton.setOnClickListener {
            val newContent = binding.editContentEditText.text.toString()
            val intent = Intent().apply {
                putExtra("postId", postId)
                putExtra("newContent", newContent)
            }
            setResult(RESULT_OK, intent) // Устанавливаем результат
            finish() // Закрываем Activity
        }

        // Обработчик нажатия на кнопку "Отмена" (необязательно)
        binding.editCancelButton.setOnClickListener {
            setResult(RESULT_CANCELED) //  Устанавливаем результат
            finish() // Закрываем Activity
        }
    }
}