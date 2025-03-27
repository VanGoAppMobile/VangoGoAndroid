package com.vango.presentation.main.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.maps.android.compose.CameraPositionState
import com.vango.R
import com.vango.presentation.main.home.HomeViewModel
import com.vango.presentation.theme.BackgroundButtonColor
import com.vango.presentation.theme.BackgroundColorButtonPrincipal
import com.vango.presentation.theme.BackgroundColorList
import com.vango.presentation.theme.BackgroundUnselected

@Composable
fun LocationActionButtons(
    onMoveToLocation: () -> Unit,
    onMapLayerClick: () -> Unit,
    selectedOption: MapOption?,
    isLocationVisible: Boolean,
    isShowingFavorites: Boolean,
    onFavoritesClick: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            onClick = onMoveToLocation,
            modifier = Modifier
                .width(50.dp)
                .height(50.dp)
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(13.dp)),
            shape = RoundedCornerShape(13.dp),
            color = if (isLocationVisible) BackgroundButtonColor else  BackgroundUnselected
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(
                        id = if (isLocationVisible) R.drawable.location else R.drawable.location_no_fill
                    ),                    contentDescription = "Ubicación",
                    modifier = Modifier.size(25.dp),
                    tint = Color.White
                )
            }
        }

        Surface(
            onClick = onMapLayerClick,
            modifier = Modifier
                .width(50.dp)
                .height(50.dp)
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(13.dp)),
            shape = RoundedCornerShape(13.dp),
            color = BackgroundUnselected
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.map_type),
                    contentDescription = "tipo de mapa",
                    modifier = Modifier.size(25.dp),
                    tint = Color.White
                )
            }
        }

        Surface(
            onClick = { onFavoritesClick(!isShowingFavorites) },
            modifier = Modifier
                .width(50.dp)
                .height(50.dp)
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(13.dp)),
            shape = RoundedCornerShape(13.dp),
            color = if(isShowingFavorites) BackgroundButtonColor else BackgroundUnselected
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = if(isShowingFavorites) painterResource(id = R.drawable.heart_fill) else painterResource(id = R.drawable.heart_stroke),
                    contentDescription = "tipo de mapa",
                    modifier = Modifier.size(25.dp),
                    tint = if(isShowingFavorites) Color.White else Color.Unspecified
                )
            }
        }
        Surface(
            onClick = onMapLayerClick,
            modifier = Modifier
                .width(50.dp)
                .height(50.dp)
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(13.dp)),
            shape = RoundedCornerShape(13.dp),
            color = if(selectedOption != null) BackgroundButtonColor else BackgroundUnselected
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(
                        id = when (selectedOption) {
                            MapOption.TRAFFIC -> R.drawable.road_full
                            MapOption.WEATHER -> R.drawable.weather_full
                            MapOption.PUBLIC_TRANSPORT -> R.drawable.traffic_full
                            null -> R.drawable.road
                        }
                    ),
                    contentDescription = "Tipo de mapa",
                    modifier = Modifier.size(25.dp),
                    tint = Color.White
                )
            }
        }
    }
}


@Composable
fun BottomActionButtons(
    isListOpen: Boolean,
    onToggleList: () -> Unit,
    onAddAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!isListOpen) {
            Spacer(
                modifier = Modifier
                    .width(50.dp)
                    .height(0.dp)
            )
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }

        Surface(
            onClick = onToggleList,
            modifier = Modifier
                .width(120.dp)
                .height(50.dp)
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(13.dp)),
            shape = RoundedCornerShape(13.dp),
            color = BackgroundColorList
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = if(!isListOpen) painterResource(id = R.drawable.list) else painterResource(id = R.drawable.map_fill),
                    contentDescription = if(!isListOpen)"Lista" else "Mapa",
                    modifier = Modifier.size(25.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if(!isListOpen)"Lista" else "Mapa",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }


        if (!isListOpen) {
            Surface(
                onClick = onAddAction,
                modifier = Modifier
                    .size(50.dp)
                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(13.dp)),
                shape = RoundedCornerShape(13.dp),
                color = BackgroundColorButtonPrincipal
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.add_btn),
                        contentDescription = "Agregar",
                        modifier = Modifier.size(25.dp),
                        tint = Color.Unspecified
                    )
                }
            }
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}


@Composable
fun BottomCoordButton(
    onNavigateToResults: () -> Unit,
    onAddAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(
            modifier = Modifier
                .width(50.dp)
                .height(0.dp)
        )

        Surface(
            onClick = onAddAction,
            modifier = Modifier
                .size(60.dp)
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(13.dp)),
            shape = RoundedCornerShape(13.dp),
            color = BackgroundColorButtonPrincipal
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.coord),
                    contentDescription = "Agregar",
                    modifier = Modifier.size(25.dp),
                    tint = Color.Unspecified
                )
                Text(
                    text = "Buscar por coord.",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Light,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun TopCenterButton(
    onNavigateToResults: () -> Unit,
    modifier: Modifier = Modifier,
    cameraPositionState: CameraPositionState,
    selectedFilterTypes: Set<Int>,
    viewModel: HomeViewModel,
) {
    val currentZoom = cameraPositionState.position.zoom
    val buttonText = if (currentZoom < 11f) "Acércate más" else "Buscar aquí"
    Surface(
        onClick = onNavigateToResults,
        modifier = modifier
            .width(128.dp)
            .height(40.dp)
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(13.dp)),
        shape = RoundedCornerShape(13.dp),
        color = BackgroundColorList
    ){
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = buttonText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun TopCenterButtonSelectedPoint(
    onNavigateToResults: () -> Unit,
    modifier: Modifier = Modifier,
    cameraPositionState: CameraPositionState,
    selectedFilterTypes: Set<Int>,
    viewModel: HomeViewModel,
) {
    val currentZoom = cameraPositionState.position.zoom
    Surface(
        onClick = onNavigateToResults,
        modifier = modifier
            .width(128.dp)
            .height(40.dp)
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(13.dp)),
        shape = RoundedCornerShape(13.dp),
        color = BackgroundColorList
    ){
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Acércate más",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}