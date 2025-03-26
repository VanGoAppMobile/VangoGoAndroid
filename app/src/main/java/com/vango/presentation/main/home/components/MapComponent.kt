package com.vango.presentation.main.home.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.vango.R
import com.vango.presentation.theme.BackgroundButtonColor
import com.vango.presentation.theme.BackgroundColorButtonPrincipal
import com.vango.presentation.theme.BackgroundUnselected
import com.vango.presentation.theme.BlackGray
import com.vango.presentation.theme.MainColor
import com.vango.shared.dtos.places.PlacesResponseDto
import kotlinx.coroutines.launch

@Composable
fun BitmapDescriptorFactory.fromResource(drawableId: Int, context: Context): BitmapDescriptor {
    val drawable = ContextCompat.getDrawable(context, drawableId)
    val bitmap = Bitmap.createBitmap(
        drawable?.intrinsicWidth ?: 33,
        drawable?.intrinsicHeight ?: 40,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    drawable?.setBounds(0, 0, canvas.width, canvas.height)
    drawable?.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

@Composable
fun MapComponent(
    modifier: Modifier = Modifier,
    cameraPositionState: CameraPositionState,
    currentLocation: LatLng,
    isLocationEnabled: Boolean,
    selectedLayer: MapLayer = MapLayer.NORMAL,
    onLocationVisibilityChanged: (Boolean) -> Unit = {},
    onMapClick: (LatLng) -> Unit,
    isSelectingPoint: Boolean = false,
    isShowingRoutePoint: Boolean = false,
    onMapLoadedCallback: () -> Unit = {},
    nearbyPlaces: List<PlacesResponseDto>,
    selectedFilterTypes: Set<Int>,
    onPlaceSelected: (PlacesResponseDto?) -> Unit = {},
    onFullScreenChanged: (Boolean) -> Unit = {},
    selectedOption: MapOption?,
) {
    val context = LocalContext.current
    val mapStyleOptions = remember {
        MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style)
    }

    val mapProperties = remember(selectedLayer, selectedOption) {
        MapProperties(
            mapType = when (selectedLayer) {
                MapLayer.NORMAL -> MapType.NORMAL
                MapLayer.SATELLITE -> MapType.SATELLITE
                MapLayer.RELIEF -> MapType.TERRAIN
                MapLayer.NO_CONNECTION -> MapType.NONE
            },
            isMyLocationEnabled = isLocationEnabled,
            mapStyleOptions = mapStyleOptions,
            isTrafficEnabled = selectedOption == MapOption.TRAFFIC
        )
    }

    val mapUiSettings = remember {
        MapUiSettings(
            compassEnabled = true,
            zoomControlsEnabled = false
        )
    }

    val markerStatesMap = remember { mutableStateMapOf<String, MarkerState>() }
    var selectedPlace by remember { mutableStateOf<PlacesResponseDto?>(null) }

    var showFullScreen by remember { mutableStateOf(false) }

//    val filteredPlaces = remember(nearbyPlaces, selectedFilterTypes) {
//        val filtered = if (selectedFilterTypes.isEmpty()) {
//            nearbyPlaces
//        } else {
//            nearbyPlaces.filter { place ->
//                place.type in selectedFilterTypes
//            }
//        }
//        Log.d(
//            "MapComponent",
//            "NearbyPlaces: ${nearbyPlaces.size}, FilteredPlaces: ${filtered.size}, Filters: $selectedFilterTypes"
//        )
//        filtered.forEach { place ->
//            Log.d(
//                "MapComponent",
//                "Filtered Place: ${place.title}, Type: ${place.type}, PlaceId: ${place.placeId}"
//            )
//        }
//        filtered
//    }

    val filteredPlaces = remember(nearbyPlaces, selectedFilterTypes) {
        val filtered = if (selectedFilterTypes.isEmpty()) {
            nearbyPlaces
        } else {
            nearbyPlaces.filter { place ->
                place.type in selectedFilterTypes || place.type == null
            }
        }
        Log.d(
            "MapComponent",
            "NearbyPlaces: ${nearbyPlaces.size}, FilteredPlaces: ${filtered.size}, Filters: $selectedFilterTypes"
        )
        filtered.forEach { place ->
            Log.d(
                "MapComponent",
                "Filtered Place: ${place.title}, Type: ${place.type}, Location: ${place.location}"
            )
        }
        filtered
    }

    LaunchedEffect(filteredPlaces) {
        filteredPlaces.forEach { place ->
            place.toLatLng()?.let { latLng ->
                Log.d("MapComponent", "Adding marker for ${place.title} at $latLng")
            } ?: Log.w("MapComponent", "toLatLng() returned null for ${place.title}")
        }
    }

    LaunchedEffect(filteredPlaces) {
        val currentPlaceIds = filteredPlaces.map { it.placeId }.toSet()
        markerStatesMap.keys.retainAll(currentPlaceIds)

        filteredPlaces.forEach { place ->
            place.placeId?.let { placeId ->
                if (!markerStatesMap.containsKey(placeId)) {
                    place.toLatLng()?.let { latLng ->
                        Log.d("MapComponent", "Adding marker for ${place.title} at $latLng")
                        markerStatesMap[placeId] = MarkerState(position = latLng)
                    } ?: Log.w("MapComponent", "toLatLng() returned null for ${place.title}")
                }
            }
        }
    }

    LaunchedEffect(showFullScreen) {
        onFullScreenChanged(showFullScreen)
    }


    Box(modifier = modifier.fillMaxSize()) {
        GoogleMap(
            modifier = modifier,
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            uiSettings = mapUiSettings,
            onMapLoaded = {
                val bounds = cameraPositionState.projection?.visibleRegion?.latLngBounds
                bounds?.let {
                    onLocationVisibilityChanged(it.contains(currentLocation))
                }
                onMapLoadedCallback()
            },
            onMapClick = {
                onMapClick(it)
                selectedPlace = null
                onPlaceSelected(null)
            }
        ) {
            if (isLocationEnabled && currentLocation.latitude != 0.0 && currentLocation.longitude != 0.0) {

                if (!isSelectingPoint && !isShowingRoutePoint) {
                    Marker(
                        state = MarkerState(position = currentLocation),
                        title = "Ubicación actual"
                    )
                }
            }

//            filteredPlaces.forEach { place ->
//                place.placeId?.let { placeId ->
//                    val markerState = remember(placeId) {
//                        place.toLatLng()?.let { MarkerState(position = it) }
//                            ?: run {
//                                Log.w("MapComponent", "toLatLng() returned null for ${place.title}")
//                                null
//                            }
//                    }
//                    markerState?.let {
//                        Marker(
//                            state = it,
//                            title = place.title,
//                            snippet = place.address,
//                            icon = when (place.type) {
//                                0 -> BitmapDescriptorFactory.fromResource(R.drawable.marker_camping)
//                                1 -> BitmapDescriptorFactory.fromResource(R.drawable.marker_parking)
//                                2 -> BitmapDescriptorFactory.fromResource(R.drawable.marker_hospital)
//                                3 -> BitmapDescriptorFactory.fromResource(R.drawable.marker_gas_station)
//                                4 -> BitmapDescriptorFactory.fromResource(R.drawable.marker_laundry)
//                                else -> null
//                            },
//                            onClick = {
//                                selectedPlace = place
//                                onPlaceSelected(place)
//                                true
//                            }
//                        )
//                    }
//                }
//            }

            filteredPlaces.forEach { place ->
                place.toLatLng()?.let { latLng ->
                    Marker(
                        state = MarkerState(position = latLng),
                        title = place.title,
                        snippet = place.address,
                        icon = when (place.type) {
                            0 -> BitmapDescriptorFactory.fromResource(R.drawable.marker_camping)
                            1 -> BitmapDescriptorFactory.fromResource(R.drawable.marker_parking)
                            2 -> BitmapDescriptorFactory.fromResource(R.drawable.marker_hospital)
                            3 -> BitmapDescriptorFactory.fromResource(R.drawable.marker_gas_station)
                            4 -> BitmapDescriptorFactory.fromResource(R.drawable.marker_laundry)
                            else -> BitmapDescriptorFactory.defaultMarker()
                        },
                        onClick = {
                            selectedPlace = place
                            onPlaceSelected(place)
                            true
                        }
                    )
                } ?: Log.w(
                    "MapComponent",
                    "No se pudo crear marcador para ${place.title}, location inválido: ${place.location}"
                )
            }


            if (isSelectingPoint) {
                Marker(
                    state = MarkerState(position = cameraPositionState.position.target),
                    title = "Punto a seleccionar",
                    snippet = "Toca el mapa para confirmar",
                    icon = BitmapDescriptorFactory.fromResource(R.drawable.marker_plus)
                )
            }
            if (isShowingRoutePoint && !isSelectingPoint) {
                Marker(
                    state = MarkerState(position = cameraPositionState.position.target),
                    title = "Punto a seleccionar",
                    snippet = "Toca el mapa para confirmar",
                )
            }

//            if (selectedOption == MapOption.PUBLIC_TRANSPORT) {
//                Marker(
//                    state = MarkerState(position = currentLocation),
//                    title = "Parada de bus cercana",
//                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
//                )
//            }
        }

        selectedPlace?.let { place ->
            Log.d("MapComponent", "SelectedPlace antes de PlaceCard: $place")
            PlaceCard(
                place = place,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 100.dp)
                    .fillMaxWidth()
                    .clickable {
                        showFullScreen = true
                    }
            )
        }
    }

    if (showFullScreen && selectedPlace != null) {
        FullScreenPlaceCard(
            place = selectedPlace!!,
            onDismiss = { showFullScreen = false }
        )
    }
    LaunchedEffect(cameraPositionState.position) {
        val bounds = cameraPositionState.projection?.visibleRegion?.latLngBounds
        bounds?.let {
            onLocationVisibilityChanged(it.contains(currentLocation))
        }
    }
}

