package com.vango.presentation.main.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vango.R
import com.vango.domain.model.SearchResult
import com.vango.presentation.theme.BlackGray

@Composable
fun SearchBar(
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
            .padding(top = 48.dp, start = 20.dp, end = 20.dp)
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

@Preview(showBackground = true)
@Composable
fun SearchBarPreview() {
    val mockResults = listOf(
        SearchResult("Madrid", 40.416775, -3.703790, "place_id_1", "Madrid, España", distanceMeters = 1000),
        SearchResult("Barcelona", 41.3851, 2.1734, "place_id_2", "Barcelona, España", distanceMeters = 700),
        SearchResult("Valencia", 39.4699, -0.3763, "place_id_3", "Valencia, España", distanceMeters = 3452)
    )

    SearchBar(
        searchQuery = "Mad",
        onSearchQueryChange = {},
        performSearch = {},
        searchResults = mockResults,
        onResultSelected = {},
        modifier = Modifier.fillMaxWidth()
    )
}

fun formatDistance(distanceMeters: Int?): String {
    if (distanceMeters == null) return ""

    return if (distanceMeters < 1000) {
        "$distanceMeters m"
    } else {
        val kilometers = distanceMeters / 1000.0
        String.format("%.1f km", kilometers)
    }
}