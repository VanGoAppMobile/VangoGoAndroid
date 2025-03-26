package com.vango.presentation.main

import MenuScreen
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.vango.R
import com.vango.presentation.base.BaseActivity
import com.vango.presentation.main.favorites.FavoritesScreen
import com.vango.presentation.main.home.HomeScreen
import com.vango.presentation.main.menu.changescreens.ChangeEmailScreen
import com.vango.presentation.main.menu.changescreens.ChangePasswordScreen
import com.vango.presentation.main.menu.deleteaccount.DeleteAccountScreen
import com.vango.presentation.main.menu.logout.LogoutScreen
import com.vango.presentation.main.menu.notification.NotificationScreen
import com.vango.presentation.main.menu.profile.ProfileScreen
import com.vango.presentation.main.menu.settings.SettingsScreen
import com.vango.presentation.main.menu.support.SupportScreen
import com.vango.presentation.main.results.HomeList
import com.vango.presentation.main.routes.RoutesScreen
import com.vango.presentation.main.travels.TravelsScreen
import com.vango.presentation.theme.BlackGray
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ActivityMain : BaseActivity() {
    private lateinit var viewModel: ActivityMainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        viewModel = ViewModelProvider(this)[ActivityMainViewModel::class.java]

        setContent {
            HomeContent(viewModel)
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeContent(viewModel: ActivityMainViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "home"
    var showMapLayersMenu by remember { mutableStateOf(false) }
    var showMapNewPointMenu by remember { mutableStateOf(false) }
    val navBarOffset = remember { Animatable(0f) }
    val navBarHeightPx = with(LocalDensity.current) { 85.dp.toPx() }

    LaunchedEffect(showMapLayersMenu || showMapNewPointMenu) {
        if (showMapLayersMenu || showMapNewPointMenu) {
            launch {
                navBarOffset.animateTo(
                    targetValue = navBarHeightPx,
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = EaseInOut
                    )
                )
            }

        } else {
            launch {
                navBarOffset.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(
                        durationMillis = 200,
                        easing = EaseInOut
                    )
                )
            }

        }
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentRoute = currentRoute,
                onItemSelected = { route ->
                    if (route != currentRoute) {
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                modifier = Modifier
                    .offset(y = navBarOffset.value.dp)
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets(0, 0, 0, 0))
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier
//                .padding(customPadding(innerPadding, systemBarsPadding))
                .fillMaxSize().background(Color.White)
        ) {
            composable("home") {
                HomeScreen(
                    modifier = Modifier.padding(innerPadding),
                    navController = navController,
                    isPreview = false,
                    onMapLayersMenuVisibilityChange = { isVisible ->
                        showMapLayersMenu = isVisible
                        showMapNewPointMenu = isVisible

                    }
                )
            }
            composable("routes") {
                RoutesScreen()
            }
            composable("travels") {
                TravelsScreen()
            }

            composable("results") {
                HomeList(navController)
            }
            composable("menu") {
               MenuScreen(navController
               )
            }
            // Componentes composables del menu

            composable("profile") {
                ProfileScreen(navController)
            }
            composable("notifications") {
                NotificationScreen(navController)
            }
            composable("favorites") {
                FavoritesScreen(navController)
            }
            composable("settings") {
                SettingsScreen(navController)
            }
            composable("support") {
                SupportScreen(navController)
            }
            composable("logout") {
                LogoutScreen(navController)
            }
            composable("change_password") {
                ChangePasswordScreen(navController)
            }
            composable("change_email") {
                ChangeEmailScreen(navController)
            }
            composable("log_out") {
                LogoutScreen( navController)
            }
            composable("delete_account") {
                DeleteAccountScreen(navController)
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val colorMain = Color(ContextCompat.getColor(context, R.color.color_emphasis))
    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .height(85.dp),
        tonalElevation = 0.dp,
        containerColor = Color(ContextCompat.getColor(context, R.color.white))
    ) {
        val iconColorUnselected = BlackGray

        NavigationBarItem(
            modifier = Modifier.wrapContentHeight(),
            icon = {
                Icon(
                    painter = if (currentRoute == "home" || currentRoute == "results") painterResource(id = R.drawable.home_fill) else painterResource(
                        id = R.drawable.home
                    ),
                    modifier = Modifier.size(25.dp),
                    contentDescription = "Inicio",
                    tint = if (currentRoute == "home" || currentRoute == "results") colorMain else iconColorUnselected
                )
            },
            label = {
                Text(
                    text = "Inicio",
                    fontSize = 9.sp,
                    fontWeight = if (currentRoute == "home" || currentRoute == "results") FontWeight.Bold else FontWeight.Normal,

                    color = if (currentRoute == "home" || currentRoute == "results") colorMain else iconColorUnselected

                )
            },
            selected = currentRoute == "home",
            onClick = { onItemSelected("home") },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            modifier = Modifier.wrapContentHeight(),
            icon = {
                Icon(
                    painter = if (currentRoute == "routes") painterResource(id = R.drawable.ruta_fill) else painterResource(
                        id = R.drawable.ruta
                    ),
                    modifier = Modifier.size(25.dp),
                    contentDescription = "Mis rutas",
                    tint = if (currentRoute == "routes") colorMain else iconColorUnselected
                )
            },
            label = {
                Text(
                    text = "Mis rutas",
                    fontWeight = if (currentRoute == "routes") FontWeight.Bold else FontWeight.Normal,

                    fontSize = 9.sp,
                    color = if (currentRoute == "routes") colorMain else iconColorUnselected
                )
            },
            selected = currentRoute == "routes",
            onClick = { onItemSelected("routes") },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            modifier = Modifier.wrapContentHeight(),

            icon = {
                Icon(
                    painter = if (currentRoute == "travels") painterResource(id = R.drawable.travel_fill) else painterResource(
                        id = R.drawable.travel
                    ),
                    modifier = Modifier.size(25.dp),
                    contentDescription = "Mis viajes",
                    tint = if (currentRoute == "travels") colorMain else iconColorUnselected
                )
            },
            label = {
                Text(
                    "Mis viajes",
                    fontSize = 9.sp,
                    fontWeight = if (currentRoute == "travels") FontWeight.Bold else FontWeight.Normal,

                    color = if (currentRoute == "travels") colorMain else iconColorUnselected
                )
            },
            selected = currentRoute == "travels",
            onClick = { onItemSelected("travels") },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent
            )
        )

        NavigationBarItem(
            modifier = Modifier.wrapContentHeight(),

            icon = {
                Icon(
                    painter = if (currentRoute == "menu") painterResource(id = R.drawable.menu_fill) else painterResource(
                        id = R.drawable.menu
                    ),
                    modifier = Modifier.size(25.dp),
                    contentDescription = "Menú",
                    tint = if (currentRoute == "menu"||currentRoute == "profile"||currentRoute == "notifications"||currentRoute == "favorites"||currentRoute == "settings"||currentRoute == "support"||currentRoute == "logout") colorMain else iconColorUnselected
                )
            },
            label = {
                Text(
                    text = "Menú",
                    fontSize = 9.sp,
                    fontWeight = if (currentRoute == "menu") FontWeight.Bold else FontWeight.Normal,
                    color = if (currentRoute == "menu"||currentRoute == "profile"||currentRoute == "notifications"||currentRoute == "favorites"||currentRoute == "settings"||currentRoute == "support"||currentRoute == "logout") colorMain else iconColorUnselected
                )
            },
            selected = currentRoute == "menu",
            onClick = { onItemSelected("menu") },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent
            )
        )
    }
}



@Preview(showBackground = true)
@Composable
fun BottomNavigationBarPreview() {
    BottomNavigationBar(
        currentRoute = "home",
        onItemSelected = { },
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets(0, 0, 0, 0))
    )
}

@Composable
private fun customPadding(
    innerPadding: PaddingValues,
    systemBarsPadding: PaddingValues
): PaddingValues {
    return PaddingValues(
        top = 0.dp,
        bottom = innerPadding.calculateBottomPadding(),
        start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
        end = innerPadding.calculateEndPadding(LocalLayoutDirection.current)
    )
}