enum class PlaceType(val value: Int) {
    CAMPING(0),
    PARKING(1),
    HOSPITAL(2),
    GASOLINERA(3),
    LAVANDERIA(4);

    companion object {
        fun fromValue(value: Int): String =
            values().find { it.value == value }?.name?.lowercase() ?: "desconocido"
    }
}

data class Place(val type: Int)

@Composable
fun PlaceCard(
    place: PlacesResponseDto,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(329.dp)
            .heightIn()
            .padding(start = 20.dp, end = 20.dp),

        shape = RoundedCornerShape(15.dp)
    ) {
        Column(
            modifier = Modifier
                .background(Color.White)
        ) {

            val photoUrls = place.photoUrls ?: listOf(R.drawable.noimage)
            val pagerState = rememberPagerState(pageCount = { photoUrls.size })
            Log.d("PlaceCard", "Photo URLs for ${place.title}: $photoUrls")

            Box {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(162.dp)
                ) { page ->
                    Log.d("PlaceCard", "Cargando imagen: ${photoUrls[page]}")
                    AsyncImage(
                        model = photoUrls[page],
                        contentDescription = "Imagen ${page + 1} de ${place.title}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(229.dp),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.noimage),
                        error = painterResource(R.drawable.noimage)
                    )
                }

                Row(
                    modifier = Modifier
                        .padding(top = 10.dp, start = 16.dp, end = 16.dp)
                        .height(17.5.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        modifier = Modifier
                            .height(17.5.dp)
                            .wrapContentWidth(),
                        color = Color.White.copy(alpha = 0.54f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "#${place.type?.let { PlaceType.fromValue(it) } ?: "desconocido"}",
                                fontSize = 8.sp,
                                textAlign = TextAlign.Center,
                                color = BackgroundUnselected,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }

                    Icon(
                        painter = painterResource(id = R.drawable.heart),
                        tint = BlackGray,
                        contentDescription = "favorite"
                    )
                }

                if (photoUrls.size > 1) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(photoUrls.size) { index ->
                            val isSelected = pagerState.currentPage == index
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .padding(2.dp)
                                    .background(
                                        color = if (isSelected) Color.White else Color.Gray.copy(
                                            alpha = 0.5f
                                        ),
                                        shape = CircleShape
                                    )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 50.dp)
                ) {
                    Text(
                        text = place.title ?: "Sin título",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = place.address ?: "Sin dirección",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = BlackGray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                }
                Column(
                    horizontalAlignment = Alignment.End

                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.start),
                            contentDescription = "Rating",
                            tint = MainColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = "${place.rating ?: 0.0}",
                            fontSize = 16.sp,
                            textAlign = TextAlign.End,
                            color = BlackGray
                        )
                    }

                    Row(
                        modifier = Modifier.width(70.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "${place.userVotes ?: 0} votos",
                            fontSize = 9.7.sp,
                            textAlign = TextAlign.End,
                            color = Color.Gray,
                        )
                    }


                }


            }

