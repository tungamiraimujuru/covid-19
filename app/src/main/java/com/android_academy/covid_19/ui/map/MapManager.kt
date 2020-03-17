package com.android_academy.covid_19.ui.map

import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.android_academy.covid_19.R
import com.android_academy.covid_19.ui.activity.LocationMarkerData
import com.android_academy.covid_19.ui.map.MapManager.InteractionInterface
import com.android_academy.covid_19.util.logTag
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import timber.log.Timber

interface MapManager : OnMapReadyCallback {

    fun onMyLocationsChanged(markerOptions: List<LocationMarkerData>?)
    fun onCoronaChanged(markerOptions: List<LocationMarkerData>?)

    interface InteractionInterface {
        fun onUserHistoryLocationMarkerSelected(data: LocationMarkerData)
    }
}

class MapManagerImpl(
    private val interactionInterface: InteractionInterface,
    private val context: Context
) : MapManager {

    private lateinit var map: GoogleMap

    private val myLocations = mutableMapOf<Int, Marker>()

    private val coronaLocations = mutableMapOf<Int, Marker>()

    private var selectedCoronaLocation: Marker? = null

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.setOnMarkerClickListener { clicked ->

            if (clicked.tag == null || clicked.tag == selectedCoronaLocation?.tag) return@setOnMarkerClickListener false

            selectedCoronaLocation?.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.not_selected_circle))
            clicked.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.selected_circle))
            selectedCoronaLocation = clicked
            interactionInterface.onUserHistoryLocationMarkerSelected(clicked.tag as LocationMarkerData)

            return@setOnMarkerClickListener false
        }

        val address = Geocoder(context).getFromLocationName("Israel", 1);
        if (address == null) {
            Timber.e(Throwable(), "[MapManagerImpl], onMapReady: Israel location not found")
        } else {
            val loc = address[0];

            val pos = LatLng(loc.latitude, loc.longitude)
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 7.3F))
        }
        googleMap.isMyLocationEnabled = true
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = true
    }

    override fun onMyLocationsChanged(markerOptions: List<LocationMarkerData>?) {
        markerOptions?.forEach { options ->
            myLocations[options.id]?.remove()
            myLocations[options.id] = map.addMarker(createMyLocationMarkerOptions(options))
        }
    }

    override fun onCoronaChanged(markerOptions: List<LocationMarkerData>?) {
        markerOptions?.forEach { options ->
            coronaLocations[options.id]?.let {
                it.remove()
            }

            val marker = map.addMarker(
                createCoronaLocationMarkerOptions(
                    options,
                    R.drawable.not_selected_circle
                )
            )
            marker.tag = options
            coronaLocations[options.id] = marker
        }
    }

    private fun createMyLocationMarkerOptions(options: LocationMarkerData): MarkerOptions {
        return MarkerOptions()
            .position(LatLng(options.lat, options.lon))
            .title(options.title)
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_location_icon))
    }

    private fun createCoronaLocationMarkerOptions(
        options: LocationMarkerData,
        coronaIcon: Int
    ): MarkerOptions {
        return MarkerOptions()
            .position(LatLng(options.lat, options.lon))
            .title(options.title)
            .icon(BitmapDescriptorFactory.fromResource(coronaIcon))
    }
}
