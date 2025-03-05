package com.example.dicodingevent.ui.favorite

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dicodingevent.ViewModelFactory
import com.example.dicodingevent.adapter.EventAdapter
import com.example.dicodingevent.data.local.entity.EventEntity
import com.example.dicodingevent.databinding.FragmentFavoriteBinding
import com.example.dicodingevent.repository.Result
import okhttp3.internal.toImmutableList

class FavoriteFragment : Fragment() {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private val adapter = EventAdapter()

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireContext())
        val viewModel: FavoriteViewModel by viewModels {
            factory
        }
        binding.rvVertical.layoutManager = LinearLayoutManager(requireContext())
        binding.rvVertical.adapter = adapter
        viewModel.getFavoriteEvent().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.noEvent.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
                    Log.d(
                        "FavoriteFragment (loading)",
                        "ProgressBar visibility: ${binding.progressBar.visibility}"
                    )
                }

                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Log.d(
                        "FavoriteFragment (success)",
                        "ProgressBar visibility: ${binding.progressBar.visibility}"
                    )
                    val favoriteList = result.data
                    if (favoriteList.isEmpty()) {
                        binding.noEvent.visibility = View.VISIBLE
                        binding.rvVertical.visibility = View.GONE
                    } else {
                        binding.noEvent.visibility = View.GONE
                        val eventData =
                            favoriteList.map { favorite ->
                                EventEntity(
                                    id = favorite.id.toInt(),
                                    category = favorite.category,
                                    name = favorite.name,
                                    mediaCover = favorite.mediaCover,
                                    isFavorite = true,
                                )
                            }
                        adapter.submitList(eventData.toImmutableList())
                        Log.d("Favorite Fragment: ", eventData.toString())
                    }
                }

                is Result.Error -> {
                    binding.noEvent.visibility = View.GONE
                    binding.progressBar.visibility = View.GONE
                    binding.rvVertical.visibility = View.GONE
                    Log.d(
                        "FavoriteFragment (error)",
                        "ProgressBar visibility: ${binding.progressBar.visibility}"
                    )
                    Toast
                        .makeText(
                            context,
                            "Terjadi kesalahan" + result.error,
                            Toast.LENGTH_SHORT,
                        ).show()
                }
            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