//            if(place.type != PlaceType.CAMPING ))

            Row(
                modifier = Modifier.padding(start = 10.dp, top = 0.dp)
            ) {
                Text(
                    text = "Entrada gratuita",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = BackgroundButtonColor

                )
            }

            Spacer(modifier = Modifier.height(16.dp))


        }
    }
}


@Composable
fun PlaceCardList(
    place: PlacesResponseDto,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(329.dp)
            .heightIn()
            .padding(start = 20.dp, end = 20.dp),

        shape = RoundedCornerShape(15.dp)
    ) {
        Column(
            modifier = Modifier
                .background(Color.White)
        ) {

            val photoUrls = place.photoUrls ?: listOf(R.drawable.noimage)
            val pagerState = rememberPagerState(pageCount = { photoUrls.size })

            Box {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.3f)
                        .clip(RoundedCornerShape(15.dp))
                ) { page ->
                    Log.d("PlaceCard", "Cargando imagen: ${photoUrls[page]}")
                    AsyncImage(
                        model = photoUrls[page],
                        contentDescription = "Imagen ${page + 1} de ${place.title}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1.3f),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.noimage),
                        error = painterResource(R.drawable.noimage)
                    )
                }

                Row(
                    modifier = Modifier
                        .padding(top = 10.dp, start = 16.dp, end = 16.dp)
                        .height(17.5.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        modifier = Modifier
                            .height(17.5.dp)
                            .wrapContentWidth(),
                        color = Color.White.copy(alpha = 0.54f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "#${place.type?.let { PlaceType.fromValue(it) } ?: "desconocido"}",
                                fontSize = 8.sp,
                                textAlign = TextAlign.Center,
                                color = BackgroundUnselected,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                        }
                    }

                    Icon(
                        painter = painterResource(id = R.drawable.heart),
                        tint = Color.Unspecified,
                        contentDescription = "favorite",
                        modifier = Modifier.width(19.65.dp).height(17.75.dp)
                    )
                }

                if (photoUrls.size > 1) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(photoUrls.size) { index ->
                            val isSelected = pagerState.currentPage == index
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .padding(2.dp)
                                    .background(
                                        color = if (isSelected) Color.White else Color.Gray.copy(
                                            alpha = 0.5f
                                        ),
                                        shape = CircleShape
                                    )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),

                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 50.dp)
                ) {
                    Text(
                        text = place.title ?: "Sin título",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = BlackGray
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = place.address ?: "Sin dirección",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = BlackGray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                }
                Column(
                    horizontalAlignment = Alignment.End

                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        Icon(
                            modifier = Modifier.size(width = 15.dp, height = 14.29.dp),
                            painter = painterResource(id = R.drawable.start),
                            contentDescription = "Rating",
                            tint = MainColor

                        )
                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = "${place.rating ?: 0.0}",
                            fontSize = 14.sp,
                            textAlign = TextAlign.End,
                            fontWeight = FontWeight.Bold,
                            color = BlackGray
                        )
                    }

                    Row(
                        modifier = Modifier.width(70.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "${place.userVotes ?: 0} votos",
                            fontSize = 9.7.sp,
                            textAlign = TextAlign.End,
                            color = Color.Gray,
                        )
                    }


                }


            }


