package ru.netology.nmedia


import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.adapter.PostAction
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.viewmodel.PostViewModel


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: PostViewModel by viewModels()
        val adapter = PostsAdapter { action ->
            when (action) {
                is PostAction.Like -> viewModel.likeById(action.postId)
                is PostAction.Share -> viewModel.shareById(action.postId)
                is PostAction.Remove -> viewModel.removeById(action.postId)
            }
        }

        binding.container.adapter = adapter
        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
        }
        binding.save.setOnClickListener {
            val text = binding.content.text.toString()

            if (text.isBlank()) {
                Snackbar.make(binding.root, R.string.em, Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }
            viewModel.saveContent(text)
            binding.content.setText("")   // убераем текст из сткори ввода после отпровления
            binding.content.clearFocus()  // убераем мигающий курсор из стороки Save

            binding.container.smoothScrollToPosition(0)  // АВТО ПОДЪЕМ ДО НОВОГО ПОСТА
        }
    }
}














