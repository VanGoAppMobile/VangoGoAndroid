package com.vango.presentation.main.home.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vango.R
import com.vango.presentation.theme.BackgroundButtonColor
import com.vango.presentation.theme.BackgroundColorCard
import com.vango.presentation.theme.BackgroundUnselected
import com.vango.presentation.theme.BlackGray
import com.vango.presentation.theme.MainColor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapLayersMenu(
    selectedLayer: MapLayer,
    selectedOption: MapOption?,
    onLayerSelected: (MapLayer) -> Unit,
    onOptionSelected: (MapOption?) -> Unit,
    onDismiss: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val offsetY = remember { Animatable(600f) }
    val density = LocalDensity.current
    val navbarHeight = with(density) { 48.dp.toPx() }
    LaunchedEffect(Unit) {
        offsetY.animateTo(0f, animationSpec = tween(300))
    }

    Box(modifier = Modifier.fillMaxSize()) {
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(BlackGray.copy(alpha = 0.4f))
//                .clickable(
//                    onClick = {
//                        scope.launch {
//                            offsetY.animateTo(600f, animationSpec = tween(300))
//                            onDismiss()
//                        }
//                    },
//                    indication = null,
//                    interactionSource = remember { MutableInteractionSource() }
//                )
//        )

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
                    .fillMaxWidth().padding(top = 16.dp, start = 20.dp, end = 20.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)


            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Surface (
                        color = MainColor,
                        modifier = Modifier.size(32.dp),
                        shape = RoundedCornerShape(11.dp),


                    ){
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
                        text = "Configuración del mapa",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = BlackGray
                    )
                }

                Box(
                    modifier = Modifier
                        .background(BackgroundColorCard, shape = RoundedCornerShape(20.dp))
                        .fillMaxWidth()
                        .height(130.dp),

                ) {
                    Column {
                        Text(
                            text = "Tipo de mapa",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BlackGray,
                            modifier = Modifier.padding(top = 10.dp, bottom = 10.dp, start = 20.dp)
                        )

                        Row(
                            modifier = Modifier.padding(start = 20.dp, end = 20.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            MapLayerButton(
                                text = "Predefinido",
                                iconRes = R.drawable.preset,
                                isSelected = selectedLayer == MapLayer.NORMAL,
                                onClick = { onLayerSelected(MapLayer.NORMAL) }
                            )
                            MapLayerButton(
                                text = "Satélite",
                                iconRes = R.drawable.satellite,
                                isSelected = selectedLayer == MapLayer.SATELLITE,
                                onClick = { onLayerSelected(MapLayer.SATELLITE) }
                            )
                            MapLayerButton(
                                text = "Terreno",
                                iconRes = R.drawable.terrain,
                                isSelected = selectedLayer == MapLayer.RELIEF,
                                onClick = { onLayerSelected(MapLayer.RELIEF) }
                            )
                            MapLayerButton(
                                text = "Sin conexión",
                                iconRes = R.drawable.save_map,
                                isSelected = selectedLayer == MapLayer.NO_CONNECTION,
                                onClick = { onLayerSelected(MapLayer.NO_CONNECTION) }
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .background(BackgroundColorCard, shape = RoundedCornerShape(20.dp))
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                        .heightIn()
                        .padding(top = 15.dp, bottom = 15.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column {
                        Text(
                            text = "Opciones del mapa",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BlackGray,
                            modifier = Modifier.padding(bottom = 10.dp, start = 20.dp)
                        )
                        Row(
                            horizontalArrangement = Arrangement.SpaceAround,
                            modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp),
                        ) {
                            MapOptionButton(
                                text = "Tráfico",
                                iconRes = R.drawable.road,
                                iconResFull = R.drawable.road_full,
                                isSelected = selectedOption == MapOption.TRAFFIC,
                                onClick = {
                                    onOptionSelected(if (selectedOption == MapOption.TRAFFIC) null else MapOption.TRAFFIC)
                                }
                            )
                            MapOptionButton(
                                text = "Tiempo",
                                iconRes = R.drawable.weather,
                                iconResFull = R.drawable.weather_full,
                                isSelected = selectedOption == MapOption.WEATHER,
                                onClick = {
                                    onOptionSelected(if (selectedOption == MapOption.WEATHER) null else MapOption.WEATHER)
                                }
                            )
                            MapOptionButton(
                                text = "Transporte\nPúblico",
                                iconRes = R.drawable.traffic,
                                iconResFull = R.drawable.traffic_full,
                                isSelected = selectedOption == MapOption.PUBLIC_TRANSPORT,
                                onClick = {
                                    onOptionSelected(if (selectedOption == MapOption.PUBLIC_TRANSPORT) null else MapOption.PUBLIC_TRANSPORT)
                                }
                            )
                            Spacer(modifier = Modifier.width(60.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MapLayerButton(
    text: String,
    iconRes: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Surface(
            onClick = onClick,
            modifier = Modifier
                .width(64.dp)
                .height(64.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color.Unspecified,

            border = if (isSelected) BorderStroke(2.dp, BackgroundButtonColor) else null
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = text,
                modifier = Modifier
                    .size(64.dp)
                    .align(Alignment.CenterHorizontally),
                )


        }
        Text(
            text = text,
            modifier = Modifier.padding(top = 6.dp),
            fontSize = 10.sp,
            color = BlackGray,
            maxLines = 3,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun MapOptionButton(
    text: String,
    iconRes: Int,
    iconResFull: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Surface(
            onClick = onClick,
            modifier = Modifier
                .width(64.dp)
                .height(64.dp),
            shape = RoundedCornerShape(16.dp),
            color = if(isSelected) BackgroundButtonColor else BackgroundUnselected,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    painter = if(isSelected) painterResource(id = iconResFull) else painterResource(id = iconRes),
                    contentDescription = text,
                    modifier = Modifier.size(25.dp),
                    tint = Color.White
                )
            }
        }
        Text(
            text = text,
            modifier = Modifier.padding(top = 6.dp),
            fontSize = 10.sp,
            color = BlackGray,
            maxLines = 3,
            textAlign = TextAlign.Center
        )
    }
}


enum class MapLayer {
    NORMAL,
    RELIEF,
    SATELLITE,
    NO_CONNECTION
}

enum class MapOption {
    TRAFFIC,
    WEATHER,
    PUBLIC_TRANSPORT
}

@Preview(showBackground = true)
@Composable
fun MapLayersMenuPreview() {
    Box(modifier = Modifier.fillMaxSize()) {
        MapLayersMenu(
            onLayerSelected = {},
            onOptionSelected = {},
            selectedLayer = MapLayer.NORMAL,
            selectedOption = MapOption.TRAFFIC,
            onDismiss = {}
        )
    }
}