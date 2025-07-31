package br.com.marllonbruno.fitnesstracker.android.data.remote

// DTO para a requisição de registro
data class UserRegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

// DTO para a requisição de login
data class AuthenticationRequest(
    val email: String,
    val password: String
)

// DTO para a resposta de autenticação
data class AuthenticationResponse(
    val token: String
)
