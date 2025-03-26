package com.vango.presentation.main.home.components

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.vango.R
import com.vango.presentation.main.home.HomeViewModel
import com.vango.presentation.theme.BackgroundButtonColor
import com.vango.presentation.theme.BackgroundColorCard
import com.vango.presentation.theme.BackgroundColorImage
import com.vango.presentation.theme.BackgroundColorList
import com.vango.presentation.theme.BlackGray
import com.vango.presentation.theme.MainColor
import com.vango.presentation.theme.WhiteGray
import com.vango.presentation.theme.YellowMelow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapNewRoutePointMenu(
    selectedRoutePoint: MapNewPointRoute?,
    onLayerSelected: (MapNewPointRoute) -> Unit,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val offsetY = remember { Animatable(600f) }
    val density = LocalDensity.current
    val navbarHeight = with(density) { 48.dp.toPx() }

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
                        text = "¿Qué quieres crear?",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = BlackGray
                    )
                }

                Box(
                    modifier = Modifier
                        .background(BackgroundColorCard, shape = RoundedCornerShape(20.dp))
                        .fillMaxWidth()
                        .height(149.dp),
                    contentAlignment = Alignment.Center

                ) {

                    Column(
                        modifier = Modifier
                            .padding(start = 20.dp, end = 20.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        MapNewRoutePointButton(
                            text = "Crear ruta",
                            iconRes = R.drawable.ruta,
                            tint = Color.White,
                            color = MainColor,
                            onClick = {
                                onLayerSelected(MapNewPointRoute.CREATE_ROUTE)
                            }
                        )

                        Spacer(
                            modifier = Modifier.height(13.dp),
                        )

                        MapNewRoutePointButton(
                            text = "Crear punto",
                            iconRes = R.drawable.marker,
                            tint = Color.White,
                            color = BackgroundButtonColor,
                            onClick = {
                                onLayerSelected(MapNewPointRoute.CREATE_POINT)
                            }
                        )
                    }

                }
            }
        }
    }
}




@Composable
fun MapNewPointMenu(
    selectedPoint: LatLng?,
    selectedAddress: String?,
    isPoint: Boolean?,
    onDismiss: () -> Unit,
    onClearAndDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val offsetY = remember { Animatable(600f) }
    val density = LocalDensity.current
    with(density) { 48.dp.toPx() }

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
                        text = if(isPoint == true) "Localiza el nuevo punto" else "Localiza el primer punto de la ruta",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = BlackGray
                    )
                }

                if (selectedPoint != null && selectedAddress != null) {
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

                            Text(
                                text = selectedAddress,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                color = BlackGray
                            )
                            Spacer(
                                modifier = Modifier.height(3.dp),
                            )
                            Text(
                                text = "(${selectedPoint.latitude}, ${selectedPoint.longitude})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                color = BlackGray
                            )

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
                                                    scope.launch {
                                                        offsetY.animateTo(
                                                            600f,
                                                            animationSpec = tween(300)
                                                        )
                                                        onConfirm()
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
        }
    }
}





