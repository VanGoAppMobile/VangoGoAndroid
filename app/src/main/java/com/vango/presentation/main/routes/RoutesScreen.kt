package com.vango.presentation.main.routes

import android.location.Geocoder
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.Dot
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.vango.R
import com.vango.presentation.main.home.HomeViewModel
import com.vango.presentation.theme.BlackGray
import com.vango.presentation.theme.MainColor
import java.util.Locale

@Composable
fun RoutesScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val savedPoints = remember { viewModel.loadSavedRoute(context) }

    Column(
        modifier = Modifier.fillMaxSize().padding(top = 55.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        Text(
            text = "Hoy",
            style = MaterialTheme.typography.headlineMedium,
            color = BlackGray,
            textAlign = androidx.compose.ui.text.style.TextAlign.Start
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = modifier
                .background(Color.White),
            ) {

            if (savedPoints.isNotEmpty()) {



                RouteCard(
                    routeName = "Ruta 1",
                    points = savedPoints,
                    modifier = Modifier.align(Alignment.Center)
                )

            } else {
                Text(
                    text = "No hay rutas guardadas",
                    style = MaterialTheme.typography.headlineMedium,
                    color = BlackGray
                )
            }
        }
    }

}


@Composable
fun RouteCard(
    routeName: String,
    points: List<Pair<LatLng?, String?>>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val geocoder = Geocoder(context, Locale.getDefault())

    Card(
        modifier = modifier
            .width(329.dp)
            .heightIn(),
        shape = RoundedCornerShape(15.dp)
    ) {
        Column(
            modifier = Modifier
                .background(Color.White)
        ) {
            val cameraPositionState = rememberCameraPositionState {
                val firstValidPoint = points.firstOrNull { it.first != null }?.first
                position = CameraPosition.fromLatLngZoom(
                    firstValidPoint ?: LatLng(42.0, -8.0),
                    10f
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.3f)
                    .clip(RoundedCornerShape(15.dp))
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(
                        mapStyleOptions = MapStyleOptions.loadRawResourceStyle(
                            LocalContext.current,
                            R.raw.map_style
                        ),
                        isMyLocationEnabled = false
                    ),
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = false,
                        compassEnabled = false,
                        myLocationButtonEnabled = false,
                        scrollGesturesEnabled = false,
                        zoomGesturesEnabled = false,
                        tiltGesturesEnabled = false
                    )
                ) {
                    points.forEachIndexed { index, point ->
                        point.first?.let { latLng ->
                            Marker(
                                state = MarkerState(position = latLng),
                                title = point.second ?: "Punto ${index + 1}",
                                snippet = "(${latLng.latitude}, ${latLng.longitude})",
                                icon = BitmapDescriptorFactory.defaultMarker()
                            )
                        }
                    }

                    val validPoints = points.mapNotNull { it.first }
                    if (validPoints.size >= 2) {
                        Polyline(
                            points = validPoints,
                            color = Color.Blue,
                            width = 5f,
                            pattern = listOf(Dot(), Gap(10f))
                        )
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
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,

                        ) {

                        Text(
                            text = routeName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = BlackGray
                        )

                        Row(
                            modifier = Modifier.widthIn(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End
                        ) {


                            Surface(
                                modifier = Modifier.size(23.dp),
                                color = MainColor,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Ir",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Spacer(Modifier.width(3.dp))

                            Surface(
                                modifier = Modifier.size(23.dp),
                                color = BlackGray,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.trash),
                                        contentDescription = "Eliminar ruta",
                                        tint = Color.White,
                                        modifier = Modifier
                                            .width(12.dp)
                                            .height(14.dp)
                                    )
                                }
                            }


                        }


                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val firstLocality = points.firstOrNull()?.first?.let { latLng ->
                        try {
                            val addresses =
                                geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                            addresses?.firstOrNull()?.locality ?: "Desconocido"
                        } catch (e: Exception) {
                            "Desconocido"
                        }
                    } ?: "Desconocido"

                    // Obtener la localidad del último punto desde LatLng (si existe) o desde la dirección
                    val lastLocality = points.lastOrNull()?.first?.let { latLng ->
                        try {
                            val addresses =
                                geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                            addresses?.firstOrNull()?.locality ?: "Desconocido"
                        } catch (e: Exception) {
                            "Desconocido"
                        }
                    } ?: points.lastOrNull()?.second?.let { address ->
                        try {
                            val parts = address.split(", ")
                            if (parts.size >= 2) parts[parts.size - 3] else "Desconocido"
                        } catch (e: Exception) {
                            "Desconocido"
                        }
                    } ?: "Desconocido"

                    Text(
                        text = "$firstLocality - $lastLocality (${points.size} paradas)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = BlackGray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}