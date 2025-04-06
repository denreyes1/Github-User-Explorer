package com.denreyes.githubuserexplorer.ui.userdetails

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.denreyes.githubuserexplorer.R
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

    Scaffold { innerPadding ->
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
                detailsUIState.isLoading -> UserDetailsShimmer()

                detailsUIState.user != null -> {
                    val userDetails = detailsUIState.user!!

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Profile Image
                        Box(modifier = Modifier.fillMaxWidth()) {
                            AsyncImage(
                                model = user.avatar_url,
                                contentDescription = stringResource(R.string.profile_picture),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(280.dp)
                            )

                            // Back Button
                            IconButton(
                                onClick = onBackPressed,
                                modifier = Modifier
                                    .padding(16.dp)
                                    .size(40.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.surface,
                                        shape = CircleShape
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }

                            // Followers / Following Stats - bottom left of image
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(start = 16.dp, bottom = 16.dp)
                            ) {
                                ProfileStat(
                                    number = userDetails.followers.toString(),
                                    label = stringResource(R.string.following),
                                    userId = userDetails.id,
                                    onClick = onFollowerPressed
                                )
                                ProfileStat(
                                    number = userDetails.following.toString(),
                                    label = stringResource(R.string.followers),
                                    userId = userDetails.id,
                                    onClick = onFollowingPressed
                                )
                            }
                        }

                        // Profile Card Content
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = MaterialTheme.colorScheme.background,
                                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                                )
                                .padding(24.dp)
                        ) {
                            // Name + Twitter FAB
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = userDetails.name ?: userDetails.login,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )

                                userDetails.twitter_username?.let { twitter ->
                                    val context = LocalContext.current
                                    IconButton(
                                        onClick = {
                                            val intent = Intent(
                                                Intent.ACTION_VIEW,
                                                Uri.parse("https://twitter.com/$twitter")
                                            )
                                            context.startActivity(intent)
                                        },
                                        modifier = Modifier
                                            .size(32.dp)
                                            .background(
                                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                                shape = CircleShape
                                            )
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_x_twitter),
                                            contentDescription = stringResource(R.string.twitter_x),
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp) // smaller icon
                                        )
                                    }
                                }
                            }

                            ProfileURLChip(userDetails.login, userDetails.html_url)

                            // Bio
                            userDetails.bio?.let {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(text = "Bio", style = MaterialTheme.typography.labelSmall)
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onBackground
                                )

                                Spacer(modifier = Modifier.height(12.dp))
                                Divider(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp),
                                    color = MaterialTheme.colorScheme.outline,
                                    thickness = 1.dp
                                )
                            }

                            // Company
                            userDetails.company?.let {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = stringResource(R.string.company),
                                    style = MaterialTheme.typography.labelSmall
                                )
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            // Location
                            userDetails.location?.let {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = stringResource(R.string.location),
                                    style = MaterialTheme.typography.labelSmall
                                )
                                Text(
                                    text = it,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                detailsUIState.error != null -> {
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

@Composable
private fun UserDetailsShimmer() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Profile Image with shimmer
        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(MaterialTheme.colorScheme.surface)
                    .shimmerEffect()
            )

            // Back Button shimmer
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .shimmerEffect()
            )

            // Follower/Following shimmer
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, bottom = 16.dp)
            ) {
                repeat(2) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(60.dp)
                            .height(60.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .shimmerEffect()
                    ) {}
                }
            }
        }

        // Card-style content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                )
                .padding(24.dp)
        ) {
            // Name + Twitter FAB shimmer
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(24.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .shimmerEffect()
                )

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                        .shimmerEffect()
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Twitter chip placeholder
            Box(
                modifier = Modifier
                    .height(32.dp)
                    .fillMaxWidth(0.5f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .shimmerEffect()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Bio placeholder
            Box(
                modifier = Modifier
                    .height(16.dp)
                    .fillMaxWidth(0.8f)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .shimmerEffect()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .height(14.dp)
                    .fillMaxWidth(0.6f)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .shimmerEffect()
            )

            Spacer(modifier = Modifier.height(24.dp))
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                thickness = 1.dp
            )

            // Company
            Box(
                modifier = Modifier
                    .height(16.dp)
                    .fillMaxWidth(0.4f)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .shimmerEffect()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .height(20.dp)
                    .fillMaxWidth(0.6f)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .shimmerEffect()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Location
            Box(
                modifier = Modifier
                    .height(16.dp)
                    .fillMaxWidth(0.4f)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .shimmerEffect()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .height(20.dp)
                    .fillMaxWidth(0.6f)
                    .clip(RoundedCornerShape(4.dp))
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
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .width(80.dp)
            .height(80.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onClick(userId) }
            .padding(8.dp)
    ) {
        Text(
            text = number,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
        )
        Text(
            text = label,
            fontSize = 13.sp,
        )
    }
}


/**
 * Displays a clickable chip that opens the user's GitHub profile in a browser.
 *
 * @param url The URL of the user's GitHub profile.
 */
@Composable
private fun ProfileURLChip(username: String, url: String) {
    val context = LocalContext.current

    AssistChip(
        onClick = {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        },
        label = {
            Box {
                Text(
                    text = "@$username",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            labelColor = MaterialTheme.colorScheme.primary,
            leadingIconContentColor = MaterialTheme.colorScheme.primary,
            trailingIconContentColor = MaterialTheme.colorScheme.primary
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewUserDetailsScreen() {
    UserDetailsScreen(getMockUser(), {}, {}, {})
}
