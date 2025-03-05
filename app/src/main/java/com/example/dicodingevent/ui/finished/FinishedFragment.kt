package com.example.dicodingevent.ui.finished

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.dicodingevent.ViewModelFactory
import com.example.dicodingevent.adapter.EventStaggerAdapter
import com.example.dicodingevent.databinding.FragmentFinishedBinding
import com.example.dicodingevent.repository.Result

class FinishedFragment : Fragment() {

    private var _binding: FragmentFinishedBinding? = null
    private val binding get() = _binding!!
    private val adapter = EventStaggerAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireContext())
        val viewModel: FinishedViewModel by viewModels {
            factory
        }
        binding.rvVertical.layoutManager =
            StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
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
                            "FinishedFragment (loading)",
                            "ProgressBar visibility: ${binding.progressBar.visibility}"
                        )
                    }

                    is Result.Success -> {
                        binding.progressBar.visibility = View.GONE
                        Log.d(
                            "FinishedFragment (success)",
                            "ProgressBar visibility: ${binding.progressBar.visibility}"
                        )
                        val eventData = result.data
                        if (eventData.isEmpty()) {
                            binding.noEvent.visibility = View.VISIBLE
                        } else {
                            binding.noEvent.visibility = View.GONE
                            adapter.submitList(eventData)
                            Log.d("Finished Fragment: ", eventData.toString())
                        }

                    }

                    is Result.Error -> {
                        binding.noEvent.visibility = View.GONE
                        binding.progressBar.visibility = View.GONE
                        Log.d(
                            "FinishedFragment (error)",
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}