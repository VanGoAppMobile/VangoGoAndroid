package com.vango.presentation.main.home

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.test.core.app.ApplicationProvider
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vango.domain.model.SearchResult
import com.vango.domain.usecase.location.GetUserLocationUseCase
import com.vango.domain.usecase.places.SearchPlacesUseCase
import com.vango.presentation.main.home.components.MapLayer
import com.vango.presentation.main.home.components.MapNewPointRoute
import com.vango.presentation.main.home.components.MapOption
import com.vango.shared.dtos.places.PlacesRequestDto
import com.vango.shared.dtos.places.PlacesResponseDto
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUserLocationUseCase: GetUserLocationUseCase,
    private val searchPlacesUseCase: SearchPlacesUseCase,
    private val geocoder: Geocoder,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _currentLocation = MutableStateFlow(LatLng(40.416775, -3.703790))
    val currentLocation: StateFlow<LatLng> = _currentLocation

    private val _selectedLayer = MutableStateFlow(MapLayer.NORMAL)
    val selectedLayer: StateFlow<MapLayer> = _selectedLayer.asStateFlow()

    private val _selectedOption = MutableStateFlow<MapOption?>(null)
    val selectedOption: StateFlow<MapOption?> = _selectedOption.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _hasToRequestPermission = MutableStateFlow(false)
    val hasToRequestPermission: StateFlow<Boolean> = _hasToRequestPermission.asStateFlow()

    private val _searchResults = MutableStateFlow<List<SearchResult>>(emptyList())
    val searchResults: StateFlow<List<SearchResult>> = _searchResults.asStateFlow()


    private val _selectedRoutePoint = MutableStateFlow<MapNewPointRoute?>(null)
    val selectedRoutePoint: StateFlow<MapNewPointRoute?> = _selectedRoutePoint.asStateFlow()

    var isLocationActive: Boolean = false
        private set

    private val _selectedPoint = MutableStateFlow<LatLng?>(null)
    val selectedPoint: StateFlow<LatLng?> = _selectedPoint.asStateFlow()

    private val _selectedAddress = mutableStateOf<String?>("Cargando dirección...")
    val selectedAddress: State<String?> = _selectedAddress

    private val _selectedName = MutableStateFlow<String?>(null)
    val selectedName: StateFlow<String?> = _selectedName.asStateFlow()

    private val _pointName = MutableStateFlow<String?>(null)
    val pointName: StateFlow<String?> = _pointName.asStateFlow()

    private val _nearbyPlaces = MutableStateFlow<List<PlacesResponseDto>>(emptyList())
    val nearbyPlaces: StateFlow<List<PlacesResponseDto>> = _nearbyPlaces.asStateFlow()

    private var photoUrls = mutableListOf<String>()

    private val _selectedServices = MutableStateFlow<Set<String>>(emptySet())
    val selectedServices: StateFlow<Set<String>> = _selectedServices

    fun setPhotoUrls(urls: List<String>) {
        photoUrls.clear()
        photoUrls.addAll(urls)
        Log.d("HomeViewModel", "Photo URLs set: $urls")
    }

    fun saveNewPointToApi(
        latitude: Double,
        longitude: Double,
        address: String,
        pricePerDay: Double? = null
    ) {
        viewModelScope.launch {
            try {
                val locationString = "$latitude,$longitude"
                Log.d("HomeViewModel", "Creando punto con photoUrls: $photoUrls")
                val newPoint = PlacesResponseDto(
                    title = _selectedName.value ?: "Unnamed Point",
                    address = address,
                    location = locationString,
                    rating = null,
                    userVotes = null,
                    photoUrls = photoUrls.takeIf { it.isNotEmpty() }?.toList(),
                    type = null,
                    placeId = null,
                    currentOpeningHours = null,
                    opening_hours = null,
                    international_phone_number = null,
                    formattedPhoneNumber = null,
                    website = null,
                    reviews = null,
                    source = null
                )
                _nearbyPlaces.value = _nearbyPlaces.value + newPoint


                clearSelectedPoint()
                photoUrls.clear()
                Log.d("HomeViewModel", "PhotoUrls limpiado: $photoUrls")
            } catch (e: Exception) {
                _errorMessage.value = "Error al guardar el punto: ${e.message}"
            }
        }
    }

    fun savePoints(points: List<Pair<LatLng?, String?>>) {
        if (points.size == 1) {
            Log.d("HomeViewModel", "Nuevo punto guardado: ${points[0]}")
        } else {
            Log.d("HomeViewModel", "Ruta guardada con ${points.size} puntos: $points")
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        performSearch(query)
    }

    fun searchNearbyPlaces(latLng: LatLng, radius: Int, placeType: Int) {
        viewModelScope.launch {
            try {
                val request = PlacesRequestDto(
                    lat = latLng.latitude,
                    lng = latLng.longitude,
                    radius = radius,
                    placeType = placeType
                )

                val results = searchPlacesUseCase.searchNearby(request)
                _nearbyPlaces.value = results
                Log.d("HomeViewModel", "Lugares cercanos encontrados: ${results.size}")
            } catch (e: Exception) {
                _errorMessage.value = "Error al buscar lugares cercanos: ${e.message}"
                _nearbyPlaces.value = emptyList()
            }
        }
    }

    fun saveSelectedServices(services: Set<String>) {
        // Guardar los servicios seleccionados en el estado del ViewModel o enviarlos a un repositorio
        // Por ejemplo:
        _selectedServices.value = services
    }


//    fun selectPoint(latLng: LatLng) {
//        viewModelScope.launch {
//            _selectedPoint.value = latLng
//            getAddressFromLatLng(latLng)
//            delay(100)
//        }
//    }

    fun selectPoint(latLng: LatLng) {
        viewModelScope.launch {
            Log.d("HomeViewModel", "Selecting point: $latLng")
            _selectedPoint.value = latLng
            _selectedAddress.value = "Cargando dirección..."
            val address = getAddressFromLatLng(latLng)
            Log.d("HomeViewModel", "Address retrieved: $address")
            _selectedAddress.value = address
        }
    }


    private suspend fun getAddressFromLatLng(latLng: LatLng): String? {
        return withContext(Dispatchers.IO) {
            try {
                val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                addresses?.firstOrNull()?.getAddressLine(0) ?: "No se encontró dirección"
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error getting address: ${e.message}")
                "Error al obtener dirección"
            }
        }
    }


//    private suspend fun getAddressFromLatLng(latLng: LatLng): String? = withContext(Dispatchers.IO) {
//        try {
//            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
//            addresses?.firstOrNull()?.getAddressLine(0)
//        } catch (e: Exception) {
//            Log.e("HomeViewModel", "Error getting address: ${e.message}")
//            null
//        }
//    }


//    private suspend fun getAddressFromLatLng(latLng: LatLng) {
//        try {
//            val geocoder = Geocoder(context, Locale.getDefault())
//            val addresses = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
//            } else {
//                @Suppress("DEPRECATION")
//                geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
//            }
//            if (!addresses.isNullOrEmpty()) {
//                _selectedAddress.value = addresses[0].getAddressLine(0) ?: "Unknown address"
//            } else {
//                _errorMessage.value = "No address found for this location"
//            }
//        } catch (e: Exception) {
//            _errorMessage.value = "Error getting address: ${e.message}"
//        }
//    }

    fun performSearch(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }
        viewModelScope.launch {
            try {
                val results = searchPlacesUseCase(query, _currentLocation.value).map {
                    SearchResult(it.name, it.latitude, it.longitude, it.placeId, it.secondaryText, it.types,it.distanceMeters)
                }
                _searchResults.value = results
            } catch (e: Exception) {
                _errorMessage.value = "Error al buscar: ${e.message}"
            }
        }
    }

    fun selectSearchResult(result: SearchResult) {
        viewModelScope.launch {
            try {
                val detailedResult = result.placeId?.let { searchPlacesUseCase.getPlaceDetails(it) }
                if (detailedResult != null) {
                    _currentLocation.value = searchPlacesUseCase.toLatLng(detailedResult)
                    _searchResults.value = emptyList()
                    _searchQuery.value = detailedResult.name
                } else {
                    _errorMessage.value = "No se pudieron obtener los detalles del lugar."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error al obtener detalles: ${e.message}"
            }
        }
    }

    fun fetchUserLocation() {
        viewModelScope.launch {
            val location = getUserLocationUseCase()
            if (location != null) {
                _currentLocation.value = location
            } else {
                _errorMessage.value = "Se necesitan permisos de ubicación para obtener tu posición actual."
                _hasToRequestPermission.value = true
                _currentLocation.value = LatLng(40.416775, -3.703790)
            }
        }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun clearPermissionRequest() {
        _hasToRequestPermission.value = false
    }

    fun updateMapLayer(layer: MapLayer) {
        _selectedLayer.value = layer
    }

    fun updateMapOption(option: MapOption?) {
        _selectedOption.value = option
    }

    fun selectRoutePoint(point: MapNewPointRoute?) {
        _selectedRoutePoint.value = point
    }

    fun setIsLocationActive(isActive: Boolean) {
        isLocationActive = isActive
    }

    fun clearSelectedPoint() {
        _selectedPoint.value = null
        _selectedAddress.value = null
        _pointName.value = null
    }

    fun saveRouteAndClear(context: Context, pointsList: MutableList<Pair<LatLng?, String?>>) {
        val gson = Gson()
        val json = gson.toJson(pointsList.map { point ->
            mapOf(
                "latitude" to point.first?.latitude,
                "longitude" to point.first?.longitude,
                "address" to point.second
            )
        })

        val prefs = context.getSharedPreferences("RoutesPrefs", Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putString("savedRoute", json)
            apply()
        }

        Log.d("HomeViewModel", "Route saved to SharedPreferences: $json")

        pointsList.clear()
        clearSelectedPoint()
    }

    fun loadSavedRoute(context: Context): List<Pair<LatLng?, String?>> {
        val prefs = context.getSharedPreferences("RoutesPrefs", Context.MODE_PRIVATE)
        val json = prefs.getString("savedRoute", null) ?: return emptyList()
        val gson = Gson()
        val type = object : TypeToken<List<Map<String, Any?>>>() {}.type
        val routeList: List<Map<String, Any?>> = gson.fromJson(json, type)

        return routeList.map { map ->
            val lat = map["latitude"] as? Double
            val lng = map["longitude"] as? Double
            val address = map["address"] as? String
            Pair(if (lat != null && lng != null) LatLng(lat, lng) else null, address)
        }
    }

    fun saveNewPoint(name: String) {

        _selectedName.value = name
//        selectedPoint.value?.let { point ->
//            println("Punto guardado: $name en ($point)")
//            clearSelectedPoint()
//        }
    }

}