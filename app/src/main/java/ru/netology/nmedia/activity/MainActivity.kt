package ru.netology.nmedia.activity

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: PostViewModel by viewModels()

        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onEdit(post: Post) {
                val dialogView = FragmentNewPostBinding.inflate(layoutInflater)
                val editText = dialogView.editText
                val cancelButton = dialogView.cancelButton
                val saveButton = dialogView.saveButton

                editText.setText(post.content)

                val builder = AlertDialog.Builder(this@MainActivity)
                    .setView(dialogView.root)
                    .setTitle("Редактировать пост")

                val dialog = builder.create()
                dialog.show()

                cancelButton.setOnClickListener {
                    dialog.dismiss()
                }

                saveButton.setOnClickListener {
                    val editedText = editText.text.toString()
                    if (editedText.isNotBlank()) {
                        viewModel.edit(post.copy(content = editedText))
                        viewModel.save()
                    }
                    dialog.dismiss()
                }
            }

            override fun onLike(post: Post) {
                viewModel.likeById(post.id)
            }

            override fun onShare(post: Post) {
                viewModel.shareById(post.id)

                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(intent, "Поделиться постом")
                startActivity(shareIntent)
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onPlayVideo(post: Post) {
                post.videoUrl?.let { videoUrl ->
                    try {
                        val intent = createVideoIntent(videoUrl)
                        startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(this@MainActivity, "Ошибка воспроизведения видео", Toast.LENGTH_SHORT).show()
                        e.printStackTrace()
                    }
                }
            }
        })

        binding.list.adapter = adapter
        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
        }

        viewModel.edited.observe(this) { post ->
            if (post.id == 0L) {
                binding.group.visibility = View.GONE
                return@observe
            }
            binding.group.visibility = View.VISIBLE
            binding.content.setText(post.content)
            binding.content.requestFocus()
        }

        binding.save.setOnClickListener {
            val content = binding.content.text.toString()
            val videoUrl = binding.videoUrl.text.toString()
            if (content.isNotBlank()) {
                viewModel.changeContent(content)
                viewModel.changeVideoUrl(videoUrl)
                viewModel.save()
            }
            binding.group.visibility = View.GONE
            binding.content.setText("")
            binding.videoUrl.setText("")
        }

        binding.cancel.setOnClickListener {
            viewModel.cancelEdit()
            binding.group.visibility = View.GONE
            binding.content.setText("")
            binding.videoUrl.setText("")
        }

        binding.fab.setOnClickListener {
            binding.group.visibility = View.VISIBLE
            binding.content.requestFocus()
        }
    }

    private fun createVideoIntent(videoUrl: String): Intent {
        return Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl))
    }
}