import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.vango.R
import com.vango.presentation.theme.BackgroundButtonColor
import com.vango.presentation.theme.BackgroundColorButtonPrincipal
import com.vango.presentation.theme.BlackGray
import com.vango.presentation.theme.MainColor
import com.vango.presentation.theme.TextColor

@Composable
fun MenuScreen(
    navController: NavController
) {
    var profilePictureUri by remember { mutableStateOf<String?>(null) }
    val ImageSelectlauncher = rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            profilePictureUri = uri.toString()
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 55.dp, start = 20.dp, end = 20.dp)
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
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
            Text(
                text = "Menú",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextColor
            )
            Spacer(modifier = Modifier.width(40.dp))
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box() {
                if (profilePictureUri.isNullOrEmpty()) {
                    Image(
                        painter = painterResource(id = R.drawable.default_image_profile),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    AsyncImage(
                        model = profilePictureUri,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                IconButton(
                    onClick = { ImageSelectlauncher.launch("image/*") },
                    modifier = Modifier
                        .size(27.dp)
                        .align(Alignment.BottomEnd)
                )
                {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_change_profile_image),
                        contentDescription = "Editar",
                        tint = Color.Unspecified

                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Usuario123",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextColor
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Premium",
                fontWeight = FontWeight.ExtraBold,
                fontStyle = FontStyle.Italic,
                fontSize = 10.sp,
                color = BackgroundColorButtonPrincipal
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            val options = listOf(
                "Mi cuenta" to "profile",
                "Notificaciones" to "notifications",
                "Mis Favoritos" to "favorites",
                "Ajustes" to "settings",
                "Soporte" to "support",
                "Desconectarse" to "logout"
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 0.dp),
                thickness = 1.dp,
                color = Color.Gray.copy(alpha = 0.2f)
            )
            options.forEach { (title, route) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate(route) }
                        .padding(vertical = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = when (route) {
                            "profile" -> painterResource(id = R.drawable.ic_profile)
                            "notifications" -> painterResource(id = R.drawable.ic_notifications)
                            "favorites" -> painterResource(id = R.drawable.ic_favorites)
                            "settings" -> painterResource(id = R.drawable.ic_settings)
                            "support" -> painterResource(id = R.drawable.ic_support)
                            "logout" -> painterResource(id = R.drawable.ic_logout)
                            else -> painterResource(id = R.drawable.ic_profile)
                        },
                        contentDescription = null,
                        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(BlackGray),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        color = TextColor,
                        fontWeight = FontWeight.Normal
                    )

                }
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 0.dp),
                    thickness = 1.dp,
                    color = Color.Gray.copy(alpha = 0.2f)
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewUserProfileScreen() {
    val navController = rememberNavController()
    MenuScreen(
        navController = navController
    )
}