@Composable
fun MapNewPointNameMenu(
    onNameConfirmed: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    selectedAddress: String?,
    selectedPoint: LatLng?

) {
    val scope = rememberCoroutineScope()
    val offsetY = remember { Animatable(600f) }
    var nameInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

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
                        text = "Añade un nombre \nal nuevo punto",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = BlackGray
                    )
                }

                Box(
                    modifier = Modifier
                        .background(BackgroundColorCard, shape = RoundedCornerShape(20.dp))
                        .fillMaxWidth()
                        .height(130.dp),
                    contentAlignment = Alignment.Center

                ) {

                    Column(
                        modifier = Modifier
                            .padding(start = 20.dp, end = 20.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        TextField(
                            value = nameInput,
                            onValueChange = {
                                nameInput = it
                                errorMessage = null
                            },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = TextStyle(
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            ),
                            placeholder = {
                                Text(
                                    "Pon un nombre a este punto",
                                    color = Color.LightGray,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFFFFFFF),
                                unfocusedContainerColor = Color(0xFFFFFFFF),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )

                        errorMessage?.let {
                            Text(
                                text = it,
                                color = Color.Red,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

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
//                                                    onClearAndDismiss()
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
                                        contentDescription = "Confirm",
                                        modifier = Modifier
                                            .width(12.5.dp)
                                            .height(14.29.dp)
                                            .clickable {
                                                if (nameInput.isBlank()) {
                                                    errorMessage = "El nombre no puede estar vacío"
                                                } else {
                                                    onNameConfirmed(nameInput)
                                                    scope.launch {
                                                        offsetY.animateTo(
                                                            600f,
                                                            animationSpec = tween(300)
                                                        )
                                                        onConfirm()
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
    }
}


@Composable
fun MapNewPointTagMenu(
    selectedPoint: LatLng?,
    selectedAddress: String?,
    onNameConfirmed: String?,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val offsetY = remember { Animatable(600f) }

    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedSubcategory by remember { mutableStateOf<String?>(null) }

    val subcategories = remember {
        mapOf(
            "Campings" to mapOf(
                "Camping" to Triple(
                    "Camping Estacional",
                    "De corta estancia, abierto generalmente por temporada. Destinada al turismo vacacional. Cuenta con muchos servicios, que pueden ser de pago.",
                    R.drawable.camper_no_fill
                ),
                "Gampling" to Triple(
                    "Gampling de Lujo",
                    "De corta estancia, abierto generalmente por temporada. Destinada al turismo vacacional. Puede tener parte camping normal, pero al menos tiene una zona de bungalows o tiendas de diseño.",
                    R.drawable.gampling
                ),
                "Camping residencial" to Triple(
                    "Residencial Permanente",
                    "Abierto todo el año. Puede ser para estancias vacacionales o de vivienda habitual. Son privados, y no suelen admitir alquiler por temporada. Las parcelas son compradas o arrendadas para largas estancias.",
                    R.drawable.residencial_camping
                )
            ),
            "Área de AC" to mapOf(
                "Área AC pública" to Triple(
                    "Área Pública Gratuita",
                    "Espacio gratuito gestionado por entidades públicas para autocaravanas.",
                    R.drawable.ac_area
                ),
                "Área AC " to Triple(
                    "Área de Pago",
                    "Zona de pago gestionada por empresas o particulares para autocaravanas.",
                    R.drawable.private_area
                ),
                "Área AC sin servicios" to Triple(
                    "Área Básica",
                    "Espacio básico sin agua, electricidad ni vertederos.",
                    R.drawable.sn_area
                ),
                "Área AC no oficial" to Triple(
                    "Área Informal",
                    "Lugar no regulado usado informalmente por autocaravanistas.",
                    R.drawable.no_oficial_area
                )
            ),
            "Puntos de venta" to mapOf(
                "Accesorios camping" to Triple(
                    "Tienda de Accesorios",
                    "Tienda especializada en equipamiento para camping y autocaravanas.",
                    R.drawable.ex
                ),
                "Tienda de comestibles" to Triple(
                    "Comestibles Básicos",
                    "Establecimiento con alimentos y productos básicos para viajeros.",
                    R.drawable.ex
                ),
                "Punto venta GLP" to Triple(
                    "Recarga de GLP",
                    "Lugar para recargar gas licuado de petróleo para vehículos o cocinas.",
                    R.drawable.ex
                ),
                "Otros puntos de venta" to Triple(
                    "Venta Variada",
                    "Comercios diversos para necesidades de los campistas.",
                    R.drawable.ex
                )
            ),
            "Servicios" to mapOf(
                "Lavandería" to Triple(
                    "Servicio de Lavandería",
                    "Servicio para lavar y secar ropa durante el viaje.",
                    R.drawable.ex
                ),
                "Farmacia" to Triple(
                    "Farmacia de Viaje",
                    "Establecimiento para medicamentos y productos de salud.",
                    R.drawable.ex
                ),
                "Gasolinera" to Triple(
                    "Estación de Combustible",
                    "Punto de repostaje de combustible para vehículos.",
                    R.drawable.ex
                ),
                "Área de servicio" to Triple(
                    "Área Multiservicio",
                    "Zona con múltiples servicios como agua, electricidad y vertederos.",
                    R.drawable.ex
                ),
                "Centro de salud" to Triple(
                    "Atención Médica",
                    "Instalación médica para emergencias o consultas.",
                    R.drawable.ex
                ),
                "Punto de carga" to Triple(
                    "Carga Eléctrica",
                    "Estación para recargar vehículos eléctricos.",
                    R.drawable.ex
                ),
                "Taller" to Triple(
                    "Reparación de Vehículos",
                    "Servicio de reparación y mantenimiento de vehículos.",
                    R.drawable.ex
                ),
                "Supermercado" to Triple(
                    "Supermercado Completo",
                    "Tienda grande con variedad de productos para campistas.",
                    R.drawable.ex
                )
            ),
            "Puntos de Interés" to mapOf(
                "Tienda" to Triple(
                    "Tienda Local",
                    "Comercio local de interés para visitantes.",
                    R.drawable.ex
                ),
                "Mercadillo" to Triple(
                    "Mercado al Aire Libre",
                    "Mercado al aire libre con productos variados.",
                    R.drawable.ex
                ),
                "Online" to Triple(
                    "Recurso Online",
                    "Servicio o recurso accesible por internet.",
                    R.drawable.ex
                ),
                "Feria" to Triple(
                    "Feria Temporal",
                    "Evento temporal con actividades y ventas.",
                    R.drawable.ex
                )
            )
        )
    }

    val mainCategories = listOf(
        "Campings" to R.drawable.camper_no_fill,
        "Área de AC" to R.drawable.ac_area,
        "Puntos de venta" to R.drawable.sell_point,
        "Servicios" to R.drawable.services,
        "Puntos de Interés" to R.drawable.interest_point
    )

    LaunchedEffect(Unit) {
        offsetY.animateTo(0f, animationSpec = tween(300))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxHeight()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn()
                .align(Alignment.TopStart)
                .offset(y = offsetY.value.dp),
            shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
            color = Color.White,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 55.dp, start = 20.dp, end = 20.dp, bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
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

                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Etiqueta este punto",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = BlackGray
                    )
                }

                Box(
                    modifier = Modifier
                        .background(Color.Transparent, shape = RoundedCornerShape(20.dp))
                        .fillMaxWidth()
                        .heightIn(),
                    contentAlignment = Alignment.Center

                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        Spacer(modifier = Modifier.height(12.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(color = WhiteGray, shape = RoundedCornerShape(12.dp)),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(18.dp))
                            Text(
                                text = "Categoría principal",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = BlackGray,
                                modifier = Modifier
                                    .align(Alignment.Start)
                                    .padding(start = 20.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    mainCategories.take(4).forEach { (text, iconRes) ->
                                        ButtonCategory(
                                            text = text,
                                            iconRes = iconRes,
                                            isSelected = selectedCategory == text,
                                            onClick = {
                                                selectedCategory = text
                                                selectedSubcategory = null
                                            }
                                        )
                                    }
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    mainCategories.drop(4).forEach { (text, iconRes) ->
                                        ButtonCategory(
                                            text = text,
                                            iconRes = iconRes,
                                            isSelected = selectedCategory == text,
                                            onClick = {
                                                selectedCategory = text
                                                selectedSubcategory = null
                                            }
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(color = WhiteGray, shape = RoundedCornerShape(12.dp)),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = "Subcategorías",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = BlackGray,
                                modifier = Modifier
                                    .align(Alignment.Start)
                                    .padding(start = 20.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            val currentSubcategories = subcategories[selectedCategory] ?: emptyMap()
                            if (currentSubcategories.isNotEmpty()) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    selectedSubcategory?.let { selected ->
                                        val (title, description, iconRes) = currentSubcategories[selected]
                                            ?: Triple("", "", R.drawable.ex)
                                        Column {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                ButtonCategory(
                                                    text = selected,
                                                    iconRes = iconRes,
                                                    isSelected = true,
                                                    onClick = { selectedSubcategory = null }
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column {
                                                    Text(
                                                        text = title,
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = BlackGray
                                                    )
                                                    Text(
                                                        text = description,
                                                        fontSize = 10.sp,
                                                        color = Color.Gray,
                                                        maxLines = 3,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    val remainingSubcategories =
                                        currentSubcategories.keys.filter { it != selectedSubcategory }
                                    remainingSubcategories.chunked(4).forEach { chunk ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            chunk.forEach { subcategory ->
                                                val (title, description, iconRes) = currentSubcategories[subcategory]
                                                    ?: Triple("", "", R.drawable.ex)
                                                ButtonCategory(
                                                    text = title,
                                                    iconRes = iconRes,
                                                    isSelected = false,
                                                    onClick = { selectedSubcategory = subcategory }
                                                )
                                            }
                                            repeat(4 - chunk.size) {
                                                Spacer(modifier = Modifier.width(64.dp))
                                            }
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(20.dp))
                        }


                    }


                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(51.dp)
                            .clickable {
                                scope.launch {
                                    offsetY.animateTo(
                                        600f,
                                        animationSpec = tween(300)
                                    )
                                    onConfirm()
                                }
                            },
                        shape = RoundedCornerShape(20.dp),
                        color = MainColor,

                        ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,

                            ) {


                            Text(
                                text = "Siguiente",
                                modifier = Modifier.padding(top = 6.dp),
                                fontSize = 14.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                maxLines = 3,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                }


            }
        }
    }
}

@Composable
fun MapNewPointConfirmMenu(
    selectedPoint: LatLng?,
    selectedAddress: String?,
    selectedName: String?,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val offsetY = remember { Animatable(600f) }
    var nameInput by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        offsetY.animateTo(0f, animationSpec = tween(300))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .heightIn()
    ) {
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Surface(
                            color = BackgroundColorList,
                            modifier = Modifier.size(32.dp),
                            shape = RoundedCornerShape(11.dp),
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.arrow_back),
                                    contentDescription = "volver",
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

                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "¿Quieres crear este punto?",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = BlackGray
                    )
                }

                Box(
                    modifier = Modifier
                        .background(Color.Transparent, shape = RoundedCornerShape(20.dp))
                        .fillMaxWidth()
                        .heightIn(),
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 20.dp, end = 20.dp),
                            horizontalArrangement = Arrangement.Start,

                            ) {
                            Text(
                                text = "Nombre del punto",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MainColor
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Column(
                            modifier = Modifier
                                .padding(start = 20.dp, end = 20.dp)
                                .fillMaxWidth()
                                .height(37.dp)
                                .background(color = WhiteGray, shape = RoundedCornerShape(12.dp)),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center

                        ) {
                            if (selectedName != null) {
                                Text(
                                    text = selectedName,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = BlackGray
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 20.dp, end = 20.dp),
                            horizontalArrangement = Arrangement.Start,

                            ) {
                            Text(
                                text = "Dirección del punto",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MainColor
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,

                            ) {

                            Column(
                                modifier = Modifier
                                    .padding(start = 20.dp, end = 20.dp)
                                    .fillMaxWidth()
                                    .heightIn()
                                    .background(
                                        color = WhiteGray,
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center

                            ) {

                                if (selectedAddress != null) {
                                    Text(
                                        text = selectedAddress,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = BlackGray
                                    )
                                }
                                Spacer(
                                    modifier = Modifier.height(3.dp),
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                var showDMS by remember { mutableStateOf(false) }

                                if (selectedPoint != null) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,

                                        ) {

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Text(
                                            text = if (showDMS) latLngToDMS(selectedPoint) else selectedPoint?.let {
                                                String.format(
                                                    "%.4f, %.4f",
                                                    it.latitude,
                                                    it.longitude
                                                )
                                            } ?: "0.0000, 0.0000",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Normal,
                                            color = BlackGray
                                        )

                                        Surface(
                                            modifier = Modifier
                                                .padding(top = 4.dp)
                                                .height(24.dp)
                                                .clickable { showDMS = !showDMS },
                                            shape = RoundedCornerShape(8.dp),
                                            color = MainColor
                                        ) {
                                            Text(
                                                text = if (showDMS) "Decimal" else "DMS",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                modifier = Modifier.padding(
                                                    horizontal = 8.dp,
                                                    vertical = 4.dp
                                                )
                                            )
                                        }

                                    }


                                }
                            }
                        }
                    }


                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(51.dp)
                            .clickable {
                                scope.launch {
                                    offsetY.animateTo(600f, animationSpec = tween(300))
                                    onConfirm()
                                }

                            },

                        shape = RoundedCornerShape(20.dp),
                        color = MainColor,


                        ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,

                            ) {

                            Icon(
                                painter = painterResource(id = R.drawable.btn_create_point),
                                contentDescription = "Cerrar",
                                tint = Color.Unspecified,
                                modifier = Modifier
                                    .width(20.dp)
                                    .height(23.dp)

                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = "Crear punto",
                                modifier = Modifier.padding(top = 6.dp),
                                fontSize = 14.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                maxLines = 3,
                                textAlign = TextAlign.Center

                            )
                        }
                    }

                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(51.dp)
                            .clickable {
                                scope.launch {
                                    offsetY.animateTo(600f, animationSpec = tween(300))
                                    onConfirm()
                                }

                            },

                        shape = RoundedCornerShape(20.dp),
                        color = MainColor,
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,

                            ) {

                            Icon(
                                painter = painterResource(id = R.drawable.btn_create_point_lock),
                                contentDescription = "Cerrar",
                                tint = Color.Unspecified,
                                modifier = Modifier
                                    .width(20.dp)
                                    .height(23.dp)

                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = "Crear lugar secreto",
                                modifier = Modifier.padding(top = 6.dp),
                                fontSize = 14.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                maxLines = 3,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                }


            }
        }
    }
}

fun latLngToDMS(latLng: LatLng): String {
    val latDMS = degreesToDMS(latLng.latitude, true)
    val lngDMS = degreesToDMS(latLng.longitude, false)
    return "$latDMS, $lngDMS"
}

fun degreesToDMS(degrees: Double, isLatitude: Boolean): String {
    val absDegrees = abs(degrees)
    val deg = absDegrees.toInt()
    val minutesDouble = (absDegrees - deg) * 60
    val min = minutesDouble.toInt()
    val sec = ((minutesDouble - min) * 60).toInt()

    val direction = when {
        isLatitude && degrees >= 0 -> "N"
        isLatitude -> "S"
        degrees >= 0 -> "E"
        else -> "W"
    }
    return "$deg°$min'$sec\"$direction"
}

@Composable
fun MapNewPointTagServicesMenu(
    selectedPoint: LatLng?,
    selectedAddress: String?,
    onNameConfirmed: String?,
    onDismiss: () -> Unit,
    onConfirm: (Set<String>) -> Unit
) {
    val scope = rememberCoroutineScope()
    val offsetY = remember { Animatable(600f) }
    val selectedServices = remember { mutableStateOf<Set<String>>(emptySet()) }
    var nameInput by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        offsetY.animateTo(0f, animationSpec = tween(300))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxHeight()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn()
                .align(Alignment.TopStart)
                .offset(y = offsetY.value.dp),
            shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
            color = Color.White,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 55.dp, start = 20.dp, end = 20.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Surface(
                            color = BackgroundColorList,
                            modifier = Modifier.size(32.dp),
                            shape = RoundedCornerShape(11.dp),
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.arrow_back),
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

                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Servicios de este punto",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = BlackGray
                    )
                }

                Box(
                    modifier = Modifier
                        .background(Color.Transparent, shape = RoundedCornerShape(20.dp))
                        .fillMaxWidth()
                        .heightIn(),
                    contentAlignment = Alignment.Center

                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top,

                        ) {
                        Column(

                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            Column(
                                modifier = Modifier
                                    .width(175.dp)
                                    .heightIn()
                                    .background(WhiteGray, shape = RoundedCornerShape(20.dp))
                            ) {
                                Column(
                                    modifier = Modifier.padding(
                                        start = 10.dp,
                                        top = 20.dp,
                                        bottom = 20.dp
                                    ),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Servicios básicos",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = BlackGray
                                    )

                                    ButtonService(
                                        text = "Agua potable",
                                        iconRes = R.drawable.water,
                                        tint = Color.White,
                                        color = MainColor,
                                        isSelected = selectedServices.value.contains("Agua potable"),
                                        onClick = {
                                            val current = selectedServices.value
                                            selectedServices.value = if (current.contains("Agua potable")) {
                                                current - "Agua potable"
                                            } else {
                                                current + "Agua potable"
                                            }
                                        }
                                    )
                                    ButtonService(
                                        text = "Electricidad",
                                        iconRes = R.drawable.electricity,
                                        tint = Color.White,
                                        color = MainColor,
                                        isSelected = selectedServices.value.contains("Electricidad"),
                                        onClick = {
                                            val current = selectedServices.value
                                            selectedServices.value = if (current.contains("Electricidad")) {
                                                current - "Electricidad"
                                            } else {
                                                current + "Electricidad"
                                            }
                                        }
                                    )
                                    ButtonService(
                                        text = "Baños públicos",
                                        iconRes = R.drawable.public_wc,
                                        tint = Color.White,
                                        color = MainColor,
                                        isSelected = selectedServices.value.contains("Baños públicos"),
                                        onClick = {
                                            val current = selectedServices.value
                                            selectedServices.value = if (current.contains("Baños públicos")) {
                                                current - "Baños públicos"
                                            } else {
                                                current + "Baños públicos"
                                            }
                                        }
                                    )
                                    ButtonService(
                                        text = "Duchas",
                                        iconRes = R.drawable.shower,
                                        tint = Color.White,
                                        color = MainColor,
                                        isSelected = selectedServices.value.contains("Duchas"),
                                        onClick = {
                                            val current = selectedServices.value
                                            selectedServices.value = if (current.contains("Duchas")) {
                                                current - "Duchas"
                                            } else {
                                                current + "Duchas"
                                            }
                                        }
                                    )
                                    ButtonService(
                                        text = "Internet",
                                        iconRes = R.drawable.internet,
                                        tint = Color.White,
                                        color = MainColor,
                                        isSelected = selectedServices.value.contains("Internet"),
                                        onClick = {
                                            val current = selectedServices.value
                                            selectedServices.value = if (current.contains("Internet")) {
                                                current - "Internet"
                                            } else {
                                                current + "Internet"
                                            }
                                        }
                                    )
                                    ButtonService(
                                        text = "Lavandería",
                                        iconRes = R.drawable.laundry_no_fill,
                                        tint = Color.White,
                                        color = MainColor,
                                        isSelected = selectedServices.value.contains("Lavandería"),
                                        onClick = {
                                            val current = selectedServices.value
                                            selectedServices.value = if (current.contains("Lavandería")) {
                                                current - "Lavandería"
                                            } else {
                                                current + "Lavandería"
                                            }
                                        }
                                    )
                                    ButtonService(
                                        text = "Buena cobertura",
                                        iconRes = R.drawable.high_coberture,
                                        tint = Color.White,
                                        color = MainColor,
                                        isSelected = selectedServices.value.contains("Buena cobertura"),
                                        onClick = {
                                            val current = selectedServices.value
                                            selectedServices.value = if (current.contains("Buena cobertura")) {
                                                current - "Buena cobertura"
                                            } else {
                                                current + "Buena cobertura"
                                            }
                                        }
                                    )
                                    ButtonService(
                                        text = "Mala Cobertura",
                                        iconRes = R.drawable.low_coberture,
                                        tint = Color.White,
                                        color = MainColor,
                                        isSelected = selectedServices.value.contains("Mala Cobertura"),
                                        onClick = {
                                            val current = selectedServices.value
                                            selectedServices.value = if (current.contains("Mala Cobertura")) {
                                                current - "Mala Cobertura"
                                            } else {
                                                current + "Mala Cobertura"
                                            }
                                        }
                                    )
                                }
                            }

                            Column(
                                modifier = Modifier
                                    .width(175.dp)
                                    .heightIn()
                                    .background(WhiteGray, shape = RoundedCornerShape(20.dp))
                            ) {
                                Column(
                                    modifier = Modifier.padding(
                                        start = 10.dp,
                                        top = 20.dp,
                                        bottom = 20.dp
                                    ),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Servicios AC y campers",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = BlackGray
                                    )

                                    ButtonService(
                                        text = "Aguas grises",
                                        iconRes = R.drawable.grey_water,
                                        tint = Color.White,
                                        color = MainColor,
                                        isSelected = selectedServices.value.contains("Aguas grises"),
                                        onClick = {
                                            val current = selectedServices.value
                                            selectedServices.value = if (current.contains("Aguas grises")) {
                                                current - "Aguas grises"
                                            } else {
                                                current + "Aguas grises"
                                            }
                                        }
                                    )
                                    ButtonService(
                                        text = "Aguas negras",
                                        iconRes = R.drawable.black_water,
                                        tint = Color.White,
                                        color = MainColor,
                                        isSelected = selectedServices.value.contains("Aguas negras"),
                                        onClick = {
                                            val current = selectedServices.value
                                            selectedServices.value = if (current.contains("Aguas negras")) {
                                                current - "Aguas negras"
                                            } else {
                                                current + "Aguas negras"
                                            }
                                        }
                                    )
                                    ButtonService(
                                        text = "Parcelas con sombra",
                                        iconRes = R.drawable.shadow_location,
                                        tint = Color.White,
                                        color = MainColor,
                                        isSelected = selectedServices.value.contains("Parcelas con sombra"),
                                        onClick = {
                                            val current = selectedServices.value
                                            selectedServices.value = if (current.contains("Parcelas con sombra")) {
                                                current - "Parcelas con sombra"
                                            } else {
                                                current + "Parcelas con sombra"
                                            }
                                        }
                                    )
                                    ButtonService(
                                        text = "Suministro de gas",
                                        iconRes = R.drawable.gas_supply,
                                        tint = Color.White,
                                        color = MainColor,
                                        isSelected = selectedServices.value.contains("Suministro de gas"),
                                        onClick = {
                                            val current = selectedServices.value
                                            selectedServices.value = if (current.contains("Suministro de gas")) {
                                                current - "Suministro de gas"
                                            } else {
                                                current + "Suministro de gas"
                                            }
                                        }
                                    )

                                }
                            }

                            Column(
                                modifier = Modifier
                                    .width(175.dp)
                                    .heightIn()
                                    .padding(start = 10.dp)
                                    .background(WhiteGray, shape = RoundedCornerShape(20.dp))
                            ) {
                                Column(
                                    modifier = Modifier.padding(
                                        start = 10.dp,
                                        top = 20.dp,
                                        bottom = 20.dp
                                    ),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Servicios para mascotas",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = BlackGray
                                    )

                                    ButtonService(
                                        text = "Se admiten mascotas",
                                        iconRes = R.drawable.dog,
                                        tint = Color.White,
                                        color = MainColor,
                                        isSelected = selectedServices.value.contains("Se admiten mascotas"),
                                        onClick = {
                                            val current = selectedServices.value
                                            selectedServices.value = if (current.contains("Se admiten mascotas")) {
                                                current - "Se admiten mascotas"
                                            } else {
                                                current + "Se admiten mascotas"
                                            }
                                        }
                                    )
                                    ButtonService(
                                        text = "Instalacioens para perros",
                                        iconRes = R.drawable.dog_friendly,
                                        tint = Color.White,
                                        color = MainColor,
                                        isSelected = selectedServices.value.contains("Instalaciones para perros"),
                                        onClick = {
                                            val current = selectedServices.value
                                            selectedServices.value = if (current.contains("Instalaciones para perros")) {
                                                current - "Instalaciones para perros"
                                            } else {
                                                current + "Instalaciones para perros"
                                            }
                                        }
                                    )


                                }
                            }
                        }

                        Column(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            Column(
                                modifier = Modifier
                                    .width(175.dp)
                                    .heightIn()
                                    .padding(start = 10.dp)
                                    .background(WhiteGray, shape = RoundedCornerShape(20.dp))
                            ) {
                                Column(
                                    modifier = Modifier.padding(
                                        start = 10.dp,
                                        top = 20.dp,
                                        bottom = 20.dp
                                    ),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Instalaciones",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = BlackGray
                                    )

                                    ButtonService(
                                        text = "Piscina",
                                        iconRes = R.drawable.pool,
                                        tint = Color.White,
                                        color = MainColor,
                                        isSelected = selectedServices.value.contains("Piscina"),
                                        onClick = {
                                            val current = selectedServices.value
                                            selectedServices.value = if (current.contains("Piscina")) {
                                                current - "Piscina"
                                            } else {
                                                current + "Piscina"
                                            }
                                        }
                                    )

                                    ButtonService(
                                        text = "Zona Infantil",
                                        iconRes = R.drawable.infantil_zone,
                                        tint = Color.White,
                                        color = MainColor,
                                        isSelected = selectedServices.value.contains("Zona Infantil"),
                                        onClick = {
                                            val current = selectedServices.value
                                            selectedServices.value = if (current.contains("Zona Infantil")) {
                                                current - "Zona Infantil"
                                            } else {
                                                current + "Zona Infantil"
                                            }
                                        }
                                    )

                                    ButtonService(
                                        text = "Cafetería",
                                        iconRes = R.drawable.bar,
                                        tint = Color.White,
                                        color = MainColor,
                                        isSelected = selectedServices.value.contains("Cafetería"),
                                        onClick = {
                                            val current = selectedServices.value
                                            selectedServices.value = if (current.contains("Cafetería")) {
                                                current - "Cafetería"
                                            } else {
                                                current + "Cafetería"
                                            }
                                        }
                                    )

                                    ButtonService(
                                        text = "Restaurante",
                                        iconRes = R.drawable.restaurant,
                                        tint = Color.White,
                                        color = MainColor,
                                        isSelected = selectedServices.value.contains("Restaurante"),
                                        onClick = {
                                            val current = selectedServices.value
                                            selectedServices.value = if (current.contains("Restaurante")) {
                                                current - "Restaurante"
                                            } else {
                                                current + "Restaurante"
                                            }
                                        }
                                    )
                                    ButtonService(
                                        text = "Zona de barbacoa",
                                        iconRes = R.drawable.barbacoa,
                                        tint = Color.White,
                                        color = MainColor,
                                        isSelected = selectedServices.value.contains("Zona de barbacoa"),
                                        onClick = {
                                            val current = selectedServices.value
                                            selectedServices.value = if (current.contains("Zona de barbacoa")) {
                                                current - "Zona de barbacoa"
                                            } else {
                                                current + "Zona de barbacoa"
                                            }
                                        }
                                    )
                                    ButtonService(
                                        text = "Supermercado",
                                        iconRes = R.drawable.supermarket,
                                        tint = Color.White,
                                        color = MainColor,
                                        isSelected = selectedServices.value.contains("Supermercado"),
                                        onClick = {
                                            val current = selectedServices.value
                                            selectedServices.value = if (current.contains("Supermercado")) {
                                                current - "Supermercado"
                                            } else {
                                                current + "Supermercado"
                                            }
                                        }
                                    )
                                }
                            }

                            Column(
                                modifier = Modifier
                                    .width(175.dp)
                                    .heightIn()
                                    .padding(start = 10.dp)
                                    .background(WhiteGray, shape = RoundedCornerShape(20.dp))
                            ) {
                                Column(
                                    modifier = Modifier.padding(
                                        start = 10.dp,
                                        top = 20.dp,
                                        bottom = 20.dp
                                    ),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Alojamiento",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = BlackGray
                                    )

                                    ButtonService(
                                        text = "Parcelas de larga estancia",
                                        iconRes = R.drawable.long_stance,
                                        tint = Color.White,
                                        color = MainColor,
                                        isSelected = selectedServices.value.contains("Parcelas de larga estancia"),
                                        onClick = {
                                            val current = selectedServices.value
                                            selectedServices.value = if (current.contains("Parcelas de larga estancia")) {
                                                current - "Parcelas de larga estancia"
                                            } else {
                                                current + "Parcelas de larga estancia"
                                            }
                                        }
                                    )

                                    ButtonService(
                                        text = "Bungalows",
                                        iconRes = R.drawable.bungalow,
                                        tint = Color.White,
                                        color = MainColor,
                                        isSelected = selectedServices.value.contains("Bungalows"),
                                        onClick = {
                                            val current = selectedServices.value
                                            selectedServices.value = if (current.contains("Bungalows")) {
                                                current - "Bungalows"
                                            } else {
                                                current + "Bungalows"
                                            }
                                        }
                                    )

                                    ButtonService(
                                        text = "Cabañas",
                                        iconRes = R.drawable.cabain,
                                        tint = Color.White,
                                        color = MainColor,
                                        isSelected = selectedServices.value.contains("Cabañas"),
                                        onClick = {
                                            val current = selectedServices.value
                                            selectedServices.value = if (current.contains("Cabañas")) {
                                                current - "Cabañas"
                                            } else {
                                                current + "Cabañas"
                                            }
                                        }
                                    )

                                    ButtonService(
                                        text = "Zona de acampada",
                                        iconRes = R.drawable.camping_zone,
                                        tint = Color.White,
                                        color = MainColor,
                                        isSelected = selectedServices.value.contains("Zona de acampada"),
                                        onClick = {
                                            val current = selectedServices.value
                                            selectedServices.value = if (current.contains("Zona de acampada")) {
                                                current - "Zona de acampada"
                                            } else {
                                                current + "Zona de acampada"
                                            }
                                        }
                                    )

                                }
                            }

                            Column(
                                modifier = Modifier
                                    .width(175.dp)
                                    .heightIn()
                                    .padding(start = 10.dp)
                                    .background(WhiteGray, shape = RoundedCornerShape(20.dp))
                            ) {
                                Column(
                                    modifier = Modifier.padding(
                                        start = 10.dp,
                                        top = 20.dp,
                                        bottom = 20.dp
                                    ),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Otros servicios",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = BlackGray
                                    )

                                    ButtonService(
                                        text = "Alquiler de bicicletas",
                                        iconRes = R.drawable.bike_rent,
                                        tint = Color.White,
                                        color = MainColor,
                                        isSelected = selectedServices.value.contains("Alquiler de bicicletas"),
                                        onClick = {
                                            val current = selectedServices.value
                                            selectedServices.value = if (current.contains("Alquiler de bicicletas")) {
                                                current - "Alquiler de bicicletas"
                                            } else {
                                                current + "Alquiler de bicicletas"
                                            }
                                        }
                                    )

                                    ButtonService(
                                        text = "Actividades infantiles",
                                        iconRes = R.drawable.infantil_activities,
                                        tint = Color.White,
                                        color = MainColor,
                                        isSelected = selectedServices.value.contains("Actividades infantiles"),
                                        onClick = {
                                            val current = selectedServices.value
                                            selectedServices.value = if (current.contains("Actividades infantiles")) {
                                                current - "Actividades infantiles"
                                            } else {
                                                current + "Actividades infantiles"
                                            }
                                        }
                                    )

                                    ButtonService(
                                        text = "Actividades acuáticas",
                                        iconRes = R.drawable.acuatic_activities,
                                        tint = Color.White,
                                        color = MainColor,
                                        isSelected = selectedServices.value.contains("Actividades acuáticas"),
                                        onClick = {
                                            val current = selectedServices.value
                                            selectedServices.value = if (current.contains("Actividades acuáticas")) {
                                                current - "Actividades acuáticas"
                                            } else {
                                                current + "Actividades acuáticas"
                                            }
                                        }
                                    )

                                    ButtonService(
                                        text = "Excursiones guiadas",
                                        iconRes = R.drawable.guide_excursions,
                                        tint = Color.White,
                                        color = MainColor,
                                        isSelected = selectedServices.value.contains("Excursiones guiadas"),
                                        onClick = {
                                            val current = selectedServices.value
                                            selectedServices.value = if (current.contains("Excursiones guiadas")) {
                                                current - "Excursiones guiadas"
                                            } else {
                                                current + "Excursiones guiadas"
                                            }
                                        }
                                    )

                                }
                            }

                        }

                    }


                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(51.dp),

                        shape = RoundedCornerShape(20.dp),
                        color = MainColor,
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,

                            ) {


                            Text(
                                text = "Siguiente",
                                modifier = Modifier.padding(top = 6.dp),
                                fontSize = 14.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                maxLines = 3,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                }


            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(top = 16.dp, start = 20.dp, end = 20.dp, bottom = 20.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Bottom
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(51.dp)
                        .clickable {
                            scope.launch {
                                offsetY.animateTo(600f, animationSpec = tween(300))
                                onConfirm(selectedServices.value)
                            }
                        },

                    shape = RoundedCornerShape(20.dp),
                    color = MainColor,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,

                        ) {


                        Text(
                            text = "Siguiente",
                            modifier = Modifier.padding(top = 6.dp),
                            fontSize = 14.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            maxLines = 3,
                            textAlign = TextAlign.Center
                        )
                    }
                }

            }
        }
    }
}

