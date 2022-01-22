package at.nuceria.weatherdemo.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import at.nuceria.weatherdemo.data.Result
import at.nuceria.weatherdemo.data.remote.response.WeatherResponse
import at.nuceria.weatherdemo.databinding.MainFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val viewModel by viewModels<MainViewModel>()

    private var _binding: MainFragmentBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainFragmentBinding.inflate(inflater, container, false)
        val view = binding.root


        observeViewModel()
        return view
    }

    private fun observeViewModel(){
        viewModel.weatherResult.observe(viewLifecycleOwner) {
            when(it) {
                is Result.Error -> showError(it.exception)
                is Result.Loading -> showLoading()
                is Result.Success -> showData(it.data)
            }
        }
    }
    

    private fun showLoading() {
        binding.loadingInfo.visibility = View.VISIBLE

    }

    private fun showData(weatherResult: WeatherResponse) {
        binding.loadingInfo.visibility = View.GONE
        binding.message.text = weatherResult.currentWeather.feelsLikeTemp.toString()
    }

    private fun showError(exception: Exception) {
        binding.loadingInfo.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
