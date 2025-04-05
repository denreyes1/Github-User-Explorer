package com.denreyes.githubuserexplorer.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class UserDetails(
    val login: String,
    val id: Int,
    val node_id: String,
    val gravatar_id: String,
    val url: String,
    val type: String,
    val site_admin: Boolean,
    val name: String?,
    val company: String?,
    val blog: String?,
    val location: String?,
    val email: String?,
    val hireable: Boolean?,
    val bio: String?,
    val twitter_username: String?,
    val public_repos: Int,
    val public_gists: Int,
    val followers: Int,
    val following: Int,
    val created_at: String,
    val updated_at: String
)

fun getMockUserDetails(): UserDetails {
    return UserDetails(
        login = "denreyes",
        id = 9638030,
        node_id = "MDQ6VXNlcjk2MzgwMzA=",
        gravatar_id = "",
        url = "https://api.github.com/users/denreyes",
        type = "User",
        site_admin = false,
        name = "Den Reyes",
        company = null,
        blog = "http://www.denreyes.com/",
        location = "Toronto, CA",
        email = null,
        hireable = true,
        bio = null,
        twitter_username = null,
        public_repos = 32,
        public_gists = 0,
        followers = 8,
        following = 5,
        created_at = "2014-11-09T09:23:24Z",
        updated_at = "2024-10-06T11:19:17Z"
    )
}
