package com.vango.presentation.main.menu.profile

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.vango.R
import com.vango.presentation.base.BaseActivity
import com.vango.presentation.theme.BackgroundButtonColor
import com.vango.presentation.theme.BackgroundColorList
import com.vango.presentation.theme.MainColor
import com.vango.presentation.theme.StyledButton
import com.vango.presentation.theme.TextColor
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
    }
}

@Composable
fun ProfileScreen(navController: NavController) {
    var selectedSection by remember { mutableStateOf("mis_datos") }

    Scaffold(
        modifier = Modifier.padding(start = 20.dp, bottom =  80.dp, end = 20.dp, top = 17.dp).background(Color.White),
        containerColor = Color.White,
        content = { innerPadding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .heightIn()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        color = BackgroundColorList,
                        modifier = Modifier.size(40.dp),
                        shape = RoundedCornerShape(14.dp),
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_arrow),
                                contentDescription = "Atras",
                                modifier = Modifier
                                    .width(18.dp)
                                    .height(18.dp)
                                    .clickable {
                                        navController.popBackStack()
                                    }
                                    .rotate(180f),
                                tint = Color.White
                            )

                        }
                    }
                    Column(
                        Modifier
                            .widthIn()
                            .height(40.dp),
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = "Mi Cuenta",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextColor
                        )
                    }
                    Surface(
                        color = MainColor,
                        modifier = Modifier.size(40.dp),
                        shape = RoundedCornerShape(14.dp),
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ex),
                                contentDescription = "Cerrar",
                                modifier = Modifier
                                    .width(18.dp)
                                    .height(18.dp)
                                    .clickable {
                                        navController.navigate("home")
                                    },
                                tint = Color.White
                            )

                        }
                    }

                }
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StyledButton(
                        text = "Mis datos",
                        onClick = { selectedSection = "mis_datos" },
                        isSelected = selectedSection == "mis_datos",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    StyledButton(
                        text = "Premium",
                        onClick = { selectedSection = "premium" },
                        isSelected = selectedSection == "premium",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    StyledButton(
                        text = "Aportaciones",
                        onClick = { selectedSection = "aportaciones" },
                        isSelected = selectedSection == "aportaciones",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                when (selectedSection) {
                    "mis_datos" -> MyDataContent(navController = navController)
                    "premium" -> PremiumContent(navController = navController)
                    "aportaciones" -> ContributionsContent()
                }
            }
        }
    )
}


@Preview(showBackground = true)
@Composable
fun RoutesScreenPreview() {
    val navController = rememberNavController()
    ProfileScreen(
        navController = navController
    )
}