@Composable
fun MapNewImageServiceMenu(
    selectedPoint: LatLng?,
    selectedAddress: String?,
    onNameConfirmed: String?,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onImagesSelected: (List<Uri>) -> Unit
) {
    val scope = rememberCoroutineScope()
    val offsetY = remember { Animatable(600f) }
    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri>? ->
        uris?.let {
            if (it.size <= 10) {
                onImagesSelected(it)
            } else {
                onImagesSelected(it.take(10))
            }
        }
    }
    var cameraUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            cameraUri?.let { uri ->
                onImagesSelected(listOf(uri))
            }
        }
    }

    val cameraPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                val file = File(context.cacheDir, "camera_photo.jpg")
                val uri = FileProvider.getUriForFile(context, "com.vango.fileprovider", file)
                cameraUri = uri
                cameraLauncher.launch(uri)
            } else {
                // Manejar permiso denegado (puedes mostrar un mensaje)
            }
        }


    LaunchedEffect(Unit) {
        offsetY.animateTo(0f, animationSpec = tween(300))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxHeight()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn()
                .align(Alignment.TopStart)
                .offset(y = offsetY.value.dp),
            shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
            color = Color.White,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 55.dp, start = 20.dp, end = 20.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Surface(
                            color = BackgroundColorList,
                            modifier = Modifier.size(32.dp),
                            shape = RoundedCornerShape(11.dp),
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.arrow_back),
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

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Sube fotos de este punto",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = BlackGray
                            )
                        }


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

                }


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sería de gran ayuda para la comunidad, que pudieras subir algunas fotos de este punto.",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = BlackGray,
                        textAlign = TextAlign.Center
                    )
                }

                Box(
                    modifier = Modifier
                        .background(Color.Transparent, shape = RoundedCornerShape(20.dp))
                        .fillMaxWidth()
                        .height(149.dp),
                    contentAlignment = Alignment.Center

                ) {

                    Column(
                        modifier = Modifier
                            .padding(start = 20.dp, end = 20.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        MapNewRoutePointButton(
                            text = "Añadir fotos",
                            iconRes = R.drawable.plus,
                            tint = Color.White,
                            color = MainColor,
                            onClick = {
                                galleryLauncher.launch("image/*")
                            }
                        )

                        Spacer(
                            modifier = Modifier.height(13.dp),
                        )

                        MapNewRoutePointButton(
                            text = "Hacer fotos",
                            iconRes = R.drawable.camera,
                            tint = Color.White,
                            color = BackgroundButtonColor,
                            onClick = { cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA) }
                        )
                    }

                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "¿Tienes dudas de qué fotos subir?",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = BlackGray,
                        textAlign = TextAlign.Center
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Echa un vistazo a nusetras normas y\nrecomendaciones",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MainColor,
                        textAlign = TextAlign.Center,
                        textDecoration = TextDecoration.Underline
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(top = 16.dp, start = 20.dp, end = 20.dp, bottom = 20.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Bottom
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(51.dp),

                    shape = RoundedCornerShape(20.dp),
                    color = MainColor,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,

                        ) {


                        Text(
                            text = "Subir imágenes",
                            modifier = Modifier.padding(top = 6.dp),
                            fontSize = 14.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            maxLines = 3,
                            textAlign = TextAlign.Center
                        )
                    }
                }

            }
        }
    }
}


