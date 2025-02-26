package ru.netology.nmedia.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post
import java.util.Locale

interface OnInteractionListener {
    fun onLike(post: Post) {}
    fun onEdit(post: Post) {}
    fun onRemove(post: Post) {}
    fun onShare(post: Post) {}
}

class PostsAdapter(
    private val onInteractionListener: OnInteractionListener,
) : ListAdapter<Post, PostViewHolder>(PostViewHolder.PostDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {
    private val imageViewVideo: ImageView = binding.root.findViewById(R.id.imageViewVideo)

    fun bind(post: Post) {
        binding.apply {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            // в адаптере
            share.isChecked = post.shareById
            share.text = "${post.shares.formatCount()}"

            like.isChecked = post.likedByMe
            like.text = "${post.likes.formatCount()}"
//          вместо
//          like.setImageResource(
//              if (post.likedByMe) R.drawable.ic_liked_24dp else R.drawable.ic_like_24dp
//          )

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.menu_options)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }

                            R.id.edit -> {
                                onInteractionListener.onEdit(post)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }

            like.setOnClickListener {
                onInteractionListener.onLike(post)
            }

            share.setOnClickListener {
                onInteractionListener.onShare(post)
            }
            imageViewVideo.visibility = if (post.videoUrl.isNullOrEmpty()) View.GONE else View.VISIBLE
            imageViewVideo.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.videoUrl))
                val packageManager = itemView.context.packageManager
                val resolvedActivity = intent.resolveActivity(packageManager)

                if (resolvedActivity != null) {
                    itemView.context.startActivity(intent)
                } else {
                    Toast.makeText(itemView.context, "Нет приложения для воспроизведения видео", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun Int.formatCount(): String {
        return when {
            this < 1000 -> this.toString()
            this < 10000 -> String.format(Locale.getDefault(), "%.1fK", this / 1000.0)
            this < 1000000 -> String.format(Locale.getDefault(), "%.0fK", this / 1000.0)
            else -> String.format(Locale.getDefault(), "%.1fM", this / 1000000.0)
        }
    }

    class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }
    }
}