//            Row(
//                modifier = Modifier.padding(top = 16.dp)
//            ) {
//                Text(
//                    text = "Entrada gratuita",
//                    fontSize = 12.sp,
//                    fontWeight = FontWeight.Bold,
//                    color = BackgroundButtonColor
//
//                )
//            }

            Spacer(modifier = Modifier.height(16.dp))


        }
    }
}

@Composable
fun FullScreenPlaceCard(
    place: PlacesResponseDto,
    onDismiss: () -> Unit
) {
    val offsetY = remember { Animatable(600f) }
    val scope = rememberCoroutineScope()
    var showFullMap by remember { mutableStateOf(false) }
    val miniMapCameraPositionState = rememberCameraPositionState {
        place.toLatLng()?.let { latLng ->
            position = CameraPosition.fromLatLngZoom(latLng, 15f)
        }
    }

    val fullMapCameraPositionState = rememberCameraPositionState {
        place.toLatLng()?.let { latLng ->
            position = CameraPosition.fromLatLngZoom(latLng, 15f)
        }
    }
    val scrollState = rememberScrollState()
    LaunchedEffect(Unit) {
        offsetY.animateTo(0f, animationSpec = tween(300))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BlackGray.copy(alpha = 0.5f))
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .align(Alignment.BottomCenter)
                .offset(y = offsetY.value.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    place.photoUrls?.firstOrNull()?.let { photoUrl ->
                        AsyncImage(
                            model = photoUrl,
                            contentDescription = "Imagen de ${place.title}",
                            modifier = Modifier
                                .fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } ?: AsyncImage(
                        model = R.drawable.noimage,
                        contentDescription = "Imagen por defecto",
                        modifier = Modifier
                            .fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, end = 20.dp, top = 55.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
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
                        Icon(
                            painter = painterResource(id = R.drawable.heart),
                            contentDescription = "Cerrar",
                            modifier = Modifier
                                .size(32.dp)
                                .clickable {
                                    scope.launch {
                                        offsetY.animateTo(600f, animationSpec = tween(300))
                                        onDismiss()
                                    }
                                }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(41.86.dp),
                        border = BorderStroke(0.5.dp, BlackGray),
                        shape = RoundedCornerShape(15.dp),
                        color = Color.White
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.water),
                                tint = BlackGray,
                                modifier = Modifier
                                    .width(25.dp)
                                    .height(25.dp)
                                    .clickable {
                                        scope.launch {
                                            offsetY.animateTo(600f, animationSpec = tween(300))
                                            onDismiss()
                                        }
                                    },

                                contentDescription = "favorite"
                            )
                        }


                    }

                    Surface(
                        modifier = Modifier.size(41.86.dp),
                        border = BorderStroke(0.5.dp, BlackGray),
                        shape = RoundedCornerShape(15.dp),
                        color = Color.White
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.electricity),
                                tint = BlackGray,
                                modifier = Modifier
                                    .width(25.dp)
                                    .height(25.dp)
                                    .clickable {
                                        scope.launch {
                                            offsetY.animateTo(600f, animationSpec = tween(300))
                                            onDismiss()
                                        }
                                    },

                                contentDescription = "favorite"
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier.size(41.86.dp),
                        border = BorderStroke(0.5.dp, BlackGray),
                        shape = RoundedCornerShape(15.dp),
                        color = Color.White
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.shower),
                                tint = BlackGray,
                                modifier = Modifier
                                    .width(25.dp)
                                    .height(25.dp)
                                    .clickable {
                                        scope.launch {
                                            offsetY.animateTo(600f, animationSpec = tween(300))
                                            onDismiss()
                                        }
                                    },

                                contentDescription = "favorite"
                            )
                        }
                    }
                    Surface(
                        modifier = Modifier.size(41.86.dp),
                        border = BorderStroke(0.5.dp, BlackGray),
                        shape = RoundedCornerShape(15.dp),
                        color = Color.White
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.public_wc),
                                tint = BlackGray,
                                modifier = Modifier
                                    .width(25.dp)
                                    .height(25.dp)
                                    .clickable {
                                        scope.launch {
                                            offsetY.animateTo(600f, animationSpec = tween(300))
                                            onDismiss()
                                        }
                                    },

                                contentDescription = "favorite"
                            )
                        }
                    }
                    Surface(
                        modifier = Modifier.size(41.86.dp),
                        border = BorderStroke(0.5.dp, BlackGray),
                        shape = RoundedCornerShape(15.dp),
                        color = Color.White
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.pool),
                                tint = BlackGray,
                                modifier = Modifier
                                    .width(25.dp)
                                    .height(25.dp)
                                    .clickable {
                                        scope.launch {
                                            offsetY.animateTo(600f, animationSpec = tween(300))
                                            onDismiss()
                                        }
                                    },

                                contentDescription = "favorite"
                            )
                        }
                    }
                    Surface(
                        modifier = Modifier.size(41.86.dp),
                        border = BorderStroke(0.5.dp, BlackGray),
                        shape = RoundedCornerShape(15.dp),
                        color = Color.White
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.high_coberture),
                                tint = BlackGray,
                                modifier = Modifier
                                    .width(25.dp)
                                    .height(25.dp)
                                    .clickable {
                                        scope.launch {
                                            offsetY.animateTo(600f, animationSpec = tween(300))
                                            onDismiss()
                                        }
                                    },

                                contentDescription = "favorite"
                            )
                        }
                    }
                    Surface(
                        modifier = Modifier.size(41.86.dp),
                        border = BorderStroke(0.5.dp, BlackGray),
                        shape = RoundedCornerShape(15.dp),
                        color = Color.White
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.long_stance),
                                tint = BlackGray,
                                modifier = Modifier
                                    .width(25.dp)
                                    .height(25.dp)
                                    .clickable {
                                        scope.launch {
                                            offsetY.animateTo(600f, animationSpec = tween(300))
                                            onDismiss()
                                        }
                                    },

                                contentDescription = "favorite"
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 50.dp)
                    ) {
                        Text(
                            text = place.title ?: "Sin título",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = place.address ?: "Sin dirección",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = BlackGray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                    }
                    Column(
                        verticalArrangement = Arrangement.Top
                    ) {
                        Text(
                            text = "${place.rating ?: 0.0} ★",
                            fontSize = 21.sp,
                            color = BlackGray
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${place.userVotes ?: 0} votos",
                            fontSize = 9.7.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))




                place.toLatLng()?.let { latLng ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(195.dp)
                    ) {
                        GoogleMap(
                            modifier = Modifier.fillMaxSize(),
                            cameraPositionState = miniMapCameraPositionState,
                            properties = MapProperties(
                                isMyLocationEnabled = false,
                                mapStyleOptions = MapStyleOptions.loadRawResourceStyle(
                                    LocalContext.current,
                                    R.raw.map_style
                                )
                            ),
                            uiSettings = MapUiSettings(
                                zoomControlsEnabled = false,
                                compassEnabled = false,
                                myLocationButtonEnabled = false,
                                scrollGesturesEnabled = false,
                                zoomGesturesEnabled = false,
                                tiltGesturesEnabled = false,
//                                rotateGesturesEnabled = false
                            )
                        ) {
                            Marker(
                                state = MarkerState(position = latLng),
                                title = place.title,
                                icon = when (place.type) {
                                    0 -> BitmapDescriptorFactory.fromResource(R.drawable.marker_camping)
                                    1 -> BitmapDescriptorFactory.fromResource(R.drawable.marker_parking)
                                    2 -> BitmapDescriptorFactory.fromResource(R.drawable.marker_hospital)
                                    3 -> BitmapDescriptorFactory.fromResource(R.drawable.marker_gas_station)
                                    4 -> BitmapDescriptorFactory.fromResource(R.drawable.marker_laundry)
                                    else -> null
                                }
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) { showFullMap = true }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row() {
                            Icon(
                                painter = painterResource(id = R.drawable.location_no_fill),
                                tint = BlackGray,
                                modifier = Modifier
                                    .width(18.dp)
                                    .height(18.dp)
                                    .clickable {
                                        scope.launch {
                                            offsetY.animateTo(600f, animationSpec = tween(300))
                                            onDismiss()
                                        }
                                    },

                                contentDescription = "favorite"
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            place.address?.let {
                                Text(
                                    text = it,
                                    overflow = TextOverflow.Ellipsis

                                )
                            }
                        }

                        HorizontalDivider(color = Color.LightGray)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.clock),
                                    tint = BlackGray,
                                    modifier = Modifier
                                        .width(18.dp)
                                        .height(18.dp)
                                        .clickable {
                                            scope.launch {
                                                offsetY.animateTo(600f, animationSpec = tween(300))
                                                onDismiss()
                                            }
                                        },
                                    contentDescription = "favorite"
                                )
                                Spacer(modifier = Modifier.width(16.dp))

                                Text(
                                    text = "Lunes a Domingo\n" +
                                            "9am a 9pm",
                                    fontSize = 12.sp,
                                    color = BlackGray,
                                    overflow = TextOverflow.Ellipsis

                                )

                            }

                            Spacer(modifier = Modifier.weight(1f))

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.calendar),
                                    tint = BlackGray,
                                    modifier = Modifier
                                        .width(18.dp)
                                        .height(18.dp),
                                    contentDescription = "favorite"
                                )
                                Spacer(modifier = Modifier.width(16.dp))

                                Text(
                                    text = "Abierto todo el año",
                                    fontSize = 12.sp,
                                    color = BlackGray,
                                    overflow = TextOverflow.Ellipsis

                                )

                            }

                            Spacer(modifier = Modifier.weight(1f))
                        }

                        HorizontalDivider(color = Color.LightGray)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.phone),
                                    tint = BlackGray,
                                    modifier = Modifier
                                        .width(18.dp)
                                        .height(18.dp)
                                        .clickable {
                                            scope.launch {
                                                offsetY.animateTo(600f, animationSpec = tween(300))
                                                onDismiss()
                                            }
                                        },
                                    contentDescription = "favorite"
                                )
                                Spacer(modifier = Modifier.width(16.dp))

                                Text(
                                    text = "+34 658 587 254",
                                    fontSize = 12.sp,
                                    color = BlackGray,
                                    overflow = TextOverflow.Ellipsis

                                )

                            }

                            Spacer(modifier = Modifier.weight(1f))

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.arroba),
                                    tint = BlackGray,
                                    modifier = Modifier
                                        .width(18.dp)
                                        .height(18.dp)
                                        .clickable {
                                            scope.launch {
                                                offsetY.animateTo(600f, animationSpec = tween(300))
                                                onDismiss()
                                            }
                                        },
                                    contentDescription = "favorite"
                                )
                                Spacer(modifier = Modifier.width(16.dp))

                                Text(
                                    text = "info@jabaliblanco.es",
                                    fontSize = 12.sp,
                                    color = BlackGray,
                                    overflow = TextOverflow.Ellipsis

                                )

                            }

                            Spacer(modifier = Modifier.weight(1f))
                        }

                        HorizontalDivider(color = Color.LightGray)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.www),
                                    tint = BlackGray,
                                    modifier = Modifier
                                        .width(18.dp)
                                        .height(18.dp)
                                        .clickable {
                                            scope.launch {
                                                offsetY.animateTo(600f, animationSpec = tween(300))
                                                onDismiss()
                                            }
                                        },
                                    contentDescription = "favorite"
                                )
                                Spacer(modifier = Modifier.width(16.dp))

                                Text(
                                    text = "www.jabaliblanco.es",
                                    fontSize = 12.sp,
                                    color = BlackGray,
                                    overflow = TextOverflow.Ellipsis

                                )

                            }


                        }


                    }

                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, bottom = 150.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Surface(
                        modifier = Modifier
                            .width(156.dp)
                            .height(40.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = MainColor
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Navegador",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                overflow = TextOverflow.Ellipsis

                            )
                        }

                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Surface(
                        modifier = Modifier
                            .width(156.dp)
                            .height(40.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = BackgroundColorButtonPrincipal
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Reservar",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                overflow = TextOverflow.Ellipsis


                            )
                        }

                    }
                }


            }

        }

        if (showFullMap) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BlackGray.copy(alpha = 0.7f))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { showFullMap = false }
            ) {
                GoogleMap(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.9f)
                        .align(Alignment.Center)
                        .background(Color.White)
                        .clip(RoundedCornerShape(15.dp)),
                    cameraPositionState = fullMapCameraPositionState,
                    properties = MapProperties(
                        isMyLocationEnabled = false,
                        mapStyleOptions = MapStyleOptions.loadRawResourceStyle(
                            LocalContext.current,
                            R.raw.map_style
                        )
                    ),
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = true,
                        compassEnabled = true,
                        myLocationButtonEnabled = false
                    )
                ) {
                    Marker(
                        state = MarkerState(position = place.toLatLng()!!),
                        title = place.title,
                        icon = when (place.type) {
                            0 -> BitmapDescriptorFactory.fromResource(R.drawable.marker_camping)
                            1 -> BitmapDescriptorFactory.fromResource(R.drawable.marker_parking)
                            2 -> BitmapDescriptorFactory.fromResource(R.drawable.marker_hospital)
                            3 -> BitmapDescriptorFactory.fromResource(R.drawable.marker_gas_station)
                            4 -> BitmapDescriptorFactory.fromResource(R.drawable.marker_laundry)
                            else -> null
                        }
                    )
                }

                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 55.dp, start = 20.dp)
                        .size(32.dp),
                    shape = RoundedCornerShape(11.dp),
                    color = MainColor,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_back),
                        contentDescription = "Volver atrás",
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable { showFullMap = false },
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlaceCardPreview() {
    val samplePlace = PlacesResponseDto(
        title = "Parking de La Tella Parking de La Tella Parking de La Tella",
        address = "HU-631",
        location = "42.5624343,0.0425323",
        rating = 4.3f,
        userVotes = 108,
        photoUrls = listOf("https://gratisography.com/wp-content/uploads/2025/02/gratisography-when-pigs-fly-1170x780.jpg"),
        type = 1,
        placeId = "ChIJc-IaFVwBqBIRl6QdE2o8gu8",
        currentOpeningHours = null,
        opening_hours = null,
        international_phone_number = null,
        formattedPhoneNumber = null,
        website = null,
        reviews = null,
        source = 1
    )

    PlaceCardList(
        place = samplePlace,
        modifier = Modifier
            .fillMaxWidth()
    )
}