@Composable
fun MapNewLastDatesMenu(
    selectedPoint: LatLng?,
    selectedAddress: String?,
    onNameConfirmed: (String) -> Unit,
    onDismiss: () -> Unit,
    viewModel: HomeViewModel
) {
    val scope = rememberCoroutineScope()
    val offsetY = remember { Animatable(600f) }
    var textValue by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        offsetY.animateTo(0f, animationSpec = tween(300))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxHeight()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn()
                .align(Alignment.TopStart)
                .offset(y = offsetY.value.dp),
            shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
            color = Color.White,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 55.dp, start = 20.dp, end = 20.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Surface(
                            color = BackgroundColorList,
                            modifier = Modifier.size(32.dp),
                            shape = RoundedCornerShape(11.dp),
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.arrow_back),
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

                }

                Box(
                    modifier = Modifier
                        .background(WhiteGray, shape = RoundedCornerShape(20.dp))
                        .fillMaxWidth()
                        .heightIn(),
                    contentAlignment = Alignment.TopStart
                ) {
                    Column(
                        modifier = Modifier
                            .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 20.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Contacto del lugar",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MainColor
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        CustomDropdown()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CustomCheckBox(
                                isChecked = false,
                                onCheckedChange = {}
                            )


                            Surface(
                                color = MainColor,
                                modifier = Modifier.size(26.dp),
                                shape = RoundedCornerShape(8.dp),
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.plus),
                                        contentDescription = "Cerrar",
                                        modifier = Modifier
                                            .width(12.5.dp)
                                            .height(14.29.dp)
                                            .clickable {
                                                scope.launch {
                                                    offsetY.animateTo(
                                                        600f,
                                                        animationSpec = tween(300)
                                                    )
                                                    onDismiss()
                                                }
                                            },
                                        tint = Color.White
                                    )

                                }
                            }


                        }


                    }
                }

                Box(
                    modifier = Modifier
                        .background(WhiteGray, shape = RoundedCornerShape(20.dp))
                        .fillMaxWidth()
                        .heightIn(),
                    contentAlignment = Alignment.TopStart
                ) {
                    Column(
                        modifier = Modifier
                            .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 20.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Precio por día",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MainColor
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CustomCheckBox(
                                isChecked = false,
                                onCheckedChange = {}
                            )

                            Row(
                                modifier = Modifier
                                    .width(113.dp)
                                    .height(33.dp)
                                    .background(Color.White, shape = RoundedCornerShape(12.dp))
                                    .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                BasicTextField(
                                    value = textValue,
                                    onValueChange = { textValue = it },
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = 4.dp),
                                    textStyle = TextStyle(
                                        fontSize = 12.sp,
                                        color = BlackGray
                                    )
                                )
                                Text(
                                    text = "Eur",
                                    color = BlackGray,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(start = 4.dp, end = 4.dp)
                                )
                            }

                        }


                    }
                }

                Box(
                    modifier = Modifier
                        .background(WhiteGray, shape = RoundedCornerShape(20.dp))
                        .fillMaxWidth()
                        .heightIn(),
                    contentAlignment = Alignment.TopStart
                ) {

                    Column(
                        modifier = Modifier
                            .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 20.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Horario",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MainColor
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            CustomCheckBoxDays(
                                isChecked = false,
                                day = "L",
                                onCheckedChange = {}
                            )
                            CustomCheckBoxDays(
                                isChecked = false,
                                day = "M",
                                onCheckedChange = {}
                            )
                            CustomCheckBoxDays(
                                isChecked = false,
                                day = "M",
                                onCheckedChange = {}
                            )
                            CustomCheckBoxDays(
                                isChecked = false,
                                day = "J",
                                onCheckedChange = {}
                            )
                            CustomCheckBoxDays(
                                isChecked = false,
                                day = "V",
                                onCheckedChange = {}
                            )
                            CustomCheckBoxDays(
                                isChecked = false,
                                day = "S",
                                onCheckedChange = {}
                            )
                            CustomCheckBoxDays(
                                isChecked = false,
                                day = "D",
                                onCheckedChange = {}
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CustomCheckBox(
                                isChecked = false,
                                onCheckedChange = {}
                            )
                            CustomCheckBox(
                                isChecked = false,
                                onCheckedChange = {}
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {

                            CustomDropdownHour()
                            CustomDropdownHour()

                            Surface(
                                color = MainColor,
                                modifier = Modifier.size(26.dp),
                                shape = RoundedCornerShape(8.dp),
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.plus),
                                        contentDescription = "Cerrar",
                                        modifier = Modifier
                                            .width(12.5.dp)
                                            .height(14.29.dp)
                                            .clickable {
                                                scope.launch {
                                                    offsetY.animateTo(
                                                        600f,
                                                        animationSpec = tween(300)
                                                    )
                                                    onDismiss()
                                                }
                                            },
                                        tint = Color.White
                                    )

                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Meses de apertura",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MainColor
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {

                            CustomDropdownHour()
                            CustomDropdownHour()

                            Surface(
                                color = MainColor,
                                modifier = Modifier.size(26.dp),
                                shape = RoundedCornerShape(8.dp),
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.plus),
                                        contentDescription = "Cerrar",
                                        modifier = Modifier
                                            .width(12.5.dp)
                                            .height(14.29.dp)
                                            .clickable {
                                                scope.launch {
                                                    offsetY.animateTo(
                                                        600f,
                                                        animationSpec = tween(300)
                                                    )
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
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CustomCheckBox(
                                isChecked = false,
                                onCheckedChange = {}
                            )
                            CustomCheckBox(
                                isChecked = false,
                                onCheckedChange = {}
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(top = 16.dp, start = 20.dp, end = 20.dp, bottom = 20.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Bottom
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(51.dp)
                        .clickable {
                            scope.launch {
                                selectedPoint?.let { point ->
                                    viewModel.saveNewPointToApi(
                                        latitude = point.latitude,
                                        longitude = point.longitude,
                                        address = selectedAddress ?: "Unknown address"
                                    )
//                                    viewModel.updateCurrentLocation(point)
                                }
                                offsetY.animateTo(600f, animationSpec = tween(300))
                                onDismiss()
                            }
                        },

                    shape = RoundedCornerShape(20.dp),
                    color = MainColor,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Saltar y crear punto",
                            modifier = Modifier.padding(top = 6.dp),
                            fontSize = 14.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            maxLines = 3,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(51.dp)
                        .clickable {
                            scope.launch {
                                selectedPoint?.let { point ->
                                    viewModel.saveNewPointToApi(
                                        latitude = point.latitude,
                                        longitude = point.longitude,
                                        address = selectedAddress ?: "Unknown address",
                                        pricePerDay = textValue.takeIf { it.isNotEmpty() }?.toDoubleOrNull()
                                    )
//                                    viewModel.updateCurrentLocation(point)
                                }
                                offsetY.animateTo(600f, animationSpec = tween(300))
                                onDismiss()
                            }
                        },

                    shape = RoundedCornerShape(20.dp),
                    color = MainColor,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,

                        ) {


                        Text(
                            text = "Guardar datos y crear punto",
                            modifier = Modifier.padding(top = 6.dp),
                            fontSize = 14.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            maxLines = 3,
                            textAlign = TextAlign.Center
                        )
                    }
                }

            }
        }
    }
}


@Composable
fun MapNewImageServiceUploadMenu(
    selectedPoint: LatLng?,
    selectedAddress: String?,
    onNameConfirmed: String?,
    initialImages: List<Uri> = emptyList(),
    onDismiss: () -> Unit,
    onConfirm: (List<String>) -> Unit,
    viewModel: HomeViewModel
) {
    val scope = rememberCoroutineScope()
    val offsetY = remember { Animatable(600f) }
    val context = LocalContext.current
    var images by remember { mutableStateOf(initialImages) }
    var cameraUri by remember { mutableStateOf<Uri?>(null) }
    var imageUrls by remember { mutableStateOf<List<String>>(emptyList()) }
    var isUploading by remember { mutableStateOf(false) }

    var uploadProgress by remember { mutableStateOf<Map<Uri, Float>>(emptyMap()) }

    val storage = FirebaseStorage.getInstance()
    val storageRef = storage.reference

    suspend fun uploadImagesToFirebase(images: List<Uri>): List<String> {
        val urls = mutableListOf<String>()
        images.forEach { uri ->
            val fileName =
                "point_${selectedPoint?.latitude}_${selectedPoint?.longitude}_${System.currentTimeMillis()}.jpg"
            val imageRef: StorageReference = storageRef.child("images/$fileName")
            try {
                val uploadTask = imageRef.putFile(uri)
                uploadTask.addOnProgressListener { snapshot ->
                    val progress = (100.0 * snapshot.bytesTransferred / snapshot.totalByteCount).toFloat()
                    uploadProgress = uploadProgress + (uri to progress)
                }
                uploadTask.await()
                val downloadUrl = imageRef.downloadUrl.await().toString()
                urls.add(downloadUrl)
                uploadProgress = uploadProgress + (uri to 100f)
                delay(500)
                uploadProgress = uploadProgress - uri
            } catch (e: Exception) {
                Log.e("FirebaseUpload", "Error uploading image: ${e.message}")
            }
        }
        return urls
    }

    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            uris?.let {
                val newImages = (images + it).take(10)
                images = newImages
                scope.launch {
                    val newUris = it.filter { uri -> !imageUrls.any { url -> url.contains(uri.toString()) } }
                    val urls = uploadImagesToFirebase(newUris)
                    imageUrls = imageUrls + urls
                }
            }
        }
    val cameraLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                cameraUri?.let { uri ->
                    val newImages = (images + uri).take(10)
                    images = newImages
                    scope.launch {
                        val urls = uploadImagesToFirebase(listOf(uri))
                        imageUrls = imageUrls + urls
                    }
                }
            }
        }

    val cameraPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                val file = File(context.cacheDir, "camera_photo_${System.currentTimeMillis()}.jpg")
                val uri = FileProvider.getUriForFile(context, "com.vango.fileprovider", file)
                cameraUri = uri
                cameraLauncher.launch(uri)
            }
        }

    LaunchedEffect(initialImages) {
        if (initialImages.isNotEmpty() && imageUrls.isEmpty()) {
            isUploading = true
            val urls = uploadImagesToFirebase(initialImages)
            imageUrls = urls
            isUploading = false
        }
    }



