package com.example.phonebookapp.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.phonebookapp.data.Category
import com.example.phonebookapp.data.Item
import com.example.phonebookapp.ui.AppViewModelProvider
import com.example.phonebookapp.ui.navigation.NavigationDestination
import com.example.phonebookapp.ui.theme.PhoneBookAppTheme
import kotlinx.coroutines.launch

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = "Phone Book App"
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToItemEntry: () -> Unit,
    navigateToItemDetails: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
//    val homeUiState by viewModel.stateFlow.collectAsState()
    val homeUiState = viewModel.homeUiState

    val coroutineScope = rememberCoroutineScope()

//    coroutineScope.launch {
//        viewModel.deleteAllItems(homeUiState.itemList)
//    }
    Scaffold(topBar = {

        HomeTopAppBar(
            homeUiState = homeUiState,
            viewModel = viewModel,
            value = homeUiState.searchValue,
            onValueChange = viewModel::searchUpdate,
            onClickButton = {
                navigateToItemEntry()
            },
            updateAlphabetItemLists = viewModel::updateAlphabetItemLists,
            buttonIcon = Icons.Default.Add,
        )
    }) { innerPadding ->
        PeopleList(
            homeUiState.itemList, homeUiState.alphabetItemLists, onClick = {
                coroutineScope.launch {
                    viewModel.deleteAllItems(homeUiState.itemList)
                }
            }, navigateToItemUpdate = navigateToItemDetails,
//        onItemClick = {
//        },
            modifier = Modifier.padding(innerPadding)
        )
    }


}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    homeUiState: HomeUiState,
    viewModel: HomeViewModel,
    value: String,
    onValueChange: (String) -> Unit,
    onClickButton: () -> Unit = {},
    updateAlphabetItemLists: () -> Unit,
    buttonIcon: ImageVector? = null,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
//                modifier = Modifier.fillMaxWidth()
            ) {
//                Spacer(modifier = Modifier.width(Icons.Default.Add.defaultWidth))
//                Spacer(modifier = Modifier.weight(0.5f))
//                Icon(imageVector = Icons.Default.Home, contentDescription = null, modifier = Modifier.padding(4.dp))

                var expanded by remember { mutableStateOf(false) }
                OutlinedTextField(
                    value = value,
                    onValueChange = {
                        onValueChange(it)
                        updateAlphabetItemLists()
                    },
                    textStyle = MaterialTheme.typography.titleMedium,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search, contentDescription = null
                        )
                    },
                    singleLine = true,
                    modifier = Modifier
                        .padding(8.dp)
                        .widthIn(max = OutlinedTextFieldDefaults.MinWidth - 50.dp),
                    shape = MaterialTheme.shapes.large,
                )
//                Spacer(modifier = Modifier.weight(0.5f))
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
//                    Card(modifier = Modifier
//                        .clickable {}) {
                    val maxCategoryLength = Category.values().maxOf { it.toString().length }
                    Box(modifier = Modifier
                        .menuAnchor()
                        .widthIn(maxCategoryLength * 10.dp, maxCategoryLength * 10.dp)
                    ){PersonCategory(
                        text = homeUiState.categoryValue.toString(),
                        icon = Icons.Default.Person,
                        color = homeUiState.categoryValue.color,
                        modifier=Modifier.fillMaxWidth()

                        )}

