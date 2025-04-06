package com.denreyes.githubuserexplorer.ui.userdetails

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AssistChip
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.denreyes.githubuserexplorer.model.User
import com.denreyes.githubuserexplorer.model.getMockUser
import com.denreyes.githubuserexplorer.ui.common.shimmerEffect
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

/**
 * Displays detailed information about a user, including profile stats and bio.
 *
 * @param user The user whose details are being displayed.
 * @param onBackPressed Function triggered when the back button is pressed.
 * @param onFollowerPressed Function to handle follower click events, with userId as argument.
 * @param onFollowingPressed Function to handle following click events, with userId as argument.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailsScreen(
    user: User,
    onBackPressed: () -> Unit,
    onFollowerPressed: (Int) -> Unit,
    onFollowingPressed: (Int) -> Unit
) {
    val viewModel: UserDetailsViewModel = koinViewModel()
    val detailsUIState by viewModel.detailsUIState.collectAsStateWithLifecycle()

    val pullToRefreshState = rememberPullToRefreshState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(user.id) {
        viewModel.fetchUserDetails(user.id)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = user.login) },
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
    ) { innerPadding ->
        PullToRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            state = pullToRefreshState,
            isRefreshing = detailsUIState.isLoading,
            onRefresh = {
                coroutineScope.launch {
                    pullToRefreshState.animateToThreshold()
                    viewModel.fetchUserDetails(user.id)
                    delay(1000)
                    pullToRefreshState.animateToHidden()
                }
            }
        ) {
            when {
                detailsUIState.isLoading -> {
                    UserDetailsShimmer()
                }

                detailsUIState.user != null -> {
                    val userDetails = detailsUIState.user!!

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AsyncImage(
                                model = user.avatar_url,
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    ProfileStat(
                                        number = userDetails.followers.toString(),
                                        label = "Followers",
                                        userId = userDetails.id,
                                        onClick = onFollowerPressed
                                    )
                                    ProfileStat(
                                        number = userDetails.following.toString(),
                                        label = "Following",
                                        userId = userDetails.id,
                                        onClick = onFollowingPressed
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                ProfileURLChip(user.html_url)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = userDetails.name ?: userDetails.login,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )

                        userDetails.bio?.let {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = it, fontSize = 14.sp)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        userDetails.company?.let {
                            Text(text = "🏢 $it", fontSize = 14.sp)
                        }
                        userDetails.location?.let {
                            Text(text = "📍 $it", fontSize = 14.sp)
                        }
                        userDetails.twitter_username?.let {
                            Text(text = "🐦 @$it", fontSize = 14.sp)
                        }
                    }
                }

                detailsUIState.error != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Error: ${detailsUIState.error}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UserDetailsShimmer() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .shimmerEffect()
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    repeat(2) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.width(60.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .height(20.dp)
                                    .width(40.dp)
                                    .background(MaterialTheme.colorScheme.surface)
                                    .shimmerEffect()
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .height(14.dp)
                                    .width(40.dp)
                                    .background(MaterialTheme.colorScheme.surface)
                                    .shimmerEffect()
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp)
                        .background(MaterialTheme.colorScheme.surface)
                        .shimmerEffect()
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .height(20.dp)
                .fillMaxWidth(0.5f)
                .background(MaterialTheme.colorScheme.surface)
                .shimmerEffect()
        )

        Spacer(modifier = Modifier.height(8.dp))

        repeat(3) {
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .height(14.dp)
                    .fillMaxWidth(0.8f)
                    .background(MaterialTheme.colorScheme.surface)
                    .shimmerEffect()
            )
        }
    }
}

/**
 * Displays profile stats such as followers and following count.
 *
 * @param number The number to display.
 * @param label A label to describe the number.
 * @param userId The user ID associated with the stats.
 * @param onClick The function triggered when the stat is clicked.
 */
@Composable
private fun ProfileStat(
    number: String,
    label: String,
    userId: Int,
    onClick: (Int) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(60.dp)
            .clickable{
                onClick(userId)
            }
    ) {
        Text(
            text = number,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Displays a clickable chip that opens the user's GitHub profile in a browser.
 *
 * @param url The URL of the user's GitHub profile.
 */
@Composable
private fun ProfileURLChip(url: String) {
    val context = LocalContext.current

    AssistChip(
        onClick = {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        },
        label = {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "GitHub Profile",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewUserDetailsScreen() {
    UserDetailsScreen(getMockUser(), {}, {}, {})
}