//    suspend fun uploadImagesToFirebase(images: List<Uri>): List<String> {
//        val urls = mutableListOf<String>()
//        images.forEach { uri ->
//            val fileName =
//                "point_${selectedPoint?.latitude}_${selectedPoint?.longitude}_${System.currentTimeMillis()}.jpg"
//            val imageRef: StorageReference = storageRef.child("images/$fileName")
//            try {
//                val uploadTask = imageRef.putFile(uri)
//                uploadTask.addOnProgressListener { snapshot ->
//                    val progress = (100.0 * snapshot.bytesTransferred / snapshot.totalByteCount).toFloat()
//                    uploadProgress = uploadProgress + (uri to progress)
//                }
//                uploadTask.await()
//                val downloadUrl = imageRef.downloadUrl.await().toString()
//                urls.add(downloadUrl)
//                uploadProgress = uploadProgress + (uri to 100f)
//                delay(500)
//                uploadProgress = uploadProgress - uri
//            } catch (e: Exception) {
//                Log.e("FirebaseUpload", "Error uploading image: ${e.message}")
//            }
//        }
//        return urls
//    }

    LaunchedEffect(Unit) {
        offsetY.animateTo(0f, animationSpec = tween(300))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxHeight()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .align(Alignment.TopStart)
                .offset(y = offsetY.value.dp),
            shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
            color = Color.White
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 55.dp, start = 20.dp, end = 20.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = BackgroundColorList,
                            modifier = Modifier.size(32.dp),
                            shape = RoundedCornerShape(11.dp),
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.arrow_back),
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
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Sube fotos de este punto",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = BlackGray
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Sería de gran ayuda para la comunidad, que pudieras subir algunas fotos de este punto.",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = BlackGray,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                item {
                    Box(
                        modifier = Modifier
                            .background(BackgroundColorImage, shape = RoundedCornerShape(20.dp))
                            .fillMaxWidth()
                            .height(244.dp)
                    ) {
                        if (images.isNotEmpty()) {
                            Image(
                                painter = rememberAsyncImagePainter(images[0]),
                                contentDescription = "Foto de portada",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        BackgroundColorImage,
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                    .clip(RoundedCornerShape(20.dp))
                            )
                            uploadProgress[images[0]]?.let { progress ->
                                LinearProgressIndicator(
                                    progress = { progress / 100f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
                                        .height(8.dp)
                                        .align(Alignment.BottomCenter),
                                    color = Color.White,
                                    trackColor = Color.Gray.copy(alpha = 0.3f)
                                )
                            }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 12.dp, end = 12.dp, top = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Surface(
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(28.dp),
                                shape = RoundedCornerShape(8.dp),
                                color = Color.White
                            ) {
                                Text(
                                    text = "Foto de portada",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = BlackGray,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.wrapContentSize()
                                )
                            }

                            Surface(
                                color = Color.White,
                                modifier = Modifier.size(32.dp),
                                shape = RoundedCornerShape(11.dp),
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center,
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.points_more),
                                        contentDescription = "Eliminar",
                                        modifier = Modifier
                                            .width(12.5.dp)
                                            .height(14.29.dp)
                                            .clickable {
                                                if (images.isNotEmpty()) {
                                                    images = images.drop(1)
                                                }
                                            },
                                        tint = BlackGray
                                    )
                                }
                            }
                        }
                    }
                }

                items(images.drop(1).chunked(2)) { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        pair.forEach { uri ->
                            Box(
                                modifier = Modifier
                                    .background(
                                        BackgroundColorImage,
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                    .width(170.dp)
                                    .height(132.dp)
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(uri),
                                    contentDescription = "Foto adicional",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            BackgroundColorImage,
                                            shape = RoundedCornerShape(20.dp)
                                        )
                                        .clip(RoundedCornerShape(20.dp))
                                )

                                uploadProgress[uri]?.let { progress ->
                                    LinearProgressIndicator(
                                        progress = { progress / 100f },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
                                            .height(4.dp)
                                            .align(Alignment.BottomCenter),
                                        color = Color.White,
                                        trackColor = Color.Gray.copy(alpha = 0.3f)
                                    )
                                }

                                Surface(
                                    color = Color.White,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .align(Alignment.TopEnd)
                                        .padding(top = 10.dp, end = 10.dp),
                                    shape = RoundedCornerShape(6.dp),

                                    ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center,
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.points_more),
                                            contentDescription = "Cerrar",
                                            modifier = Modifier
                                                .width(12.5.dp)
                                                .height(14.29.dp)
                                                .clickable {
                                                    val index = images.indexOf(uri)
                                                    if (index != -1) {
                                                        images = images.toMutableList()
                                                            .apply { removeAt(index) }
                                                    }
                                                },
                                            tint = BlackGray
                                        )
                                    }
                                }
                            }

                        }
                        if (pair.size < 2) {
                            Spacer(modifier = Modifier.width(170.dp))
                        }
                    }
                }

                if (images.size < 10) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(170.dp)
                                    .height(132.dp)
                                    .background(
                                        Color.Transparent,
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                    .drawBehind {
                                        val strokeWidth = 1.dp.toPx()
                                        val pathEffect =
                                            PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                                        drawRoundRect(
                                            color = Color.Gray,
                                            size = size,
                                            cornerRadius = CornerRadius(20.dp.toPx()),
                                            style = Stroke(
                                                width = strokeWidth,
                                                pathEffect = pathEffect
                                            )
                                        )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Surface(color = Color.White) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.camera),
                                            contentDescription = "Hacer fotos",
                                            modifier = Modifier
                                                .width(28.dp)
                                                .height(28.dp)
                                                .clickable {
                                                    cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                                                },
                                            tint = BlackGray
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Hacer fotos",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Normal,
                                            color = BlackGray,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .width(170.dp)
                                    .height(132.dp)
                                    .background(
                                        Color.Transparent,
                                        shape = RoundedCornerShape(20.dp)
                                    )
                                    .drawBehind {
                                        val strokeWidth = 1.dp.toPx()
                                        val pathEffect =
                                            PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                                        drawRoundRect(
                                            color = Color.Gray,
                                            size = size,
                                            cornerRadius = CornerRadius(20.dp.toPx()),
                                            style = Stroke(
                                                width = strokeWidth,
                                                pathEffect = pathEffect
                                            )
                                        )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Surface(color = Color.White) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.plus),
                                            contentDescription = "Añadir fotos",
                                            modifier = Modifier
                                                .width(28.dp)
                                                .height(28.dp)
                                                .clickable {
                                                    galleryLauncher.launch("image/*")
                                                },
                                            tint = BlackGray
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Añadir fotos",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Normal,
                                            color = BlackGray,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "¿Tienes dudas de qué fotos subir?",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal,
                            color = BlackGray,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Echa un vistazo a nuestras normas y\nrecomendaciones",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MainColor,
                            textAlign = TextAlign.Center,
                            textDecoration = TextDecoration.Underline
                        )
                    }
                }

                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(51.dp)
                            .clickable(
                                enabled = !isUploading
                            ) {
                                scope.launch {
                                    viewModel.setPhotoUrls(imageUrls)
                                    offsetY.animateTo(600f, animationSpec = tween(300))
                                    onConfirm(imageUrls)
                                    selectedPoint?.let { point ->
                                        viewModel.saveNewPointToApi(
                                            latitude = point.latitude,
                                            longitude = point.longitude,
                                            address = selectedAddress ?: "Unknown address"
                                        )
                                    }
                                    onDismiss()
                                }
                            },
                        shape = RoundedCornerShape(20.dp),
                        color = if (isUploading) Color.Gray else MainColor
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Confirmar",
                                modifier = Modifier.padding(top = 6.dp),
                                fontSize = 14.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                maxLines = 3,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinearProgressIndicator(
    modifier: Modifier = Modifier,
    color: Color = ProgressIndicatorDefaults.linearColor,
    trackColor: Color = ProgressIndicatorDefaults.linearTrackColor,
    strokeCap: StrokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
    gapSize: Dp = ProgressIndicatorDefaults.LinearIndicatorTrackGapSize,
){

}