//                    }
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }) {
                        for (category in Category.values()) {
                            DropdownMenuItem(
                                text = {
                                    PersonCategory(
                                        text = category.toString(),
                                        icon = Icons.Default.Person,
                                        color = category.color
                                    )
                                },
                                onClick = {
//                                    coroutineScope {  }.launch {
                                    viewModel.categoryUpdate(category)
                                    updateAlphabetItemLists()
                                    expanded = false
//                                    }
                                    updateAlphabetItemLists()
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                IconButton(onClick = onClickButton, modifier=Modifier.weight(1f)) {
                    Icon(
                        imageVector = buttonIcon ?: Icons.Filled.ArrowBack,
                        contentDescription = null
                    )
                }
            }
        },
//        modifier = Modifier.height(48.dp),
        scrollBehavior = scrollBehavior, colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    )
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PeopleList(
    itemList: List<Item>,
    alphabetItemLists: List<List<Item>>,
    onClick: () -> Unit = {},
    navigateToItemUpdate: (Int) -> Unit,
//    onItemClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column {

        val state: LazyListState = rememberLazyListState()

        if (alphabetItemLists.isEmpty()) {
            Text(
                text = "No items found",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            val sections = alphabetItemLists.map { it.first().name.uppercase().first().toString() }

            LazyColumn(
                state = state,
                modifier = modifier,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                sections.forEach { section ->
                    stickyHeader {
                        Row(
                        ) {
                            Text(
                                section,
                                style = MaterialTheme.typography.headlineLarge,
                                modifier = Modifier.padding(
                                    start = 16.dp, top = 4.dp, bottom = 4.dp
                                )
                            )
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
//                    items(items = itemList, key = { section + it.id }) {
//
//                        if (it.name.uppercase().first() == section[0]) {
//                            PersonRow(item = it, onItemClick = { navigateToItemUpdate(it.id) })
//                        }
//                    }
                    items(items = alphabetItemLists) { items ->
                        items.forEach {
                            if (it.name.uppercase().first() == section[0]) {
                                PersonRow(item = it, onItemClick = { navigateToItemUpdate(it.id) })
                            }
                        }
                    }
                }
            }
        }

//        }


    }
}

@Composable
private fun PersonRow(item: Item, onItemClick: () -> Unit) {
    Card(
        modifier = Modifier
//            .fillMaxWidth()
            .padding(start = 48.dp, end = 8.dp, top = 8.dp, bottom = 8.dp)
            .clickable(onClick = onItemClick),
//            .border(1.dp, Color.Gray, MaterialTheme.shapes.medium),
        shape = MaterialTheme.shapes.medium, colors = CardDefaults.cardColors(
            MaterialTheme.colorScheme.secondaryContainer,
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
//                .clickable(onClick = onItemClick),

//        horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            HomePersonIcon(item = item, size = 48.dp)
            val nameAndSurname = if (item.surname.isNotEmpty()) {
                "${item.name} ${item.surname}"
            } else {
                item.name
            }
            Column {
                Text(
                    text = if (nameAndSurname.length > 16) nameAndSurname.substring(0, 13)
                        .plus("...") else nameAndSurname,
                    style = MaterialTheme.typography.titleLarge,
                )//, modifier = Modifier.weight(1f))
                val number =
                    if (item.number.toString().length > 20) item.number.toString().substring(1, 20)
                        .plus("...") else item.number.toString()
                        .substring(1, item.number.toString().length - 1)

                Text(text = formatPhoneNumber(number), style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.weight(1f))
            if (item.category != "NONE") {
                PersonCategory(
                    text = item.category,
                    icon = Icons.Default.Person,
                    color = Category.valueOf(item.category).color,
                    modifier = Modifier.padding(end=16.dp)
                )
            }
        }
    }

}

fun formatPhoneNumber(phoneNumber: String): AnnotatedString {
    return buildAnnotatedString {
        val length = phoneNumber.length
        if (length < 9) {
            // Dla mniejszych numerów telefonów, możemy zdecydować się na prosty format bez dodatkowych znaków
            append(phoneNumber)
            return@buildAnnotatedString
        }
        // Dodajemy trzy pierwsze cyfry numeru telefonu
        withStyle(SpanStyle(textDecoration = TextDecoration.None)) {
            append(phoneNumber.substring(0, 3))
        }
        // Dodajemy spację
        withStyle(SpanStyle(textDecoration = TextDecoration.None)) {
            append(" ")
        }
        // Dodajemy trzy następne cyfry numeru telefonu
        withStyle(SpanStyle(textDecoration = TextDecoration.None)) {
            append(phoneNumber.substring(3, 6))
        }
        // Dodajemy myślnik i spację
        withStyle(SpanStyle(textDecoration = TextDecoration.None)) {
            append(" ")
        }
        // Dodajemy trzy ostatnie cyfry numeru telefonu
        withStyle(SpanStyle(textDecoration = TextDecoration.None)) {
            append(phoneNumber.substring(6, 9))
        }
        // Dodajemy spację i pozostałe cyfry numeru telefonu
        withStyle(SpanStyle(textDecoration = TextDecoration.None)) {
            append(phoneNumber.substring(9, length))
        }
    }
}

@Composable
fun PersonCategory(text: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        Image(
            contentDescription = "Category",
            imageVector = icon,
            modifier = Modifier.size(24.dp),
            colorFilter = ColorFilter.tint(color)
        )
//        Spacer(modifier = Modifier.padding(4.dp))
        Text(text = text.lowercase(), color = color)
    }
}


@Composable
fun HomePersonIcon(item: Item, size: Dp) {
    val (initial, color) = if (item.name.isNotEmpty() and (item.photo.toString().isEmpty())) {
        item.name.first().uppercaseChar().toString() to generateUniqueColor(
            item.id, item.name.first(), item.number.first().toString()
        )
    } else {
        "" to Color.Gray
    }
    Box(modifier = Modifier.padding(16.dp)) {
        Card(
            colors = CardDefaults.cardColors(
                color
            ),
            modifier = Modifier
                .size(size)
                .clip(CircleShape),


            ) {
            if (item.photo.toString().isNotEmpty()) {
                val image = item.photo

                val painter: Painter = rememberAsyncImagePainter(
                    model = image,
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,

//                    size = Size.ORIGINAL // Set the target size to load the image at.
                )
//                val painter2: Painter= rememberAsyncImagePainter(
//                    ImageRequest.Builder(LocalContext.current).data(data = item.photo).apply(block = fun ImageRequest.Builder.() {
//                        crossfade(true)
//                    }).build()
//                )

                Image(
                    painter = painter,
                    contentDescription = null,
//                    modifier = Modifier.size(size),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initial, style = MaterialTheme.typography.headlineLarge
                    )
                }
            }
        }
    }
}

fun generateUniqueColor(id: Int, initial: Char, number: String): Color {
    val red = (id.hashCode() * 17) % 128 + 128
    val green = (initial.hashCode() * 31) % 128 + 128
    val blue = (number.hashCode() * 47) % 128 + 128

    return Color(red = red / 255f, green = green / 255f, blue = blue / 255f)
}

@Preview()
@Composable
fun GreetingPreview() {
    PhoneBookAppTheme {
//        PersonIcon(
//            item = Item(
//                1,
//                "Xyz",
//                "cos",
//                "999888333",
//                "asf",
//                listOf("asdfa"),
//                listOf("asdfa"),
//                "asdfa",
//                "asdfa"
//            )
//        )
//        PeopleList(
//            listOf(
//                Item(1, "Xyz", "cos","999888333","asf","asdfa","asdfa"),
//            )
//        )
    }
}
