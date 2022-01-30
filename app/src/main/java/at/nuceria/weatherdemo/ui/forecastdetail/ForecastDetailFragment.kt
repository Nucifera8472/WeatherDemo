package at.nuceria.weatherdemo.ui.forecastdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.SharedElementCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.transition.Transition
import androidx.transition.TransitionInflater
import androidx.transition.TransitionListenerAdapter
import androidx.transition.TransitionSet
import at.nuceria.weatherdemo.R
import at.nuceria.weatherdemo.data.Resource
import at.nuceria.weatherdemo.data.local.getDayIcon
import at.nuceria.weatherdemo.data.model.WeatherData
import at.nuceria.weatherdemo.databinding.FragmentForecastDetailBinding
import at.nuceria.weatherdemo.ui.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class ForecastDetailFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prepareSharedElementTransition()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
    }

    private val viewModel by activityViewModels<WeatherViewModel>()

    private var _binding: FragmentForecastDetailBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentForecastDetailBinding.inflate(inflater, container, false)
        val view = binding.root
        observeViewModel()
        return view
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            // repeatOnLifecycle launches the block in a new coroutine every time the
            // lifecycle is in the STARTED state (or above) and cancels it when it's STOPPED.
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                Timber.d("Collect weather flow")
                // Start listening for values.
                viewModel.weatherData.collect { onNewDataReceived(it) }
            }
        }
    }

    private fun onNewDataReceived(resource: Resource<out WeatherData?>) {
        Timber.d("onNewDataReceived")
        if (resource.data != null) {
            val dailyData =
                resource.data.forecastWeatherData.getOrNull(viewModel.selectedForecastPosition)
                    ?: return

            context?.let { context ->
                binding.currentWeatherIcon.setImageDrawable(
                    ContextCompat.getDrawable(context, dailyData.condition.getDayIcon())
                )
            }
        }
        // data is ready, start transition
        startPostponedEnterTransition()
    }

    /**
     * Prepares the shared element transition from and back to the grid fragment.
     */
    private fun prepareSharedElementTransition() {
        val transition =
            TransitionInflater.from(context).inflateTransition(R.transition.shared_image)
        sharedElementEnterTransition = transition
        addSharedElementListener()

        // A similar mapping is set at the GridFragment with a setExitSharedElementCallback.
        setEnterSharedElementCallback(object : SharedElementCallback() {
            override fun onMapSharedElements(
                names: List<String?>,
                sharedElements: MutableMap<String?, View?>
            ) {
                // Map the incoming transition name (including the position) to the image in
                // this view.
                sharedElements[names[0]] = binding.currentWeatherIcon
            }
        })
    }

    private fun addSharedElementListener() {
        (sharedElementEnterTransition as TransitionSet).addListener((object :
            TransitionListenerAdapter() {
            override fun onTransitionEnd(transition: Transition) {
                super.onTransitionEnd(transition)
                // start reveal animation
                binding.maxIcon.visibility = View.VISIBLE
            }
        }))
    }
}
