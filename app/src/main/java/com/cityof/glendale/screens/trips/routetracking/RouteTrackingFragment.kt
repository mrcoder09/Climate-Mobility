package com.cityof.glendale.screens.trips.routetracking

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Looper
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.cityof.glendale.R
import com.cityof.glendale.databinding.FragmentRouteTrackingBinding
import com.cityof.glendale.network.googleresponses.Route
import com.cityof.glendale.network.googleresponses.getDestinationLoc
import com.cityof.glendale.network.googleresponses.getDistanceFormatted
import com.cityof.glendale.network.googleresponses.getHtmlInstruction
import com.cityof.glendale.network.googleresponses.getLegStart
import com.cityof.glendale.network.googleresponses.getOriginLoc
import com.cityof.glendale.network.googleresponses.getPolyline
import com.cityof.glendale.network.googleresponses.toLatLng
import com.cityof.glendale.network.responses.SavedTrip
import com.cityof.glendale.network.responses.getCircledIcon
import com.cityof.glendale.network.responses.isBeeline
import com.cityof.glendale.network.responses.isTripOngoing
import com.cityof.glendale.network.responses.toIcon
import com.cityof.glendale.network.responses.toRoute
import com.cityof.glendale.utils.angleInDegrees
import com.cityof.glendale.utils.animateMarker
import com.cityof.glendale.utils.animateWithTilt
import com.cityof.glendale.utils.bitmapDescriptorFromVector
import com.cityof.glendale.utils.hasLocationPermission
import com.cityof.glendale.utils.isInDistance
import com.cityof.glendale.utils.moveCameraToBounds
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber


const val TAG = "TAG_ROUTE_TRACK"