@Composable
fun MapNewRoutePointButton(
    text: String,
    iconRes: Int,
    tint: Color,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Surface(
            onClick = onClick,
            modifier = Modifier
                .width(252.dp)
                .height(51.dp),

            shape = RoundedCornerShape(20.dp),
            color = color,
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,

                ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = text,
                    modifier = Modifier.size(36.dp),
                    tint = tint
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = text,
                    modifier = Modifier.padding(top = 6.dp),
                    fontSize = 12.sp,
                    color = Color.White,
                    maxLines = 3,
                    textAlign = TextAlign.Center
                )
            }
        }

    }
}


@Composable
fun ButtonCategory(
    text: String,
    iconRes: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .width(64.dp)
            .height(64.dp)
            .border(1.dp, Color.LightGray, shape = RoundedCornerShape(16.dp))
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) YellowMelow else Color.White,
    ) {
        Column(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = text,
                modifier = Modifier.size(25.dp),
                tint = if (isSelected) Color.White else BlackGray
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = text,
                    modifier = Modifier.padding(top = 3.dp),
                    fontSize = 10.sp,
                    maxLines = 2,
                    color = if (isSelected) Color.White else BlackGray
                )
            }

        }
    }
}

@Composable
fun ButtonService(
    text: String,
    iconRes: Int,
    tint: Color,
    color: Color,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,

        ) {
        Surface(
            onClick = onClick,
            modifier = Modifier
                .width(25.dp)
                .height(25.dp),
            shape = RoundedCornerShape(8.dp),
            border = if(isSelected) BorderStroke(3.dp, YellowMelow) else BorderStroke(1.dp, Color.LightGray),
            color = Color.White,
        ) {
            Row(
                modifier = Modifier.padding(4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = text,
                    modifier = Modifier.size(25.dp),
                    tint = if(isSelected) YellowMelow else BlackGray
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Row() {
            Text(
                text = text,
                modifier = Modifier.padding(top = 3.dp),
                fontSize = 10.sp,
                color = BlackGray
            )
        }

    }

}

enum class MapNewPointRoute {
    CREATE_ROUTE,
    CREATE_POINT

}

@Composable
fun CustomDropdown() {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("Selecciona una opción") }
    val opciones = listOf("Opción 1", "Opción 2", "Opción 3")

    var dropdownWidth by remember { mutableStateOf(0) }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { layoutCoordinates ->
                    dropdownWidth = layoutCoordinates.size.width
                }
                .clickable { expanded = true }
                .border(
                    1.dp,
                    Color.LightGray,
                    shape = if (expanded == true) RoundedCornerShape(
                        topEnd = 14.dp,
                        topStart = 14.dp,
                        bottomEnd = 0.dp,
                        bottomStart = 0.dp
                    ) else RoundedCornerShape(14.dp)
                )
                .background(
                    Color.White,
                    shape = if (expanded == true) RoundedCornerShape(
                        topEnd = 14.dp,
                        topStart = 14.dp,
                        bottomEnd = 0.dp,
                        bottomStart = 0.dp
                    ) else RoundedCornerShape(14.dp)
                )
                .padding(14.dp),
            color = Color.White,
            shape = if (expanded == true) RoundedCornerShape(
                topEnd = 14.dp,
                topStart = 14.dp,
                bottomEnd = 0.dp,
                bottomStart = 0.dp
            ) else RoundedCornerShape(14.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = selectedOption, color = Color.LightGray)
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Expandir"
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current) { dropdownWidth.toDp() })
                .clip(RoundedCornerShape(0.dp))
                .background(Color.White, shape = RoundedCornerShape(0.dp))
        ) {

            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(text = opcion, color = BlackGray) },
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        selectedOption = opcion
                        expanded = false
                    }
                )
            }


        }
    }
}

