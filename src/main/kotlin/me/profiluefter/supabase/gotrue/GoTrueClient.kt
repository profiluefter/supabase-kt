package me.profiluefter.supabase.gotrue

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

class GoTrueClient(baseURL: String, private var anonToken: String, httpClient: HttpClient = HttpClient()) {
    private val httpClient = httpClient.config {
        install(ContentNegotiation) {
            json()
        }

        defaultRequest {
            url(baseURL)
            contentType(ContentType.Application.Json)
        }
    }

    var userToken: String? = null

    suspend fun signUpWithEmail(email: String, password: String): TokenResponse =
        httpClient.post("signup") {
            url { parameters["apikey"] = anonToken }
            setBody(EmailAuthPayload(email, password))
        }.handleError()
            .body()

    suspend fun signInWithEmail(email: String, password: String): TokenResponse =
        httpClient.post("token") {
            url {
                parameters["apikey"] = anonToken
                parameters["grant_type"] = "password"
            }
            setBody(EmailAuthPayload(email, password))
        }.handleError()
            .body()

    suspend fun signOut() =
        httpClient.post("logout") {
            url {
                parameters["apikey"] = anonToken
            }
            bearerAuth(userToken ?: throw IllegalStateException("Can't sign out when not signed in"))
        }.handleError()
}

suspend fun HttpResponse.handleError(): HttpResponse {
    if (!status.isSuccess())
        throw GoTrueApiException(body())
    return this
}

@Serializable
data class EmailAuthPayload(val email: String, val password: String)

@Serializable
data class TokenResponse(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("refresh_token")
    val refreshToken: String,
    @SerialName("expires_in")
    val expiresIn: Long,
    @SerialName("token_type")
    val tokenType: String, // = "bearer"
    val user: User
)

@Serializable
data class User(
    val id: String,
    @SerialName("aud")
    val audience: String,
    val role: String,
    val email: String,
    @SerialName("email_confirmed_at")
    val emailConfirmedAt: String?, // TODO: deserialize dates correctly
    val phone: String,
    @SerialName("confirmed_at")
    val confirmedAt: String?,
    @SerialName("last_sign_in_at")
    val lastSignInAt: String, // TODO: check which values should be nullable
    @SerialName("app_metadata")
    val appMetadata: JsonObject,
    @SerialName("user_metadata")
    val userMetadata: JsonObject,
    val identities: List<Identity>,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("updated_at")
    val updatedAt: String
)

@Serializable
data class Identity(
    val id: String,
    @SerialName("user_id")
    val userId: String,
    @SerialName("identity_data")
    val identityData: JsonObject,
    val provider: String,
    @SerialName("last_sign_in_at")
    val lastSignInAt: String,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("updated_at")
    val updatedAt: String
)

@Serializable
data class ApiErrorResponse(
    val code: Long,
    @SerialName("msg")
    val message: String
)

class GoTrueApiException internal constructor(response: ApiErrorResponse) :
    Exception("${response.code}: ${response.message}") {
    val code = response.code
    val apiMessage = response.message
}
