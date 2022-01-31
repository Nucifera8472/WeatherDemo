package at.nuceria.weatherdemo.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLayoutChangeListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.core.app.SharedElementCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import androidx.transition.TransitionInflater
import androidx.transition.TransitionSet
import at.nuceria.weatherdemo.R
import at.nuceria.weatherdemo.data.Resource
import at.nuceria.weatherdemo.data.local.getDayIcon
import at.nuceria.weatherdemo.data.location.LocationHandler
import at.nuceria.weatherdemo.data.model.WeatherData
import at.nuceria.weatherdemo.databinding.MainFragmentBinding
import at.nuceria.weatherdemo.ui.WeatherViewModel
import at.nuceria.weatherdemo.ui.forecastdetail.ForecastDetailFragment
import at.nuceria.weatherdemo.util.OnSnapPositionChangeListener
import at.nuceria.weatherdemo.util.SnapOnScrollListener
import at.nuceria.weatherdemo.util.attachSnapHelperWithListener
import at.nuceria.weatherdemo.util.to24hTime
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import kotlin.math.roundToInt


@AndroidEntryPoint
class MainFragment : Fragment(), OnSnapPositionChangeListener,
    ForecastPreviewAdapter.ItemClickListener {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val viewModel by activityViewModels<WeatherViewModel>()

    private var _binding: MainFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val adapter = ForecastPreviewAdapter(this)
    private val snapHelper = PagerSnapHelper() // makes the recyclerview act like a viewpager


    // coarse location permission callback
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Timber.d("Permission Granted")
                lifecycleScope.launch {
                    // repeatOnLifecycle launches the block in a new coroutine every time the
                    // lifecycle is in the STARTED state (or above) and cancels it when it's STOPPED.
                    repeatOnLifecycle(Lifecycle.State.STARTED) {
                        // Trigger the flow and start listening for values.
                        viewModel.retrieveWeatherForCurrentLocation()
                        viewModel.weatherData.collect { onNewDataReceived(it) }
                    }
                }
            } else {
                // Explain to the user that the feature is unavailable because the
                // features requires a permission that the user has denied.
                showError(LocationHandler.MissingLocationPermissionError())
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MainFragmentBinding.inflate(inflater, container, false)
        val view = binding.root

        prepareTransitions()
        postponeEnterTransition()

//        binding.weatherResultView.more.setOnClickListener {
//            if (isAdded) {
//                parentFragmentManager.commit {
//                    setCustomAnimations(
//                        R.anim.slide_in_bottom,
//                        R.anim.slide_out_top,
//                        R.anim.slide_in_top,
//                        R.anim.slide_out_bottom
//                    )
//                    replace(R.id.container, ForecastsFragment())
//                    addToBackStack(null)
//                }
//            }
//        }

        binding.weatherResultView.forecastTiles.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.weatherResultView.forecastTiles.adapter = adapter
        snapHelper.attachToRecyclerView(binding.weatherResultView.forecastTiles)
        snapHelper.attachToRecyclerView(binding.weatherResultView.forecastTiles)
        binding.weatherResultView.forecastTiles.attachSnapHelperWithListener(
            snapHelper,
            SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL_STATE_IDLE,
            this
        )
        binding.weatherResultView.viewOverflowPagerIndicator.attachToRecyclerView(binding.weatherResultView.forecastTiles)

        binding.weatherResultView.root.visibility = View.GONE
        binding.errorView.root.visibility = View.GONE

        requestPermissionLauncher.launch("android.permission.ACCESS_COARSE_LOCATION")
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // restore state when returning from the detail view
        scrollToPosition()
    }

    private fun onNewDataReceived(resource: Resource<out WeatherData?>) {
        Timber.d("onNewDataReceived")
        if (resource.data != null) {
            binding.weatherResultView.root.visibility = View.VISIBLE
            showData(resource.data)
        } else {
            binding.weatherResultView.root.visibility = View.GONE
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

        if (resource is Resource.Error && resource.data == null) {
            binding.errorView.root.visibility = View.VISIBLE
            showError(resource.error)
        } else {
            binding.errorView.root.visibility = View.GONE
        }
    }

    private fun showData(weatherData: WeatherData) {
        weatherData.currentWeatherData.let { data ->
            binding.weatherResultView.run {
                root.visibility = View.VISIBLE
                // we don't want to show decimals for the values
                temperature.text = data.temperature.roundToInt().toString()
                wind.windSpeed.text = data.windSpeed.roundToInt().toString()
                wind.windDirection.rotation =
                    data.windDegrees.toFloat() + 180 // the icon is "upside down"
                weatherDescription.text = data.localizedDescription.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                }
                precipitation.precipitationAmount.text =
                    if (data.precipitationAmount == 0f) "0"
                    else String.format("%.1f", data.precipitationAmount)
                details.sunupTime.text = weatherData.todayOverView.localSunrise?.to24hTime()
                details.sunDownTime.text = weatherData.todayOverView.localSunset?.to24hTime()
                details.dailyCondition.text =
                    weatherData.todayOverView.localizedDescription.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                    }
                details.temperatureDetails.minTemperature.text = getString(
                    R.string._c,
                    weatherData.todayOverView.minTemperature.roundToInt().toString()
                )
                details.temperatureDetails.maxTemperature.text = getString(
                    R.string._c,
                    weatherData.todayOverView.maxTemperature.roundToInt().toString()
                )
                details.precipitationDetails.precipitationProbability.text = getString(
                    R.string.percent,
                    (weatherData.todayOverView.precipitationProbability * 100).roundToInt()
                        .toString()
                )
                details.precipitationDetails.precipitationAmount.text = getString(
                    R.string._mm,
                    if (weatherData.todayOverView.precipitationAmount == 0f) "0"
                    else String.format("%.1f", weatherData.todayOverView.precipitationAmount)
                )
                data.condition.run {
                    setWeatherConditionIcon(
                        binding.weatherResultView.currentWeatherIcon,
                        getDayIcon()
                    )
                }
                weatherData.todayOverView.condition.run {
                    setWeatherConditionIcon(
                        details.dailyConditionIcon,
                        getDayIcon()
                    )
                }
            }
        }

        weatherData.forecastWeatherData.run {
            adapter.submitList(this)


        }
    }

    private fun setWeatherConditionIcon(icon: ImageView, @DrawableRes int: Int) {
        context?.let {
            icon.setImageDrawable(
                ContextCompat.getDrawable(
                    it,
                    int
                )
            )
        }
    }

    private fun showError(exception: Throwable?) {
        Timber.e(exception)
        val message = when (exception) {
            is LocationHandler.LocationNullError -> getString(R.string.your_location_could_not_be_determined)
            is LocationHandler.MissingLocationPermissionError -> getString(R.string.location_permission_required)
            else -> getString(R.string.something_went_wrong)
        }

        showError(message)
    }

    private fun showError(message: String) {
        Timber.e("ERROR: $message")
        // TODO create error view or show dialog
        // show "you are offline" in case there is no network and the request failed
        binding.errorView.message.text = message
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun prepareTransitions() {
        // fade out the view while entering the shared element transition
        exitTransition = TransitionInflater.from(context).inflateTransition(R.transition.exit_fade)

        setExitSharedElementCallback(
            object : SharedElementCallback() {
                override fun onMapSharedElements(
                    names: List<String?>, sharedElements: MutableMap<String?, View?>
                ) {
                    // Locate the ViewHolder for the clicked position.
                    val selectedViewHolder =
                        binding.weatherResultView.forecastTiles.findViewHolderForAdapterPosition(
                            viewModel.selectedForecastPosition
                        ) as ForecastPreviewAdapter.DailyWeatherDataViewHolder? ?: return

                    // add necessary mapping to the recycler viewholder
                    sharedElements[names[0]] = selectedViewHolder.itemBinding.currentWeatherIcon
                }
            })
    }

    private fun openDetail(position: Int) {
        val viewHolder = getSelectedForecastViewHolder()?.itemBinding
        val sharedViews = listOfNotNull(
            viewHolder?.currentWeatherIcon
        )

        // Exclude the shared views from the exit transition to prevent an overlapping animation of
        // fade and move here. The shared elements should always stay visible
        sharedViews.forEach {
            (exitTransition as TransitionSet).excludeTarget(it, true)
        }

        if (isAdded) {
            parentFragmentManager.commit {
                setReorderingAllowed(true)
                sharedViews.forEach {
                    addSharedElement(it, it.transitionName)
                }
                replace(R.id.container, ForecastDetailFragment())
                addToBackStack(null)
            }
        }
    }

    private fun getSelectedForecastViewHolder(): ForecastPreviewAdapter.DailyWeatherDataViewHolder? {
        // Locate the ViewHolder for the currently selected position
        return binding.weatherResultView.forecastTiles.findViewHolderForAdapterPosition(
            viewModel.selectedForecastPosition
        ) as ForecastPreviewAdapter.DailyWeatherDataViewHolder?
    }

    /**
     * Scrolls the recycler view to show the last viewed item in the grid. This is important when
     * navigating back from the grid.
     */
    private fun scrollToPosition() {

        val onScrollListener: RecyclerView.OnScrollListener =
            object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    when (newState) {
                        SCROLL_STATE_IDLE -> {
                            //we reached the target position
                            recyclerView.removeOnScrollListener(this)
                            startPostponedEnterTransition()
                        }

                    }
                }
            }
        binding.weatherResultView.forecastTiles.addOnLayoutChangeListener(object :
            OnLayoutChangeListener {
            override fun onLayoutChange(
                v: View,
                left: Int,
                top: Int,
                right: Int,
                bottom: Int,
                oldLeft: Int,
                oldTop: Int,
                oldRight: Int,
                oldBottom: Int
            ) {
                binding.weatherResultView.forecastTiles.let { recyclerView ->
                    recyclerView.removeOnLayoutChangeListener(this)
                    val layoutManager = recyclerView.layoutManager
                    val viewAtPosition =
                        layoutManager?.findViewByPosition(viewModel.selectedForecastPosition)
                    // Scroll to position if the view for the current position is null (not currently part of
                    // layout manager children), or it's not completely visible.
                    if (viewAtPosition == null || layoutManager.isViewPartiallyVisible(
                            viewAtPosition,
                            false,
                            true
                        )
                    ) {
                        recyclerView.post {
                            recyclerView.clearOnScrollListeners()
                            recyclerView.addOnScrollListener(onScrollListener)
                            layoutManager?.scrollToPosition(viewModel.selectedForecastPosition)
                        }
                    } else {
                        // we don't need to scroll first, so the recycler is ready for the shared element transition
                        startPostponedEnterTransition()
                    }
                }
            }
        })
    }

    override fun onSnapPositionChange(position: Int, wasUserScroll: Boolean) {
        viewModel.selectedForecastPosition = position // needed for correct enter transition
    }

    override fun onForecastPreviewItemClicked(position: Int) {
        openDetail(position)
    }

}
