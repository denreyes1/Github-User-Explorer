package com.denreyes.githubuserexplorer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.denreyes.githubuserexplorer.model.User
import com.denreyes.githubuserexplorer.navigation.CustomNavType
import com.denreyes.githubuserexplorer.navigation.UserDetails
import com.denreyes.githubuserexplorer.navigation.UsersList
import com.denreyes.githubuserexplorer.ui.followersfollowing.FollowerScreen
import com.denreyes.githubuserexplorer.ui.followersfollowing.FollowingScreen
import com.denreyes.githubuserexplorer.ui.search.SearchScreen
import com.denreyes.githubuserexplorer.ui.userdetails.UserDetailsScreen
import com.denreyes.githubuserexplorer.ui.theme.GithubUserExplorerTheme
import kotlin.reflect.typeOf

/**
 * MainActivity serves as the entry point of the application.
 * Sets up the navigation between SearchScreen and UserDetailsScreen using Jetpack Compose.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            GithubUserExplorerTheme {
                NavHost(
                    navController = navController,
                    startDestination = UsersList,
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable<UsersList> {
                        SearchScreen(
                            onShowDetails = { user ->
                                navController.navigate(UserDetails(user))
                            }
                        )
                    }
                    composable<UserDetails>(
                        typeMap = mapOf(typeOf<User>() to CustomNavType.UserType)
                    ) { backStackEntry ->

                        val route = backStackEntry.toRoute<UserDetails>()
                        UserDetailsScreen(
                            user = route.user,
                            onBackPressed = { navController.popBackStack() },
                            onFollowerPressed = { userId ->
                                navController.navigate("follower/$userId")
                            },
                            onFollowingPressed = { userId ->
                                navController.navigate("following/$userId")
                            }
                        )
                    }
                    composable(
                        route = "follower/{userId}",
                    ) { backStackEntry ->
                        val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull()
                        if (userId != null) {
                            FollowerScreen(
                                userId = userId,
                                onShowDetails = { user ->
                                    navController.navigate(UserDetails(user))
                                },
                                onBackPressed = { navController.popBackStack() }
                            )
                        }
                    }
                    composable(
                        route = "following/{userId}",
                    ) { backStackEntry ->
                        val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull()
                        if (userId != null) {
                            FollowingScreen(
                                userId = userId,
                                onShowDetails = { user ->
                                    navController.navigate(UserDetails(user))
                                },
                                onBackPressed = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}