@AndroidEntryPoint
class RouteTrackingFragment(
    private val onExit: () -> Unit,
    private val onFeedback: (LatLng?, String?) -> Unit,
    private val onPop: () -> Unit
) : Fragment() {

    //FRAGMENT's
    private var pd: Dialog? = null
    private lateinit var binding: FragmentRouteTrackingBinding
    private lateinit var routeIn: Route
    private val viewModel: RouteTrackingViewModel by viewModels<RouteTrackingViewModel>()

    //GOOGLE MAP OBJECTS
    private lateinit var googleMap: GoogleMap
    private lateinit var polyline: Polyline
    lateinit var originMarker: Marker
    lateinit var destinationMarker: Marker


    //LOCATION OBJECTS
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {

            if (locationResult.locations.isNotEmpty()) locationResult.locations[0]?.let { loc ->
                Timber.d("$TAG ${loc.latitude} ${loc.longitude}")

                val latLng = LatLng(loc.latitude, loc.longitude)
                viewModel.dispatch(RouteTrackingContract.Intent.UpdateCurrentLatLng(latLng))

                if (viewModel.state.value.tripIn?.isTripOngoing() == true) {
                    Timber.d("$TAG TRIP IS ONGOING")
                    if (latLng.isInDistance(originMarker.position)) {
                        viewModel.dispatch(
                            RouteTrackingContract.Intent.GetDirections(
                                currentLatLng = latLng
                            )
                        )

                        val angle = latLng.angleInDegrees(originMarker.position)
                        val previousLatLng = originMarker.position
                        originMarker.rotation = angle.toFloat()
                        animateMarker(originMarker, previousLatLng, latLng)
                        googleMap.animateWithTilt(previousLatLng, latLng)
                    }

                } else {
                    Timber.d("$TAG TRIP IS NOT ONGOING")
                }

            }
        }
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val fineLocationResult = result[android.Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationResult =
            result[android.Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (fineLocationResult && coarseLocationResult) {
            requestLocation()
        }
    }


    companion object {

        private const val SAVE_TRIP = "SAVED_TRIP"

        fun newInstance(
            trip: SavedTrip?,
            onExit: () -> Unit,
            onFeedback: (LatLng?, String?) -> Unit,
            onPop: () -> Unit
        ): RouteTrackingFragment {
            val fragment = RouteTrackingFragment(onExit, onFeedback, onPop)
            val args = Bundle()
            args.putParcelable(SAVE_TRIP, trip)
            fragment.arguments = args
            return fragment
        }


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Timber.d("HandleOnBackPressed")
                removeFragmentFromBackStack()
                onPop()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        if (this::binding.isInitialized.not()) {
            binding = FragmentRouteTrackingBinding.inflate(inflater)
            handleViews()
        }
        binding.lifecycleOwner = this
        return binding.root
    }

    private fun handleViews() {
        binding.let { routeTracking ->
            routeTracking.ivClose.setOnClickListener {
                requireActivity().onBackPressed()
            }
            viewModel.state.value.tripIn?.toIcon()?.let { icon ->
                routeTracking.ivTravelMode.setImageResource(icon)
            }
            routeTracking.btnExit.setOnClickListener {
                val isExit = viewModel.state.value.isExitTrip
                if (isExit) {
                    viewModel.dispatch(RouteTrackingContract.Intent.ExitTrip)
                } else {
                    viewModel.dispatch(RouteTrackingContract.Intent.StartTrip)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        arguments?.let {
            it.getParcelable<SavedTrip>(SAVE_TRIP)?.let { trip ->
                viewModel.init(trip)
                trip.toRoute()?.let { route: Route ->
                    routeIn = route

                }
            }
        }

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        updateUi(routeIn)
        mapFragment.getMapAsync { googleMap ->

            this.googleMap = googleMap
            routeIn.getOriginLoc()?.toLatLng()?.let { latLng ->
                originMarker = googleMap.addMarker(
                    MarkerOptions().position(latLng).icon(
                        bitmapDescriptorFromVector(
                            requireActivity(), R.drawable.ic_current_loc_marker
                        )
                    )
                )!!
            }

            routeIn.getDestinationLoc()?.toLatLng()?.let { latlng ->
                destinationMarker = googleMap.addMarker(
                    MarkerOptions().position(latlng).icon(
                        bitmapDescriptorFromVector(requireActivity(), R.drawable.ic_src_destination)
                    )
                )!!
            }

            routeIn.getPolyline()?.let {
                googleMap.moveCameraToBounds(it)
                val polylineOptions =
                    PolylineOptions().color(requireActivity().resources.getColor(R.color.colorPrimary))
                        .width(12f)
                polylineOptions.addAll(it)

                polyline = googleMap.addPolyline(polylineOptions)
                polyline.zIndex = 2.0f
                polyline.isGeodesic = true
            }

            requestLocation()
        }


        viewModel.tripActivityEvent.observe(this.viewLifecycleOwner) {
            it?.let { tripActivity ->
                feedbackAlert("${tripActivity.point}", "${tripActivity.emission}")
            }
        }

        viewModel.isLoading.observe(this.viewLifecycleOwner) {
            lifecycleScope.launch {
                if (it) {
                    showLoader()
                } else {
                    pd?.dismiss()
                    pd = null
                }
            }
        }



        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {


                viewModel.state.collectLatest { it ->

                    Timber.d(
                        "COLLECT_LATEST"
                    )

                    it.toastMsg?.let { uiStr ->
                        val msg = uiStr.toStr(requireActivity())
                        lifecycleScope.launch {
                            if (msg.isNotEmpty()) Toast.makeText(
                                requireContext(), msg, Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    it.onExit.let {
                        if (it) {
                            onExit()
                            onStop()
                        }
                    }

                    it.isExitTrip.let { isExit ->
                        if (isExit) {
                            binding.ivClose.visibility = View.GONE
                            binding.btnExit.text = getString(R.string.exit)
                        } else {
                            binding.ivClose.visibility = View.VISIBLE
                            binding.btnExit.text = getString(R.string.start)
                        }
                    }

                    it.userRoute?.let { userRoute ->
                        userRoute.getPolyline()?.let { list ->
                            if (list.isNotEmpty()) {
                                polyline.points = list
                            }
                        }
                        updateUi(userRoute)
                    }

                    it.tripIn?.getCircledIcon()?.let { icon ->
                        binding.ivTravelMode.setImageResource(icon)
                    }

                }
            }
        }
    }


    override fun onStop() {
        super.onStop()
        Timber.d("ONSTOP")
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }


    @SuppressLint("MissingPermission")
    fun requestLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())


        val locationRequest = LocationRequest()
        locationRequest.setInterval(10000) // 10 seconds
        locationRequest.setFastestInterval(5000) // 5 seconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        if (requireActivity().hasLocationPermission().not()) {
            permissionLauncher.launch(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            fusedLocationClient.requestLocationUpdates(
                locationRequest, locationCallback, Looper.getMainLooper()
            )

        }
    }

    private fun updateUi(route: Route) {
        binding.tvRouteName.text = route.getLegStart() ?: ""
        binding.tvHtmlInstruction.text = route.getHtmlInstruction()?.let {
            Html.fromHtml(it, HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
        binding.tvDistanceToDestination.text = route.getDistanceFormatted() ?: ""
    }

    private fun showLoader() {
        if (pd == null) {
            pd = Dialog(requireContext())
            pd?.setContentView(R.layout.custom_progress_dialog)
            pd?.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
            pd?.setCanceledOnTouchOutside(false)
        }
        pd?.show()
    }

    private fun feedbackAlert(
        points: String?, emission: String?
    ) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.feedback_alert)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false)

        val tvFeedbackMsg = dialog.findViewById<TextView>(R.id.tvFeedbackMsg)
        val feedback = getString(R.string.msg_successfully_completed, points, emission)
        tvFeedbackMsg.text = feedback

        val tvOkay = dialog.findViewById<TextView>(R.id.tvOkay)
        val constraintLayout = dialog.findViewById<ConstraintLayout>(R.id.clFeedback)

        if (viewModel.state.value.tripIn?.isBeeline() == true) {
            constraintLayout.visibility = View.VISIBLE
            tvOkay.visibility = View.GONE
        } else {
            constraintLayout.visibility = View.GONE
            tvOkay.visibility = View.VISIBLE
        }

        tvOkay.setOnClickListener {
            dialog.dismiss()
            onExit()
            removeFragmentFromBackStack()
        }

        dialog.findViewById<TextView>(R.id.tvYes).setOnClickListener {
            dialog.dismiss()
            onFeedback(viewModel.state.value.currentLatLng, viewModel.state.value.vehicleId)
        }

        dialog.findViewById<TextView>(R.id.tvNo).setOnClickListener {
            tvOkay.performClick()
            removeFragmentFromBackStack()
        }

        fusedLocationClient.removeLocationUpdates(locationCallback)
        dialog.show()
    }


    private fun removeFragmentFromBackStack() {
        parentFragmentManager.beginTransaction().remove(this).commit()
    }
}