package com.denreyes.githubuserexplorer.ui.followersfollowing

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
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
import coil.compose.AsyncImage
import com.denreyes.githubuserexplorer.R
import com.denreyes.githubuserexplorer.model.User
import com.denreyes.githubuserexplorer.ui.common.shimmerEffect
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

/**
 * Displays a list of followers for a given user.
 *
 * @param userId The ID of the user whose followers are displayed.
 * @param onShowDetails Callback to navigate to user details.
 * @param onBackPressed Callback to handle back navigation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowerScreen(
    userId: Int,
    onShowDetails: (user: User) -> Unit,
    onBackPressed: () -> Unit
) {
    val viewModel: FollowerFollowingViewModel = koinViewModel()
    val followerUIState by viewModel.followerUIState.collectAsStateWithLifecycle()
    val pullToRefreshState = rememberPullToRefreshState()
    val coroutineScope = rememberCoroutineScope()

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
        PullToRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            state = pullToRefreshState,
            isRefreshing = followerUIState.isLoading,
            onRefresh = {
                coroutineScope.launch {
                    pullToRefreshState.animateToThreshold()
                    viewModel.fetchFollowers(userId)
                    delay(1000)
                    pullToRefreshState.animateToHidden()
                }
            }
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                when {
                    followerUIState.isLoading -> items(10) {
                        UserItemShimmer()
                    }
                    followerUIState.users.isNotEmpty() -> items(followerUIState.users) { user ->
                        UserItemView(user, onShowDetails)
                    }
                    followerUIState.error != null -> item {
                        Box(
                            modifier = Modifier.fillParentMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            ErrorMessage(followerUIState.error)
                        }
                    }
                }
            }
        }
    }
}


/**
 * Displays a list of users that the given user is following.
 *
 * @param userId The ID of the user whose followings are displayed.
 * @param onShowDetails Callback to navigate to user details.
 * @param onBackPressed Callback to handle back navigation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowingScreen(
    userId: Int,
    onShowDetails: (user: User) -> Unit,
    onBackPressed: () -> Unit
) {
    val viewModel: FollowerFollowingViewModel = koinViewModel()
    val followingUIState by viewModel.followingUIState.collectAsStateWithLifecycle()
    val pullToRefreshState = rememberPullToRefreshState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(userId) {
        viewModel.fetchFollowing(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.following)) },
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
        PullToRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            state = pullToRefreshState,
            isRefreshing = followingUIState.isLoading,
            onRefresh = {
                coroutineScope.launch {
                    pullToRefreshState.animateToThreshold()
                    viewModel.fetchFollowing(userId)
                    delay(1000)
                    pullToRefreshState.animateToHidden()
                }
            }
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                when {
                    followingUIState.isLoading -> items(10) {
                        UserItemShimmer()
                    }
                    followingUIState.users.isNotEmpty() -> items(followingUIState.users) { user ->
                        UserItemView(user, onShowDetails)
                    }
                    followingUIState.error != null -> item {
                        Box(
                            modifier = Modifier.fillParentMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            ErrorMessage(followingUIState.error)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Composable that shows a shimmer placeholder for a user list item.
 */
@Composable
private fun UserItemShimmer() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .shimmerEffect()
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .height(16.dp)
                    .fillMaxWidth(0.5f)
                    .background(MaterialTheme.colorScheme.surface)
                    .shimmerEffect()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .height(14.dp)
                    .fillMaxWidth(0.3f)
                    .background(MaterialTheme.colorScheme.surface)
                    .shimmerEffect()
            )
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