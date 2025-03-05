package com.example.dicodingevent.ui.home

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
import com.example.dicodingevent.adapter.EventHorizontalAdapter
import com.example.dicodingevent.databinding.FragmentHomeBinding
import com.example.dicodingevent.repository.Result

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val adapter = EventAdapter()
    private val adapterHorizontal = EventHorizontalAdapter()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireContext())
        val viewModel: HomeViewModel by viewModels {
            factory
        }
        binding.rvHorizontal.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvHorizontal.adapter = adapterHorizontal
        binding.rvVertical.layoutManager = LinearLayoutManager(requireContext())
        binding.rvVertical.adapter = adapter

        viewModel.getEvent(1).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.noEvent.visibility = View.GONE
                    binding.progressBar1.visibility = View.VISIBLE
                    Log.d(
                        "UpcomingEvent (loading)",
                        "ProgressBar visibility: ${binding.progressBar1.visibility}"
                    )
                }

                is Result.Success -> {
                    binding.progressBar1.visibility = View.GONE
                    Log.d(
                        "UpcomingEvent (success)",
                        "ProgressBar visibility: ${binding.progressBar1.visibility}"
                    )
                    val eventData = result.data
                    if (eventData.isEmpty()) {
                        binding.noEvent.visibility = View.VISIBLE
                    } else {
                        binding.noEvent.visibility = View.GONE
                        adapterHorizontal.submitList(eventData.take(5))
                        Log.d("Home Fragment Upcoming Event: ", eventData.toString())
                    }
                }

                is Result.Error -> {
                    binding.noEvent.visibility = View.GONE
                    binding.progressBar1.visibility = View.GONE
                    Log.d(
                        "UpcomingEvent (error)",
                        "ProgressBar visibility: ${binding.progressBar1.visibility}"
                    )
                    Toast.makeText(
                        context,
                        "Terjadi kesalahan" + result.error,
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }

        viewModel.getEvent(0).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.noEventFinished.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
                    Log.d(
                        "FinishedEvent (loading)",
                        "ProgressBar visibility: ${binding.progressBar.visibility}"
                    )
                }

                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Log.d(
                        "FinishedEvent (success)",
                        "ProgressBar visibility: ${binding.progressBar.visibility}"
                    )
                    val eventData = result.data
                    if (eventData.isEmpty()) {
                        binding.noEventFinished.visibility = View.VISIBLE
                    } else {
                        binding.noEventFinished.visibility = View.GONE
                        adapter.submitList(eventData.take(5))
                        Log.d("Home Fragment Finished Event: ", eventData.toString())
                    }
                }

                is Result.Error -> {
                    binding.noEventFinished.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                    Log.d(
                        "FinishedEvent (error)",
                        "ProgressBar visibility: ${binding.progressBar.visibility}"
                    )
                    Toast.makeText(
                        context,
                        "Terjadi kesalahan" + result.error,
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}