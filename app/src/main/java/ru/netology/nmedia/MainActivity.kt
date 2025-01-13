package ru.netology.nmedia


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.adapter.PostAction
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var currentEditPost : Post? = null //Variable to hold post that we are editing
    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val viewModel: PostViewModel by viewModels()
        val adapter = PostsAdapter { action ->
            when(action){
                is PostAction.Like -> viewModel.likeById(action.postId)
                is PostAction.Share ->  viewModel.shareById(action.postId)
                is PostAction.Remove ->  viewModel.removeById(action.postId)
                is PostAction.Edit ->  {
                    currentEditPost = viewModel.getPostById(action.postId)
                    binding.content.setText(currentEditPost?.content)
                    binding.inputGroup.visibility = View.VISIBLE // Show the input group
                    binding.content.requestFocus()
                }
                is PostAction.CancelEdit ->  {
                    currentEditPost = null // Reset current edit
                    binding.inputGroup.visibility = View.GONE // Hide input group
                    binding.content.text.clear() // clear input text
                    viewModel.cancelEdit()
                }
            }
        }
        binding.container.adapter = adapter
        viewModel.data.observe(this) { posts ->
            adapter.submitList(posts)
            binding.inputGroup.visibility = View.GONE
        }
        binding.save.setOnClickListener {
            val text =  binding.content.text.toString()
            if(text.isNotBlank()){
                if(currentEditPost == null){
                    viewModel.save(text)
                    Toast.makeText(this, "Post saved", Toast.LENGTH_SHORT).show()

                } else {
                    viewModel.update(currentEditPost!!.copy(content = text))
                    Toast.makeText(this, "Post edited", Toast.LENGTH_SHORT).show()
                    currentEditPost = null
                }
                binding.content.text.clear()
            } else {
                Toast.makeText(this, "Cannot save empty post", Toast.LENGTH_SHORT).show()
            }

            binding.inputGroup.visibility = View.GONE
        }
        binding.cancellation.setOnClickListener {
            if(currentEditPost != null)
                viewModel.cancelEdit()
            binding.inputGroup.visibility = View.GONE // Hide input group
            currentEditPost = null
            binding.content.text.clear()


        }
    }
}









