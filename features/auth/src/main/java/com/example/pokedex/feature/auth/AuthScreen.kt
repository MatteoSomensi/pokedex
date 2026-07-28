package com.example.pokedex.feature.auth

import android.util.Patterns
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.pokedex.core.R
import com.example.pokedex.core.ui.DevicePreviews
import com.example.pokedex.theme.LocalDimensions
import com.example.pokedex.theme.PokedexTheme
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

/**
 * Connects [AuthViewModel] state to the authentication form and Credential Manager.
 *
 * Google credential requests are initiated from this route because they require a UI context.
 * Successful email or Google authentication invokes [onAuthSuccess].
 *
 * @param onAuthSuccess navigation callback invoked after authentication completes.
 * @param modifier modifier applied to the stateless authentication form.
 * @param viewModel Hilt-provided state holder; injectable for focused tests.
 */
@Composable
fun AuthRoute(
    onAuthSuccess: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel<AuthViewModel>(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = uiState.isSuccess) {
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
        onGoogleSignInClick = {
            coroutineScope.launch {
                viewModel.setLoading(isLoading = true)
                try {
                    val credentialManager = CredentialManager.create(context = context)
                    val googleIdOption =
                        GetGoogleIdOption
                            .Builder()
                            .setFilterByAuthorizedAccounts(filterByAuthorizedAccounts = false)
                            .setServerClientId(serverClientId = viewModel.webClientId)
                            .setAutoSelectEnabled(autoSelectEnabled = true)
                            .build()

                    val request =
                        GetCredentialRequest
                            .Builder()
                            .addCredentialOption(credentialOption = googleIdOption)
                            .build()

                    val result =
                        credentialManager.getCredential(
                            context = context,
                            request = request,
                        )
                    val credential = result.credential

                    if (credential is CustomCredential &&
                        credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                    ) {
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(data = credential.data)
                        val idToken = googleIdTokenCredential.idToken
                        viewModel.signInWithGoogleToken(idToken = idToken)
                    } else {
                        viewModel.setAuthError(errorRes = R.string.error_auth_failed)
                    }
                } catch (_: GetCredentialCancellationException) {
                    viewModel.setLoading(isLoading = false)
                } catch (
                    e: CancellationException,
                ) {
                    throw e
                } catch (_: Exception) {
                    viewModel.setAuthError(errorRes = R.string.error_auth_failed)
                }
            }
        },
        modifier = modifier,
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
    modifier: Modifier = Modifier,
) {
    var passwordVisible by remember { mutableStateOf(value = false) }
    val isFormValid =
        uiState.email.isNotBlank() &&
            Patterns.EMAIL_ADDRESS
                .matcher(uiState.email)
                .matches() &&
            uiState.password.isNotBlank()
    val dimensions = LocalDimensions.current

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(all = dimensions.paddingLarge)
                .verticalScroll(state = rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text =
                if (uiState.isLogin) {
                    stringResource(id = R.string.auth_sign_in)
                } else {
                    stringResource(
                        id = R.string.auth_sign_up,
                    )
                },
            style = MaterialTheme.typography.headlineMedium,
        )

        Spacer(modifier = Modifier.height(height = dimensions.paddingExtraLarge))

        OutlinedTextField(
            value = uiState.email,
            onValueChange = onEmailChange,
            label = { Text(text = stringResource(id = R.string.auth_email)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                ),
        )

        Spacer(modifier = Modifier.height(height = dimensions.paddingMedium))

        OutlinedTextField(
            value = uiState.password,
            onValueChange = onPasswordChange,
            label = { Text(text = stringResource(id = R.string.auth_password)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                ),
            keyboardActions =
                KeyboardActions(
                    onDone = { if (isFormValid) onSubmit() },
                ),
            trailingIcon = {
                val image =
                    if (passwordVisible) {
                        Icons.Filled.Lock
                    } else {
                        Icons.Filled.Lock
                    }

                val description =
                    if (passwordVisible) {
                        stringResource(id = R.string.auth_hide_password)
                    } else {
                        stringResource(
                            id = R.string.auth_show_password,
                        )
                    }

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = description)
                }
            },
        )

        Spacer(modifier = Modifier.height(height = dimensions.paddingLarge))

        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth(),
                enabled = isFormValid,
            ) {
                Text(
                    if (uiState.isLogin) {
                        stringResource(id = R.string.auth_sign_in)
                    } else {
                        stringResource(
                            id = R.string.auth_sign_up,
                        )
                    },
                )
            }
        }

        Spacer(modifier = Modifier.height(height = dimensions.paddingMedium))

        TextButton(onClick = onToggleLogin) {
            Text(
                if (uiState.isLogin) {
                    stringResource(id = R.string.auth_toggle_to_sign_up)
                } else {
                    stringResource(
                        id = R.string.auth_toggle_to_sign_in,
                    )
                },
            )
        }

        Spacer(modifier = Modifier.height(height = dimensions.paddingLarge))

        HorizontalDivider()

        Spacer(modifier = Modifier.height(height = dimensions.paddingLarge))

        OutlinedButton(
            onClick = onGoogleSignInClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = stringResource(id = R.string.auth_google_sign_in))
        }

        if (uiState.error != null) {
            Spacer(modifier = Modifier.height(height = dimensions.paddingMedium))
            Text(
                text = uiState.error.asString(),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

/** Design-time preview of the sign-in state across shared device configurations. */
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
                onGoogleSignInClick = {},
            )
        }
    }
}
