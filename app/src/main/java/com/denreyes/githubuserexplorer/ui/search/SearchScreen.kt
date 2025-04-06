package com.denreyes.githubuserexplorer.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
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
import coil.compose.AsyncImage
import com.denreyes.githubuserexplorer.R
import com.denreyes.githubuserexplorer.model.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

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
    val viewModel: SearchViewModel = koinViewModel()
    val searchUIState by viewModel.searchUIState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val pullToRefreshState = rememberPullToRefreshState()

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

        PullToRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            state = pullToRefreshState,
            isRefreshing = searchUIState.isLoading,
            onRefresh = {
                coroutineScope.launch {
                    pullToRefreshState.animateToThreshold()
                    viewModel.refreshData()
                    delay(1500) // Simulate network delay
                    pullToRefreshState.animateToHidden()
                }
            }
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                when {
                    searchUIState.isLoading -> item {
                        Box(
                            modifier = Modifier.fillParentMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            LoadingIndicator()
                        }
                    }

                    searchUIState.users.isNotEmpty() -> items(searchUIState.users) { user ->
                        UserItemView(user, onShowDetails)
                    }

                    searchUIState.error != null -> item {
                        Box(
                            modifier = Modifier.fillParentMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            ErrorMessage(searchUIState.error)
                        }
                    }

                    searchQuery.text.length < 3 -> item {
                        Box(
                            modifier = Modifier.fillParentMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            DefaultMessage()
                        }
                    }

                    else -> item {
                        Box(
                            modifier = Modifier.fillParentMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            EmptyResultMessage()
                        }
                    }
                }
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
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.White
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
private fun UserItemView(
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
