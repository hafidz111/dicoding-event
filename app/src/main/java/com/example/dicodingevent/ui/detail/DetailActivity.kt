package com.example.dicodingevent.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.example.dicodingevent.R
import com.example.dicodingevent.ViewModelFactory
import com.example.dicodingevent.data.local.entity.EventEntity
import com.example.dicodingevent.data.local.entity.FavoriteEntity
import com.example.dicodingevent.databinding.ActivityDetailBinding
import com.example.dicodingevent.repository.Result

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private var isFavorite: Boolean = false
    private val factory: ViewModelFactory = ViewModelFactory.getInstance(this)
    private val viewModel: DetailViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val eventId = intent.getIntExtra(EXTRA_EVENT_ID, 0)

        viewModel.getDetailEvent(eventId).observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    Log.d(
                        "DetailActivity (loading)",
                        "ProgressBar visibility: ${binding.progressBar.visibility}"
                    )
                }

                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Log.d(
                        "DetailActivity (success)",
                        "ProgressBar visibility: ${binding.progressBar.visibility}"
                    )
                    val eventData = result.data
                    setInitialEventData(eventId, eventData)
                }

                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Log.d(
                        "DetailActivity (error)",
                        "ProgressBar visibility: ${binding.progressBar.visibility}"
                    )
                    Toast.makeText(
                        this,
                        "Terjadi kesalahan: ${result.error}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setInitialEventData(id: Int, event: EventEntity) {
        with(binding) {
            Glide.with(this@DetailActivity)
                .load(event.mediaCover)
                .into(imgItemEvent)
            tvCategory.text = event.category
            tvEventName.text = event.name
            tvOwnerName.text = event.ownerName
            tvBeginTime.text = event.beginTime.toString()
            tvRegistrants.text = (event.quota?.minus(event.registrants ?: 0)).toString()
            tvQuota.text = event.quota.toString()
            tvDescription.text =
                HtmlCompat.fromHtml(event.description.toString(), HtmlCompat.FROM_HTML_MODE_LEGACY)

            val fabFavorite = fabFavorite
            isFavorite = event.isFavorite
            if (isFavorite) {
                fabFavorite.setImageDrawable(
                    ContextCompat.getDrawable(
                        fabFavorite.context,
                        R.drawable.ic_favorite
                    )
                )
            } else {
                fabFavorite.setImageDrawable(
                    ContextCompat.getDrawable(
                        fabFavorite.context,
                        R.drawable.ic_favorite_border
                    )
                )
            }

            fabFavorite.setOnClickListener {
                onFavoriteClick(id, event)
            }

            btnRegister.setOnClickListener {
                val uriUrl: Uri = Uri.parse(event.link)
                val launchBrowser = Intent(Intent.ACTION_VIEW, uriUrl)
                startActivity(launchBrowser)
            }
        }
    }

    private fun onFavoriteClick(id: Int, event: EventEntity) {
        toggleFavorite()
        event.isFavorite = isFavorite
        val favoriteEntity = FavoriteEntity(
            id = event.id.toString(),
            name = event.name.toString(),
            mediaCover = event.mediaCover,
            category = event.category,
            isfavorite = event.isFavorite
        )
        viewModel.setEvent(event, isFavorite)
        viewModel.setFavoriteEvent(id, favoriteEntity, isFavorite)

    }

    private fun toggleFavorite() {
        isFavorite = !isFavorite
        val fabFavorite = binding.fabFavorite
        if (isFavorite) {
            fabFavorite.setImageDrawable(
                ContextCompat.getDrawable(
                    fabFavorite.context,
                    R.drawable.ic_favorite
                )
            )
        } else {
            fabFavorite.setImageDrawable(
                ContextCompat.getDrawable(
                    fabFavorite.context,
                    R.drawable.ic_favorite_border
                )
            )
        }
    }

    companion object {
        const val EXTRA_EVENT_ID = "EVENT_ID"
    }
}