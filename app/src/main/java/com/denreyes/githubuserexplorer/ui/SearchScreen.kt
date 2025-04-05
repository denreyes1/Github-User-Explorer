package com.denreyes.githubuserexplorer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.denreyes.githubuserexplorer.R
import com.denreyes.githubuserexplorer.model.User
import kotlinx.coroutines.flow.debounce

/**
 * The main search screen composable for GitHub user search.
 * It includes:
 * - A top app bar with a search field
 * - A content area displaying loading state, search results, errors, or messages
 *
 * @param onShowDetails Callback to handle navigation to the details screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(onShowDetails: (user: User) -> Unit) {
    val viewModel: SearchViewModel = viewModel()
    val searchUIState by viewModel.searchUIState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsState()

    // Helper state to determine what message or content to show
    val isDefaultState = searchQuery.text.length < 3 &&
            !searchUIState.isLoading &&
            searchUIState.users.isEmpty() &&
            searchUIState.error == null

    val isEmptyResult = searchQuery.text.length >= 3 &&
            !searchUIState.isLoading &&
            searchUIState.users.isEmpty() &&
            searchUIState.error == null

    // Trigger user search or clear results based on query length
    LaunchedEffect(Unit) {
        snapshotFlow { searchQuery.text }
            .debounce(300)
            .collect { query ->
                if (query.length >= 3) {
                    viewModel.searchUser(query)
                } else if (query.isEmpty()) {
                    viewModel.clearUsers()
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    SearchBar(
                        searchQuery = searchQuery,
                        onSearchChange = viewModel::onSearchQueryChange
                    )
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                searchUIState.isLoading -> LoadingIndicator()
                searchUIState.users.isNotEmpty() -> SearchListUI(searchUIState.users, onShowDetails)
                searchUIState.error != null -> ErrorMessage(searchUIState.error)
                isDefaultState -> DefaultMessage()
                isEmptyResult -> EmptyResultMessage()
            }
        }
    }
}

/**
 * Composable for the search bar input inside the top app bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(
    searchQuery: TextFieldValue,
    onSearchChange: (TextFieldValue) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(R.string.search_placeholder),
                tint = Color.Gray
            )
            Spacer(modifier = Modifier.width(8.dp))
            TextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = {
                    Text(
                        stringResource(R.string.search_placeholder),
                        color = Color.Gray
                    )
                },
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    cursorColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Composable that shows a loading spinner.
 */
@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

/**
 * Composable that shows the list of user search results.
 *
 * @param users List of GitHub users.
 * @param onShowDetails Callback to handle navigation to the details screen.
 */
@Composable
private fun SearchListUI(
    users: List<User>,
    onShowDetails: (user: User) -> Unit
) {
    LazyColumn {
        items(users) { user ->
            UserItemView(user, onShowDetails)
        }
    }
}

/**
 * Composable that shows a single user item with avatar and username.
 *
 * @param user A GitHub user to display.
 * @param onShowDetails Callback to handle navigation to the details screen.
 */
@Composable
fun UserItemView(
    user: User,
    onShowDetails: (user: User) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .clickable { onShowDetails(user) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = user.avatar_url,
            contentDescription = "${user.login}'s avatar",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.LightGray),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = user.login,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Composable that shows an error message.
 *
 * @param error The error message to display.
 */
@Composable
private fun ErrorMessage(error: String?) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.search_error_title),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                ),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = error ?: stringResource(R.string.search_error_subtitle),
                textAlign = TextAlign.Center,
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/**
 * Composable that shows a default message when the user hasn't typed enough characters.
 */
@Composable
private fun DefaultMessage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.search_default_title),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                ),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.search_default_subtitle),
                textAlign = TextAlign.Center,
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/**
 * Composable that shows a message when no search results are found.
 */
@Composable
private fun EmptyResultMessage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.search_empty_title),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                ),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.search_empty_subtitle),
                textAlign = TextAlign.Center,
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchScreenPreview() {
    SearchScreen {}
}