@Composable
fun CustomCheckBox(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(if (isChecked) MainColor else Color.White)
                .border(1.dp, Color.LightGray, RoundedCornerShape(6.dp))
                .clickable { onCheckedChange(!isChecked) },
            contentAlignment = Alignment.Center
        ) {
            if (isChecked) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Checked",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Text(
            text = "Sin contacto",
            modifier = Modifier
                .padding(12.dp),
            color = BlackGray
        )
    }

}

@Composable
fun CustomCheckBoxDays(
    isChecked: Boolean,
    day: String,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isChecked) MainColor else Color.White)
                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                .clickable { onCheckedChange(!isChecked) },
            contentAlignment = Alignment.Center
        ) {
            Text(text = day, color = BlackGray, fontSize = 12.sp)
        }
    }
}

@Composable
fun CustomDropdownHour() {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("Hora Apertura") }
    val opciones = listOf("Opción 1", "Opción 2", "Opción 3")

    var dropdownWidth by remember { mutableStateOf(0) }

    Box(
        modifier = Modifier
            .width(135.dp)
            .height(33.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { layoutCoordinates ->
                    dropdownWidth = layoutCoordinates.size.width
                }
                .clickable { expanded = true }
                .border(1.dp, Color.LightGray, shape = RoundedCornerShape(12.dp))
                .background(Color.White, shape = RoundedCornerShape(12.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            color = Color.White,
            shape = RoundedCornerShape(14.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = selectedOption, color = Color.LightGray, fontSize = 10.sp)
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Expandir",
                    tint = Color.LightGray
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current) { dropdownWidth.toDp() })
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White, shape = RoundedCornerShape(14.dp))
        ) {

            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(text = opcion, color = BlackGray, fontSize = 10.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        selectedOption = opcion
                        expanded = false
                    }
                )
            }


        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun MapNewPointTagMenua() {
//    var nameInput by remember { mutableStateOf("Punto de prueba") }
//    MapNewImageServiceUploadMenu(
//        selectedPoint = LatLng(111.0, 1111.0),
//        selectedAddress = "test",
//        onNameConfirmed = "test",
//        onConfirm = {},
//        onDismiss = {},
//    )
//}