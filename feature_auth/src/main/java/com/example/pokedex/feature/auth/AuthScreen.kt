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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
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
    var passwordVisible by remember { mutableStateOf(false) }
    val isFormValid = uiState.email.isNotBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(uiState.email).matches() && uiState.password.isNotBlank()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(androidx.compose.foundation.rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (uiState.isLogin) stringResource(com.example.pokedex.core.R.string.auth_sign_in) else stringResource(com.example.pokedex.core.R.string.auth_sign_up),
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = uiState.email,
            onValueChange = onEmailChange,
            label = { Text(stringResource(com.example.pokedex.core.R.string.auth_email)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Email,
                imeAction = androidx.compose.ui.text.input.ImeAction.Next
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = uiState.password,
            onValueChange = onPasswordChange,
            label = { Text(stringResource(com.example.pokedex.core.R.string.auth_password)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (passwordVisible) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Password,
                imeAction = androidx.compose.ui.text.input.ImeAction.Done
            ),
            keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                onDone = { if (isFormValid) onSubmit() }
            ),
            trailingIcon = {
                val image = if (passwordVisible) {
                    Icons.Filled.Lock
                } else {
                    Icons.Filled.Lock
                }
                
                // Note: using Lock icon as fallback since Visibility might need material-icons-extended
                // The label will change regardless to assist screen readers.
                val description = if (passwordVisible) stringResource(com.example.pokedex.core.R.string.auth_hide_password) else stringResource(com.example.pokedex.core.R.string.auth_show_password)

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = description)
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth(),
                enabled = isFormValid
            ) {
                Text(if (uiState.isLogin) stringResource(com.example.pokedex.core.R.string.auth_sign_in) else stringResource(com.example.pokedex.core.R.string.auth_sign_up))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        TextButton(onClick = onToggleLogin) {
            Text(if (uiState.isLogin) stringResource(com.example.pokedex.core.R.string.auth_toggle_to_sign_up) else stringResource(com.example.pokedex.core.R.string.auth_toggle_to_sign_in))
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        HorizontalDivider()

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedButton(
            onClick = onGoogleSignInClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(com.example.pokedex.core.R.string.auth_google_sign_in))
        }

        if (uiState.error != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(id = uiState.error),
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
