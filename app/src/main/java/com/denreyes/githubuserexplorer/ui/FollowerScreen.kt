package com.denreyes.githubuserexplorer.ui

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.denreyes.githubuserexplorer.R
import com.denreyes.githubuserexplorer.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowerScreen(
    userId: Int,
    onShowDetails: (user: User) -> Unit,
    onBackPressed: () -> Unit
) {
    val viewModel: FollowerViewModel = viewModel()
    val followerUIState by viewModel.followerUIState.collectAsStateWithLifecycle()

    // To avoid calling every recomposition
    LaunchedEffect(userId) {
        viewModel.fetchFollowers(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.followers)) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
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
                followerUIState.isLoading -> LoadingIndicator()
                followerUIState.users.isNotEmpty() -> FollowerListUI(followerUIState.users, onShowDetails)
                followerUIState.error != null -> ErrorMessage(followerUIState.error)
            }
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
private fun FollowerListUI(
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