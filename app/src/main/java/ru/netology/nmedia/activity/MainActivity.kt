package ru.netology.nmedia.activity


import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        val viewModel: PostViewModel by viewModels()
        val newPostLauncher = registerForActivityResult(NewPostResultContract()) { result ->
            result ?: return@registerForActivityResult
            viewModel.changeContent(result)
            viewModel.save()
        }
        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onEdit(post: Post) {
                val dialogView = layoutInflater.inflate(R.layout.fragment_new_post, null) // Используем новый layout
                val editText = dialogView.findViewById<EditText>(R.id.editText)
                val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)
                val saveButton = dialogView.findViewById<Button>(R.id.saveButton)

                editText.setText(post.content)

                val builder = AlertDialog.Builder(this@MainActivity)
                    .setView(dialogView)
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
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, post.content)

                }

                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }
        })
        binding.list.adapter = adapter
        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
        }



        binding.save.setOnClickListener {
            newPostLauncher.launch(Unit)
        }
    }
}