@Composable
fun FullScreenPlaceList(
    places: List<PlacesResponseDto>,
    onDismiss: () -> Unit,
    onPlaceSelected: (PlacesResponseDto) -> Unit = {}
) {
    val offsetY = remember { Animatable(600f) }
    val scope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    val searchResults = remember { mutableStateListOf<SearchResult>() }
    var selectedFilter by remember { mutableStateOf("All") }

    val filterTypeMap = mapOf(
        "All" to null,
        "Camping" to 0,
        "Parking" to 1,
        "Hospital" to 2,
        "Gasolinera" to 3,
        "Lavanderia" to 4
    )

    LaunchedEffect(Unit) {
        offsetY.animateTo(0f, animationSpec = tween(300))
    }

    val filteredPlaces = when (selectedFilter) {
        "All" -> places
        else -> places.filter { place ->
            place.type == filterTypeMap[selectedFilter]
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BlackGray.copy(alpha = 0.5f))
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .align(Alignment.BottomCenter)
                .offset(y = offsetY.value.dp),
            color = Color.White,
            shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 16.dp, top = 55.dp)
            ) {
                item {
                    SearchBarHomeList(
                        searchQuery = searchQuery,
                        onSearchQueryChange = { newQuery ->
                            searchQuery = newQuery
                            if (newQuery.isNotEmpty()) {
                                searchResults.clear()
                                searchResults.addAll(
                                    filteredPlaces.map { place ->
                                        SearchResult(
                                            name = place.title ?: "",
                                            secondaryText = place.address,
                                            distanceMeters = 0f
                                        )
                                    }.filter { it.name.contains(newQuery, ignoreCase = true) }
                                )
                            } else {
                                searchResults.clear()
                            }
                        },
                        performSearch = { query ->
                            searchResults.clear()
                            searchResults.addAll(
                                filteredPlaces.map { place ->
                                    SearchResult(
                                        name = place.title ?: "",
                                        secondaryText = place.address,
                                        distanceMeters = 0f
                                    )
                                }.filter { it.name.contains(query, ignoreCase = true) }
                            )
                        },
                        searchResults = searchResults,
                        onResultSelected = { result ->
                            val selectedPlace = filteredPlaces.find { it.title == result.name }
                            selectedPlace?.let { onPlaceSelected(it) }
                            searchQuery = result.name
                        },
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp, bottom = 20.dp, start = 20.dp, end = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(15.dp)
                    ) {
                        Surface(
                            color = if (selectedFilter == "All") BackgroundButtonColor else Color.White,
                            modifier = Modifier.size(60.dp),
                            shape = RoundedCornerShape(11.dp),
                            border = if (selectedFilter == "All") BorderStroke(1.dp, MainColor)  else BorderStroke(1.dp, Color.LightGray)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {

                                Icon(
                                    painter = painterResource(id = R.drawable.all_btn),
                                    contentDescription = "Todos",
                                    modifier = Modifier
                                        .width(33.dp)
                                        .height(33.dp)
                                        .clickable {
                                            selectedFilter = "All"
                                        },
                                    tint = if(selectedFilter == "All") Color.White else BlackGray
                                )
                                Text(
                                    text = "Todos",
                                    fontSize = 10.sp,
                                    color = if(selectedFilter == "All") Color.White else BlackGray
                                )
                            }
                        }

                        Surface(
                            color = if (selectedFilter == "Camping") BackgroundButtonColor else Color.White,
                            modifier = Modifier.size(60.dp),
                            shape = RoundedCornerShape(11.dp),
                            border = if (selectedFilter == "Camping") BorderStroke(1.dp, MainColor) else BorderStroke(1.dp, Color.LightGray)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.camper_no_fill),
                                    contentDescription = "Camping",
                                    modifier = Modifier
                                        .width(33.dp)
                                        .height(33.dp)
                                        .clickable {
                                            selectedFilter = "Camping"
                                        },
                                    tint = if(selectedFilter == "Camping") Color.White else BlackGray

                                )
                                Text(
                                    text = "Camping",
                                    fontSize = 10.sp,
                                    color = if(selectedFilter == "Camping") Color.White else BlackGray
                                )
                            }
                        }

                        Surface(
                            color = if (selectedFilter == "Parking") BackgroundButtonColor else Color.White,
                            modifier = Modifier.size(60.dp),
                            shape = RoundedCornerShape(11.dp),
                            border = if (selectedFilter == "Parking") BorderStroke(1.dp, MainColor) else BorderStroke(1.dp, Color.LightGray)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.parking_no_fill),
                                    contentDescription = "Parking",
                                    modifier = Modifier
                                        .width(33.dp)
                                        .height(33.dp)
                                        .clickable {
                                            selectedFilter = "Parking"
                                        },
                                    tint = if(selectedFilter == "Parking") Color.White else BlackGray
                                )
                                Text(
                                    text = "Parking",
                                    fontSize = 10.sp,
                                    color = if(selectedFilter == "Parking") Color.White else BlackGray
                                )

                            }
                        }

                        Surface(
                            color = if (selectedFilter == "Lavandería") BackgroundButtonColor else Color.White,
                            modifier = Modifier.size(60.dp),
                            shape = RoundedCornerShape(11.dp),
                            border = if (selectedFilter == "Lavandería") BorderStroke(1.dp, MainColor) else BorderStroke(1.dp, Color.LightGray)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {

                                Icon(
                                    painter = painterResource(id = R.drawable.laundry_no_fill),
                                    contentDescription = "Lavandería",
                                    modifier = Modifier
                                        .width(33.dp)
                                        .height(33.dp)
                                        .clickable {
                                            selectedFilter = "Lavandería"
                                        },
                                    tint = if(selectedFilter == "Lavandería") Color.White else BlackGray
                                )
                                Text(
                                    text = "Lavandería",
                                    fontSize = 10.sp,
                                    color = if(selectedFilter == "Lavandería") Color.White else BlackGray
                                )
                            }
                        }

                        Surface(
                            color = if (selectedFilter == "Gasolinera") BackgroundButtonColor else Color.White,
                            modifier = Modifier.size(60.dp),
                            shape = RoundedCornerShape(11.dp),
                            border = if (selectedFilter == "Gasolinera") BorderStroke(1.dp, MainColor) else BorderStroke(1.dp, Color.LightGray)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {

                                Icon(
                                    painter = painterResource(id = R.drawable.fuel_station_no_fill),
                                    contentDescription = "Gasolinera",
                                    modifier = Modifier
                                        .width(33.dp)
                                        .height(33.dp)
                                        .clickable {
                                            selectedFilter = "Gasolinera"
                                        },
                                    tint = if(selectedFilter == "Gasolinera") Color.White else BlackGray
                                )

                                Text(
                                    text = "Gasolinera",
                                    fontSize = 10.sp,
                                    color = if(selectedFilter == "Gasolinera") Color.White else BlackGray
                                )
                            }
                        }

                        Surface(
                            color = if (selectedFilter == "Hospital") BackgroundButtonColor else Color.White,
                            modifier = Modifier.size(60.dp),
                            shape = RoundedCornerShape(11.dp),
                            border = if (selectedFilter == "Hospital") BorderStroke(1.dp, MainColor) else BorderStroke(1.dp, Color.LightGray)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {

                                Icon(
                                    painter = painterResource(id = R.drawable.hospital_no_fill),
                                    contentDescription = "Hospital",
                                    modifier = Modifier
                                        .width(33.dp)
                                        .height(33.dp)
                                        .clickable {
                                            selectedFilter = "Hospital"
                                        },
                                    tint = if(selectedFilter == "Hospital") Color.White else BlackGray
                                )
                                Text(
                                    text = "Hospital",
                                    fontSize = 10.sp,
                                    color = if(selectedFilter == "Hospital") Color.White else BlackGray
                                )
                            }
                        }
                    }
                }

                items(filteredPlaces) { place ->
                    PlaceCardList(
                        place = place,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onPlaceSelected(place) }
                    )
                }
            }
        }
    }
}

