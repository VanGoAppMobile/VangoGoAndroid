package com.vango.presentation.main.home

import android.Manifest
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.rememberCameraPositionState
import com.vango.R
import com.vango.presentation.main.home.components.BottomActionButtons
import com.vango.presentation.main.home.components.BottomCoordButton
import com.vango.presentation.main.home.components.FilterMenu
import com.vango.presentation.main.home.components.FullScreenPlaceList
import com.vango.presentation.main.home.components.LocationActionButtons
import com.vango.presentation.main.home.components.MapComponent
import com.vango.presentation.main.home.components.MapLayersMenu
import com.vango.presentation.main.home.components.MapNewImageServiceMenu
import com.vango.presentation.main.home.components.MapNewImageServiceUploadMenu
import com.vango.presentation.main.home.components.MapNewLastDatesMenu
import com.vango.presentation.main.home.components.MapNewPointConfirmMenu
import com.vango.presentation.main.home.components.MapNewPointMenu
import com.vango.presentation.main.home.components.MapNewPointNameMenu
import com.vango.presentation.main.home.components.MapNewPointRoute
import com.vango.presentation.main.home.components.MapNewPointTagMenu
import com.vango.presentation.main.home.components.MapNewPointTagServicesMenu
import com.vango.presentation.main.home.components.MapNewRoutePointMenu
import com.vango.presentation.main.home.components.SearchBar
import com.vango.presentation.main.home.components.TopCenterButton
import com.vango.presentation.main.home.components.TopCenterButtonSelectedPoint
import com.vango.presentation.theme.BackgroundButtonColor
import com.vango.presentation.theme.BackgroundColorBadge
import com.vango.presentation.theme.BackgroundColorCard
import com.vango.presentation.theme.BlackGray
import com.vango.presentation.theme.MainColor
import com.vango.shared.dtos.places.PlacesResponseDto
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel(),
    isPreview: Boolean = false,
    onMapLayersMenuVisibilityChange: (Boolean) -> Unit = {}
) {
    val locationPermission = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val currentLocation by viewModel.currentLocation.collectAsState()
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentLocation, 15f)
    }
    val context = LocalContext.current
    var showMapLayersMenu by remember { mutableStateOf(false) }
    val selectedLayer by viewModel.selectedLayer.collectAsState()
    val selectedOption by viewModel.selectedOption.collectAsState()
    val hasToRequestPermission by viewModel.hasToRequestPermission.collectAsState()
    var isLocationVisible by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val errorMessage by viewModel.errorMessage.collectAsState()
    var showPermissionDialog by remember { mutableStateOf(false) }
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val selectedRoutePoint by viewModel.selectedRoutePoint.collectAsState()
    var showMapCreatePointRouteMenu by remember { mutableStateOf(false) }
    var showBottomActionButtons by remember { mutableStateOf(true) }
    var isSelectingPoint by remember { mutableStateOf(false) }
    var isPoint by remember { mutableStateOf(false) }
    var showMapNewPointMenu by remember { mutableStateOf(false) }
    var showMapNewPointNameMenu by remember { mutableStateOf(false) }
    var showMapNewPointTagMenu by remember { mutableStateOf(false) }
    var showMapNewPointTagServicesMenu by remember { mutableStateOf(false) }
    var showMapNewPointConfirmMenu by remember { mutableStateOf(false) }
    var showMapNewImageServiceMenu by remember { mutableStateOf(false) }
    var showMapNewLastDatesMenu by remember { mutableStateOf(false) }
    var isMapLoaded by remember { mutableStateOf(false) }
    var selectedPlace by remember { mutableStateOf<PlacesResponseDto?>(null) }
    val nearbyPlaces by viewModel.nearbyPlaces.collectAsState()
    var selectedFilterTypes by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var images by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var showMapNewImageServiceUploadMenu by remember { mutableStateOf(false) }
    var isFullScreenOpen by remember { mutableStateOf(false) }
    var showPlaceList by remember { mutableStateOf(false) }
    var lastSearchedPosition by remember { mutableStateOf(currentLocation) }
    var showSearchHereButton by remember { mutableStateOf(false) }
    val mapPoints = remember { mutableStateListOf<Pair<LatLng?, String?>>() }
    LaunchedEffect(Unit) {
        locationPermission.launchPermissionRequest()
    }

    LaunchedEffect(locationPermission.status) {
        if (locationPermission.status.isGranted && !isPreview) {
            viewModel.fetchUserLocation()
        }
    }

    LaunchedEffect(currentLocation) {
        cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(currentLocation, 12f), 1000)

    }

    LaunchedEffect(isMapLoaded, currentLocation) {
        if (isMapLoaded && currentLocation != LatLng(
                40.416775,
                -3.703790
            ) && nearbyPlaces.isEmpty()
        ) {
            viewModel.searchNearbyPlaces(radius = 5000, placeType = 5, latLng = currentLocation)
            lastSearchedPosition = currentLocation
            showSearchHereButton = false
            Log.d("HomeScreen", "Carga inicial en $currentLocation, botón ocultado")
        }
    }

    LaunchedEffect(cameraPositionState.position) {
        val currentMapCenter = cameraPositionState.position.target
        val distance = FloatArray(1)
        Location.distanceBetween(
            lastSearchedPosition.latitude, lastSearchedPosition.longitude,
            currentMapCenter.latitude, currentMapCenter.longitude,
            distance
        )
        showSearchHereButton = distance[0] > 500f
    }

    LaunchedEffect(nearbyPlaces) {
        if (nearbyPlaces.isNotEmpty()) {
            val boundsBuilder = LatLngBounds.Builder()
            nearbyPlaces.forEach { place ->
                place.toLatLng()?.let { boundsBuilder.include(it) }
            }
            boundsBuilder.include(currentLocation)
            val bounds = boundsBuilder.build()
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearErrorMessage()
        }
    }

    LaunchedEffect(hasToRequestPermission) {
        if (hasToRequestPermission && !locationPermission.status.isGranted) {
            if (locationPermission.status.shouldShowRationale) {
                locationPermission.launchPermissionRequest()
            } else {
                showPermissionDialog = true
            }
            viewModel.clearPermissionRequest()
        }
    }

    LaunchedEffect(
        showMapLayersMenu,
        showMapCreatePointRouteMenu,
        showMapNewPointMenu,
        showMapNewPointNameMenu,
        showMapNewPointTagMenu,
        showMapNewPointTagServicesMenu,
        showMapNewPointConfirmMenu,
        showMapNewImageServiceMenu,
        showMapNewImageServiceUploadMenu,
        showMapNewLastDatesMenu
    ) {
        val shouldHideNavigation = showMapLayersMenu ||
                showMapCreatePointRouteMenu ||
                showMapNewPointMenu ||
                showMapNewPointNameMenu ||
                showMapNewPointTagMenu ||
                showMapNewPointTagServicesMenu ||
                showMapNewPointConfirmMenu ||
                showMapNewImageServiceMenu ||
                showMapNewImageServiceUploadMenu ||
                showMapNewLastDatesMenu
        onMapLayersMenuVisibilityChange(shouldHideNavigation)
    }

    LaunchedEffect(viewModel.selectedPoint.collectAsState().value) {
        viewModel.selectedPoint.value?.let { latLng ->
            val address = viewModel.selectedAddress.value ?: "No address available"
        }
    }

    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Permisos necesarios") },
            text = { Text("Por favor, habilita los permisos de ubicación en la configuración de la app para continuar.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPermissionDialog = false
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showPermissionDialog = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {

        MapComponent(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            currentLocation = currentLocation,
            isLocationEnabled = locationPermission.status.isGranted,
            selectedLayer = selectedLayer,
            nearbyPlaces = nearbyPlaces,
            selectedFilterTypes = selectedFilterTypes,
            onLocationVisibilityChanged = { visible ->
                isLocationVisible = visible
            },
            isSelectingPoint = isSelectingPoint,
            isShowingRoutePoint = showMapNewPointMenu,
            onMapClick = {
                if (isSelectingPoint) {
                    val centerLatLng = cameraPositionState.position.target
                    viewModel.selectPoint(centerLatLng)
                    isSelectingPoint = false
                    showBottomActionButtons = true
                    showMapNewPointMenu = true
                }
            },

            onMapLoadedCallback = {
                isMapLoaded = true
            },
            onPlaceSelected = { place ->
                selectedPlace = place
            },
            onFullScreenChanged = { isFullScreenOpen = it },
            selectedOption = selectedOption,
            pointsList = mapPoints,
            viewModel = viewModel

            )
        if (!isFullScreenOpen) {
            LocationActionButtons(
                onMoveToLocation = {
                    scope.launch {
                        viewModel.fetchUserLocation()
                        delay(100)
                        cameraPositionState.animate(
                            CameraUpdateFactory.newLatLngZoom(
                                currentLocation,
                                15f
                            ), 1000
                        )
                    }
                },
                onMapLayerClick = { showMapLayersMenu = true },
                selectedOption = selectedOption,
                isLocationVisible = isLocationVisible,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 20.dp, top = 120.dp)
            )

            FilterMenu(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(y = 120.dp, x = 20.dp),
                onFiltersChanged = { filters ->
                    selectedFilterTypes = filters
                }
            )

            if (showSearchHereButton && !isSelectingPoint) {
                TopCenterButton(
                    onNavigateToResults = {
                        Log.d("HomeScreen", "Clic en TopCenterButton detectado")
                        val centerLatLng = cameraPositionState.position.target
                        try {
                            viewModel.searchNearbyPlaces(centerLatLng, 5000, 5)
                            lastSearchedPosition = centerLatLng
                            showSearchHereButton = false
                            Log.d(
                                "HomeScreen",
                                "Búsqueda realizada en $centerLatLng, botón ocultado"
                            )
                        } catch (e: Exception) {
                            Log.e("HomeScreen", "Error al buscar lugares: ${e.message}")
                        }
                    },
                    viewModel = viewModel,
                    cameraPositionState = cameraPositionState,
                    selectedFilterTypes = selectedFilterTypes,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 120.dp, start = 5.5.dp)
                )
            }

            val currentZoom = cameraPositionState.position.zoom

            if (isSelectingPoint && currentZoom < 16f) {
                TopCenterButtonSelectedPoint(
                    onNavigateToResults = {
                        val centerLatLng = cameraPositionState.position.target
                        try {
                            viewModel.searchNearbyPlaces(centerLatLng, 5000, 5)
                            lastSearchedPosition = centerLatLng
                            showSearchHereButton = false
                        } catch (e: Exception) {
                        }
                    },
                    viewModel = viewModel,
                    cameraPositionState = cameraPositionState,
                    selectedFilterTypes = selectedFilterTypes,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 120.dp, start = 5.5.dp)
                )

            }

            SearchBar(
                searchQuery = searchQuery,
                onSearchQueryChange = viewModel::updateSearchQuery,
                performSearch = viewModel::performSearch,
                searchResults = searchResults,
                onResultSelected = viewModel::selectSearchResult,
                modifier = Modifier
                    .align(Alignment.TopCenter)
            )
        }

        if (showPlaceList) {
            FullScreenPlaceList(
                places = nearbyPlaces,
                onDismiss = { showPlaceList = false },
                onPlaceSelected = { place ->
                    selectedPlace = place
                    showPlaceList = false
                }
            )
        }

        if (showBottomActionButtons && !isSelectingPoint && selectedPlace == null) {
            BottomActionButtons(
                isListOpen = showPlaceList,
                onToggleList = { showPlaceList = !showPlaceList },
                onAddAction = {
                    showBottomActionButtons = false
                    showMapCreatePointRouteMenu = true
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 101.dp)
            )
        } else if (selectedPlace == null) {
            BottomCoordButton(
                onNavigateToResults = { navController.navigate("results") },
                onAddAction = {
                    showBottomActionButtons = true
                    showMapCreatePointRouteMenu = false
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 300.dp)
            )
        }

        if (showMapLayersMenu) {

            MapLayersMenu(
                selectedLayer = selectedLayer,
                selectedOption = selectedOption,
                onLayerSelected = { layer -> viewModel.updateMapLayer(layer) },
                onOptionSelected = { option -> viewModel.updateMapOption(option) },
                onDismiss = { showMapLayersMenu = false }

            )
        }

        if (showMapCreatePointRouteMenu) {
            MapNewRoutePointMenu(
                selectedRoutePoint = selectedRoutePoint,
                onLayerSelected = { routePoint ->
                    viewModel.selectRoutePoint(routePoint)
                    if (routePoint == MapNewPointRoute.CREATE_POINT) {
                        println("DEBUG: Seleccionando CREATE_POINT")
                        showMapCreatePointRouteMenu = false
                        showMapNewPointMenu = true
                        isSelectingPoint = true
                        isPoint = true
                        println("DEBUG: isSelectingPoint = $isSelectingPoint")
                    } else {
                        println("DEBUG: Seleccionando CREATE_ROUTE")
                        showMapCreatePointRouteMenu = false
                        showMapNewPointMenu = true
                        isSelectingPoint = true
                        isPoint = false
                    }
                },
                onDismiss = {
                    showMapCreatePointRouteMenu = false
                    showBottomActionButtons = true
                }
            )
        }

        @Composable
        fun MapNewPointMenu(
            selectedPoint: LatLng?,
            selectedAddress: String?,
            isPoint: Boolean?,
            onDismiss: () -> Unit,
            onClearAndDismiss: () -> Unit,
            onConfirm: () -> Unit,
            onConfirmRoute: (List<Pair<LatLng?, String?>>) -> Unit,
            onSelectedPoint: () -> Unit,
            pointsList: MutableList<Pair<LatLng?, String?>>,
            viewModel: HomeViewModel
        ) {
            val scope = rememberCoroutineScope()
            val offsetY = remember { Animatable(600f) }
            val density = LocalDensity.current
            with(density) { 48.dp.toPx() }

            val context = LocalContext.current

            LaunchedEffect(Unit) {
                offsetY.animateTo(0f, animationSpec = tween(300))
            }

            Box(modifier = Modifier.fillMaxSize()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn()
                        .align(Alignment.BottomCenter)
                        .offset(y = offsetY.value.dp),
                    shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
                    color = Color.White,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, start = 20.dp, end = 20.dp, bottom = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = MainColor,
                                modifier = Modifier.size(32.dp),
                                shape = RoundedCornerShape(11.dp),
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ex),
                                        contentDescription = "Cerrar",
                                        modifier = Modifier
                                            .width(12.5.dp)
                                            .height(14.29.dp)
                                            .clickable {
                                                scope.launch {
                                                    offsetY.animateTo(600f, animationSpec = tween(300))
                                                    onDismiss()
                                                }
                                            },
                                        tint = Color.White
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (isPoint == true) "Localiza el nuevo punto" else "Localiza el primer punto de la ruta",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = BlackGray
                            )
                        }

                        if (selectedPoint != null && selectedAddress != null && selectedAddress != "Cargando dirección...") {
                            Box(
                                modifier = Modifier
                                    .background(BackgroundColorCard, shape = RoundedCornerShape(20.dp))
                                    .fillMaxWidth()
                                    .height(113.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(start = 20.dp, end = 20.dp)
                                        .fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        if (pointsList.isNotEmpty()) {
                                            Column(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                pointsList.forEachIndexed { index, point ->
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.Start,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Surface(
                                                            modifier = Modifier.size(20.dp),
                                                            color = BackgroundColorBadge,
                                                            shape = CircleShape
                                                        ) {
                                                            Box(
                                                                modifier = Modifier.fillMaxSize(),
                                                                contentAlignment = Alignment.Center
                                                            ) {
                                                                Text(
                                                                    text = (index + 1).toString(),
                                                                    fontSize = 10.sp,
                                                                    fontWeight = FontWeight.Normal,
                                                                    color = Color.White
                                                                )
                                                            }
                                                        }
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Column {
                                                            Text(
                                                                text = point.second ?: "Sin dirección",
                                                                fontSize = 10.sp,
                                                                fontWeight = FontWeight.Normal,
                                                                color = BlackGray
                                                            )
                                                            Spacer(modifier = Modifier.height(2.dp))
                                                            Text(
                                                                text = point.first?.let { "(${it.latitude}, ${it.longitude})" } ?: "Sin coordenadas",
                                                                fontSize = 10.sp,
                                                                fontWeight = FontWeight.Normal,
                                                                color = BlackGray
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        Column {
                                            Text(
                                                text = selectedAddress,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Normal,
                                                color = BlackGray
                                            )
                                            Spacer(modifier = Modifier.height(3.dp))
                                            Text(
                                                text = "(${selectedPoint.latitude}, ${selectedPoint.longitude})",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Normal,
                                                color = BlackGray
                                            )
                                        }
                                    }

                                    if (pointsList.isEmpty()) {
                                        Spacer(modifier = Modifier.height(18.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            Surface(
                                                modifier = Modifier
                                                    .width(32.dp)
                                                    .height(32.dp),
                                                shape = RoundedCornerShape(10.dp),
                                                color = Color.Transparent,
                                                border = BorderStroke(0.5.dp, BlackGray)
                                            ) {
                                                Column(
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    verticalArrangement = Arrangement.Center,
                                                ) {
                                                    Icon(
                                                        painter = painterResource(id = R.drawable.ex),
                                                        contentDescription = "Cerrar",
                                                        modifier = Modifier
                                                            .width(12.5.dp)
                                                            .height(14.29.dp)
                                                            .clickable {
                                                                scope.launch {
                                                                    onClearAndDismiss()
                                                                }
                                                            },
                                                        tint = BlackGray
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.width(8.dp))

                                            Surface(
                                                modifier = Modifier
                                                    .width(32.dp)
                                                    .height(32.dp),
                                                shape = RoundedCornerShape(10.dp),
                                                color = MainColor,
                                            ) {
                                                Column(
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                    verticalArrangement = Arrangement.Center,
                                                ) {
                                                    Icon(
                                                        painter = painterResource(id = R.drawable.check),
                                                        contentDescription = "Confirmar",
                                                        modifier = Modifier
                                                            .width(12.5.dp)
                                                            .height(14.29.dp)
                                                            .clickable {
                                                                if (isPoint == true) {
                                                                    scope.launch {
                                                                        offsetY.animateTo(600f, animationSpec = tween(300))
                                                                        onConfirm()
                                                                    }
                                                                } else {
                                                                    val newPoint = Pair(selectedPoint, selectedAddress)
                                                                    // Evitar duplicación
                                                                    if (pointsList.isEmpty() || pointsList.last() != newPoint) {
                                                                        pointsList.add(newPoint)
                                                                        Log.d("MapNewPointMenu", "Added point: ($selectedPoint, $selectedAddress), Points: $pointsList")
                                                                    }
                                                                }
                                                            },
                                                        tint = Color.White
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (isPoint == false && pointsList.isNotEmpty()) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .clickable {
                                        val newPoint = Pair(selectedPoint, selectedAddress)
                                        if (selectedPoint != null && selectedAddress != null && selectedAddress != "Cargando dirección..." && (pointsList.isEmpty() || pointsList.last() != newPoint)) {
                                            pointsList.add(newPoint)
                                            Log.d("MapNewPointMenu", "Added point: ($selectedPoint, $selectedAddress), Points: $pointsList")
                                        }
                                        onClearAndDismiss()
                                    },
                                shape = RoundedCornerShape(20.dp),
                                color = MainColor
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Añadir más puntos",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .clickable {
                                        scope.launch {

                                            viewModel.saveRouteAndClear(context, pointsList)
                                            onConfirmRoute(pointsList.toList())
                                            offsetY.animateTo(600f, animationSpec = tween(300))
                                        }
                                    },
                                shape = RoundedCornerShape(20.dp),
                                color = BackgroundButtonColor
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Guardar ruta",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showMapNewPointNameMenu) {
            MapNewPointNameMenu(
                selectedPoint = viewModel.selectedPoint.value,
                selectedAddress = viewModel.selectedAddress.value,
                onDismiss = {
                    showMapNewPointNameMenu = false
                    showBottomActionButtons = true
                    isSelectingPoint = false
                    viewModel.clearSelectedPoint()
                },
                onConfirm = {
                    showMapNewPointNameMenu = false
                    showMapNewPointConfirmMenu = true
                },
                onNameConfirmed = { name ->
                    viewModel.saveNewPoint(name)
                },
            )
        }

        if (showMapNewPointConfirmMenu) {
            MapNewPointConfirmMenu(
                selectedPoint = viewModel.selectedPoint.value,
                selectedAddress = viewModel.selectedAddress.value,
                selectedName = viewModel.selectedName.value,
                onDismiss = {
                    showMapNewPointConfirmMenu = false
                    showBottomActionButtons = true
                    isSelectingPoint = false
                    viewModel.clearSelectedPoint()
                },
                onConfirm = {
                    showMapNewPointTagMenu = true
                    showMapNewPointConfirmMenu = false
                }
            )
        }

        if (showMapNewPointTagMenu) {
            MapNewPointTagMenu(
                selectedPoint = viewModel.selectedPoint.value,
                onDismiss = {
                    showMapNewPointTagMenu = false
                    showBottomActionButtons = true
                    isSelectingPoint = false
                    viewModel.clearSelectedPoint()
                },
                selectedAddress = viewModel.selectedAddress.value,
                onNameConfirmed = viewModel.selectedAddress.value,
                onConfirm = {
                    showMapNewPointTagMenu = false
                    showMapNewPointTagServicesMenu = true
                }
            )
        }

        if (showMapNewPointTagServicesMenu) {
            MapNewPointTagServicesMenu(
                selectedPoint = viewModel.selectedPoint.value,
                onDismiss = {
                    showMapNewPointTagServicesMenu = false
                    showBottomActionButtons = true
                    isSelectingPoint = false
                    viewModel.clearSelectedPoint()
                },
                onConfirm = { selectedServices ->
                    showMapNewPointTagServicesMenu = false
                    showMapNewImageServiceMenu = true
                    viewModel.saveSelectedServices(selectedServices)
                },
                selectedAddress = viewModel.selectedAddress.value,
                onNameConfirmed = viewModel.selectedAddress.value
            )
        }

        if (showMapNewImageServiceMenu) {
            MapNewImageServiceMenu(
                selectedPoint = viewModel.selectedPoint.value,
                onDismiss = {
                    showMapNewImageServiceMenu = false
                    showBottomActionButtons = true
                    isSelectingPoint = false
                },
                selectedAddress = viewModel.selectedAddress.value,
                onNameConfirmed = viewModel.selectedAddress.value,
                onConfirm = {
                    showMapNewImageServiceMenu = false
                },
                onImagesSelected = { selectedImages ->
                    images = selectedImages
                    showMapNewImageServiceUploadMenu = true
                }

            )
        }

        if (showMapNewImageServiceUploadMenu) {
            MapNewImageServiceUploadMenu(
                selectedPoint = viewModel.selectedPoint.value,
                onDismiss = {
                    showMapNewImageServiceUploadMenu = false
                    showBottomActionButtons = true
                    isSelectingPoint = false
                },
                selectedAddress = viewModel.selectedAddress.value,
                onNameConfirmed = viewModel.selectedAddress.value,
                onConfirm = { updatedImages ->
                    images = emptyList()
                    showMapNewImageServiceMenu = false
                    showMapNewLastDatesMenu = true
                },
                initialImages = images,
                viewModel = viewModel

            )
        }

        if (showMapNewLastDatesMenu) {
            MapNewLastDatesMenu(
                selectedPoint = viewModel.selectedPoint.value,
                onDismiss = {
                    showMapNewLastDatesMenu = false
                    showBottomActionButtons = true
                    isSelectingPoint = false
                },
                selectedAddress = viewModel.selectedAddress.value,
                onNameConfirmed = {
                    showMapNewLastDatesMenu = false
                },
                viewModel = viewModel
            )
        }


    }

}