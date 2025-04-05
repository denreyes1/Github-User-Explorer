package com.denreyes.githubuserexplorer.model

import kotlinx.serialization.Serializable

/**
 * Model data class representing a GitHub user returned from the search API.
 **/
@Serializable
data class User (
    val login: String,
    val id: Int,
    val node_id: String,
    val avatar_url: String,
    val gravatar_id: String,
    val url: String,
    val html_url: String,
    val followers_url: String,
    val following_url: String,
    val gists_url: String,
    val starred_url: String,
    val subscriptions_url: String,
    val organizations_url: String,
    val repos_url: String,
    val events_url: String,
    val received_events_url: String,
    val type: String,
    val user_view_type: String,
    val site_admin: Boolean,
    val score: Double
)

fun getMockUser(): User {
    return User(
        login = "janedoe",
        id = 1234567,
        node_id = "MDQ6VXNlcjEyMzQ1Njc=",
        avatar_url = "https://avatars.githubusercontent.com/u/1234567?v=4",
        gravatar_id = "",
        url = "https://api.github.com/users/janedoe",
        html_url = "https://github.com/janedoe",
        followers_url = "https://api.github.com/users/janedoe/followers",
        following_url = "https://api.github.com/users/janedoe/following{/other_user}",
        gists_url = "https://api.github.com/users/janedoe/gists{/gist_id}",
        starred_url = "https://api.github.com/users/janedoe/starred{/owner}{/repo}",
        subscriptions_url = "https://api.github.com/users/janedoe/subscriptions",
        organizations_url = "https://api.github.com/users/janedoe/orgs",
        repos_url = "https://api.github.com/users/janedoe/repos",
        events_url = "https://api.github.com/users/janedoe/events{/privacy}",
        received_events_url = "https://api.github.com/users/janedoe/received_events",
        type = "User",
        user_view_type = "public",
        site_admin = false,
        score = 0.95
    )
}