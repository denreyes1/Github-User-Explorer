package com.denreyes.githubuserexplorer.ui

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.denreyes.githubuserexplorer.model.User
import com.denreyes.githubuserexplorer.model.getMockUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailsScreen(
    user: User,
    onBackPressed: () -> Unit
) {
    val viewModel: UserDetailsViewModel = viewModel()
    val detailsUIState by viewModel.detailsUIState.collectAsStateWithLifecycle()

    // To avoid calling every recomposition
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
        when {
            detailsUIState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            detailsUIState.user != null -> {
                val userDetails = detailsUIState.user

                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Profile picture + Stats + Chip
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Avatar
                        AsyncImage(
                            model = user.avatar_url,
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        // Stats and Chip Column
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                ProfileStat(number = userDetails?.followers.toString(), label = "Followers")
                                ProfileStat(number = userDetails?.following.toString(), label = "Following")
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // GitHub Profile Chip styled like a follow button
                            ProfileURLChip(user.html_url)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Name + Bio
                    Text(
                        text = userDetails?.name ?: userDetails?.login!!,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    if (!userDetails?.bio.isNullOrBlank()) {
                        Text(
                            text = userDetails?.bio!!,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Company / Location / Twitter
                    userDetails?.company?.let {
                        Text(text = "🏢 $it", fontSize = 14.sp)
                    }
                    userDetails?.location?.let {
                        Text(text = "📍 $it", fontSize = 14.sp)
                    }
                    userDetails?.twitter_username?.let {
                        Text(text = "🐦 @$it", fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                }
            }

            detailsUIState.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Error: ${detailsUIState.error}")
                }
            }
        }
    }
}


@Composable
fun ProfileStat(number: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(60.dp)
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

@Composable
fun ProfileURLChip(url: String) {
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
    UserDetailsScreen(getMockUser()) {}
}
