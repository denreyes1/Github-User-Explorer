package com.denreyes.githubuserexplorer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.denreyes.githubuserexplorer.model.User
import com.denreyes.githubuserexplorer.model.getMockUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailsScreen(
    user: User,
    onBackPressed: () -> Unit
) {
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
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Profile picture + Stats
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

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ProfileStat(number = "456", label = "Followers")
                    ProfileStat(number = "789", label = "Following")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Name + Description
            Text(
                text = user.login,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = "GitHub user. Type: ${user.type}",
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Spacer(modifier = Modifier.height(16.dp))

            // GitHub Profile link
            Text(
                text = "GitHub Profile",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = user.html_url,
                color = Color.Blue,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Score
            Text(text = "Score: ${user.score}", fontSize = 14.sp)
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

@Preview(showBackground = true)
@Composable
fun PreviewUserDetailsScreen() {
    UserDetailsScreen(getMockUser()) {}
}
