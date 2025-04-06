package com.denreyes.githubuserexplorer.model

import kotlinx.serialization.Serializable

@Serializable
data class UserDetails(
    val login: String,
    val id: Int,
    val node_id: String,
    val gravatar_id: String,
    val url: String,
    val html_url: String,
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

