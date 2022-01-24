package at.nuceria.weatherdemo.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import at.nuceria.weatherdemo.R
import at.nuceria.weatherdemo.data.model.WeatherData
import at.nuceria.weatherdemo.databinding.MainFragmentBinding
import at.nuceria.weatherdemo.util.MarginItemDecoration
import at.nuceria.weatherdemo.util.Resource
import at.nuceria.weatherdemo.util.getDayIcon
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.math.roundToInt

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

    val adapter = ForeCastTileAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        //            binding.keyboardLanguageList.layoutManager =
//                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)




        observeViewModel()
        return view
    }

    private fun observeViewModel() {


        binding.forecastTiles.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            binding.forecastTiles.adapter = adapter


            binding.forecastTiles.addItemDecoration(
                MarginItemDecoration(resources.getDimensionPixelSize(R.dimen.tile_spacing))
            )




        viewModel.weatherResult.observe(viewLifecycleOwner) { resource ->
            if (resource.data != null) {
                showData(resource.data)
            } else {
                // TODO show an empty/placeholder view
            }

            if (resource is Resource.Loading) {
                if (resource.data != null) {
                    binding.loadingInfo.visibility = View.VISIBLE
                } else {
                    binding.progressbar.visibility = View.VISIBLE
                }
            } else {
                binding.loadingInfo.visibility = View.GONE
                binding.progressbar.visibility = View.GONE
            }

            if (resource is Resource.Error) {
                showError(resource.error)
            }
        }
    }

    private fun showData(weatherData: WeatherData) {
        weatherData.currentWeatherData.run {
            binding.currentWeatherGroup.visibility = View.VISIBLE
            // we don't want to show decimals for the values
            binding.temperature.text = temperature.roundToInt().toString()
            binding.windSpeed.text = windSpeed.roundToInt().toString()
            binding.windDirection.rotation = windDegrees.toFloat()
            binding.weatherDescription.text = localizedDescription.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.ENGLISH) else it.toString()
            }
            condition.run { setWeatherConditionIcon(getDayIcon()) }
        }

        weatherData.forecastWeatherData.run {
                adapter.submitList(this)




        }
    }

    private fun setWeatherConditionIcon(@DrawableRes int: Int) {
        context?.let {
            binding.currentWeatherIcon.setImageDrawable(ContextCompat.getDrawable(it, int))
        }
    }

    private fun showError(exception: Throwable?) {
        // TODO create error view or show dialog
        // show "you are offline" in case there is no network and the request failed
        binding.currentWeatherGroup.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
