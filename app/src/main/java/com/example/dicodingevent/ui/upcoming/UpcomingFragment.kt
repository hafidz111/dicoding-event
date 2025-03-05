package com.example.dicodingevent.ui.upcoming

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
import com.example.dicodingevent.databinding.FragmentUpcomingBinding
import com.example.dicodingevent.repository.Result

class UpcomingFragment : Fragment() {
    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding!!
    private val adapter = EventAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentUpcomingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireContext())
        val viewModel: UpcomingViewModel by viewModels {
            factory
        }
        binding.rvVertical.layoutManager = LinearLayoutManager(requireContext())
        binding.rvVertical.adapter = adapter
        with(binding) {
            searchView.setupWithSearchBar(searchBar)
            searchView
                .editText
                .setOnEditorActionListener { _, _, _ ->
                    searchBar.setText(searchView.text)
                    viewModel.setQuery(searchView.text.toString())
                    searchView.hide()
                    false
                }
        }
        viewModel.query.observe(viewLifecycleOwner) {
            viewModel.getEvent(it).observe(viewLifecycleOwner) { result ->
                when (result) {
                    is Result.Loading -> {
                        binding.noEvent.visibility = View.GONE
                        binding.progressBar.visibility = View.VISIBLE
                        Log.d(
                            "UpcomingFragment (loading)",
                            "ProgressBar visibility: ${binding.progressBar.visibility}"
                        )
                    }

                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        val eventData = result.data
                        Log.d(
                            "UpcomingFragment (success)",
                            "ProgressBar visibility: ${binding.progressBar.visibility}"
                        )
                        Log.d("Upcoming Fragment (success): ", eventData.toString())
                        if (eventData.isEmpty()) {
                            Log.d("Upcoming Fragment (empty): ", eventData.toString())
                            binding.noEvent.visibility = View.VISIBLE
                        } else {
                            adapter.submitList(eventData)
                            Log.d("Upcoming Fragment: ", eventData.toString())
                        }
                    }

                    is Result.Error -> {
                        binding.noEvent.visibility = View.GONE
                        binding.progressBar.visibility = View.GONE
                        Log.d(
                            "UpcomingFragment (error)",
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
