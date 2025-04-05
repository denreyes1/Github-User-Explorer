package com.denreyes.githubuserexplorer.navigation

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import com.denreyes.githubuserexplorer.model.User
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
object UsersList
@Serializable
data class UserDetails(
    val user: User
)

object CustomNavType {
    val UserType = object : NavType<User>(isNullableAllowed = false) {
        override fun get(bundle: Bundle, key: String): User {
            return Json.decodeFromString(bundle.getString(key) ?: throw IllegalArgumentException("User not found in bundle"))
        }

        override fun parseValue(value: String): User {
            return Json.decodeFromString(value)
        }

        override fun serializeAsValue(value: User): String {
            return Uri.encode(Json.encodeToString(value))
        }

        override fun put(bundle: Bundle, key: String, value: User) {
            bundle.putString(key, Json.encodeToString(value))
        }
    }
}
