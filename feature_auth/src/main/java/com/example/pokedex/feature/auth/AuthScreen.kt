package com.example.pokedex.feature.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pokedex.core.ui.DevicePreviews
import com.example.pokedex.theme.PokedexTheme

@Composable
fun AuthRoute(
    webClientId: String,
    onAuthSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onAuthSuccess()
        }
    }

    AuthScreen(
        uiState = uiState,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onSubmit = viewModel::submitEmailAuth,
        onToggleLogin = viewModel::toggleIsLogin,
        onGoogleSignInClick = { viewModel.signInWithGoogle(context, webClientId) },
        modifier = modifier
    )
}

@Composable
internal fun AuthScreen(
    uiState: AuthState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onToggleLogin: () -> Unit,
    onGoogleSignInClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(androidx.compose.foundation.rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (uiState.isLogin) "Sign In" else "Sign Up",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = uiState.email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.password,
            onValueChange = onPasswordChange,
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (uiState.isLogin) "Sign In" else "Sign Up")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        TextButton(onClick = onToggleLogin) {
            Text(if (uiState.isLogin) "Don't have an account? Sign Up" else "Already have an account? Sign In")
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        HorizontalDivider()

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedButton(
            onClick = onGoogleSignInClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign in with Google")
        }

        if (uiState.error != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = uiState.error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@DevicePreviews
@Composable
fun AuthScreenPreview() {
    PokedexTheme {
        Surface {
            AuthScreen(
                uiState = AuthState(isLogin = true),
                onEmailChange = {},
                onPasswordChange = {},
                onSubmit = {},
                onToggleLogin = {},
                onGoogleSignInClick = {}
            )
        }
    }
}