data class SearchResult(
    val name: String,
    val secondaryText: String?,
    val distanceMeters: Float
)

fun formatDistance(distanceMeters: Float): String {
    return if (distanceMeters < 1000) {
        "${distanceMeters.toInt()} m"
    } else {
        String.format("%.1f km", distanceMeters / 1000)
    }
}


@Composable
fun SearchBarHomeList(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    performSearch: (String) -> Unit,
    searchResults: List<SearchResult>,
    onResultSelected: (SearchResult) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val focusManager = LocalFocusManager.current

    TextField(
        value = searchQuery,
        shape = RoundedCornerShape(100.dp),
        onValueChange = { onSearchQueryChange(it) },
        modifier = modifier
            .padding(start = 20.dp, end = 20.dp)
            .fillMaxWidth()
            .background(Color.Transparent, RoundedCornerShape(100.dp))
            .shadow(4.dp, RoundedCornerShape(100.dp)),
        placeholder = {
            if (!isFocused) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.search),
                        contentDescription = "Buscar",
                        modifier = Modifier
                            .size(16.dp)
                            .clickable { performSearch(searchQuery) },
                        tint = BlackGray
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Empieza a buscar",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        },
        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
        singleLine = true,
        leadingIcon = {
            if (isFocused) {
                Icon(
                    painter = painterResource(id = R.drawable.ex),
                    contentDescription = "Borrar",
                    modifier = Modifier
                        .size(18.dp)
                        .padding(start = 8.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onSearchQueryChange("")
                            focusManager.clearFocus()
                        },
                    tint = BlackGray
                )
            }
        },
        trailingIcon = {
            if (isFocused) {
                Icon(
                    painter = painterResource(id = R.drawable.filter_search),
                    contentDescription = "Filtro",
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 8.dp)
                        .clickable { },
                    tint = BlackGray
                )
            }
        },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                performSearch(searchQuery)
                focusManager.clearFocus()
            }
        ),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White
        ),
        interactionSource = interactionSource
    )

    if (searchResults.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier
                .padding(start = 40.dp, end = 40.dp, top = 110.dp)
                .background(Color.White, RoundedCornerShape(16.dp))
                .fillMaxWidth()
                .heightIn(max = 600.dp)
                .clip(RoundedCornerShape(16.dp)),
            verticalArrangement = Arrangement.Top
        ) {
            items(searchResults) { result ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onResultSelected(result)
                            focusManager.clearFocus()
                        }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.mark),
                            contentDescription = "Ícono del lugar",
                            modifier = Modifier.size(18.dp),
                            tint = BlackGray
                        )
                        Text(
                            text = formatDistance(result.distanceMeters),
                            fontSize = 9.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = result.name,
                            fontSize = 14.sp,
                            color = BlackGray,
                            fontWeight = FontWeight.SemiBold
                        )
                        result.secondaryText?.let {
                            Text(
                                text = it,
                                fontSize = 12.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 6.dp)
                            )
                        }
                    }
                }

                if (searchResults.last() != result) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 1.dp,
                        color = Color.Gray.copy(alpha = 0.2f)
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true, heightDp = 1200)
@Composable
fun PlaceCardFullScreenPreview() {
    val samplePlace = PlacesResponseDto(
        title = "Parking de La Tella Parking de La Tella Parking de La Tella",
        address = "HU-631",
        location = "42.5624343,0.0425323",
        rating = 4.3f,
        userVotes = 108,
        photoUrls = listOf("https://gratisography.com/wp-content/uploads/2025/02/gratisography-when-pigs-fly-1170x780.jpg"),
        type = 1,
        placeId = "ChIJc-IaFVwBqBIRl6QdE2o8gu8",
        currentOpeningHours = null,
        opening_hours = null,
        international_phone_number = null,
        formattedPhoneNumber = null,
        website = null,
        reviews = null,
        source = 1
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1200.dp)
            .background(Color.Gray)
    ) {
        FullScreenPlaceList(
            places = listOf(samplePlace),
            onDismiss = {},
            onPlaceSelected = {}

        )
    }
}