package com.example.pokedex.feature.auth.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.pokedex.core.R
import com.example.pokedex.core.ui.DevicePreviews
import com.example.pokedex.domain.model.AuthUser
import com.example.pokedex.theme.LocalDimensions
import com.example.pokedex.theme.PokedexTheme

/**
 * Connects [ProfileViewModel] to the profile presentation.
 *
 * @param onNavigateBack callback for the top-app-bar back action.
 * @param onNavigateToAuth callback invoked after the user chooses to log out.
 * @param viewModel Hilt-provided profile state holder.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAuth: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel<ProfileViewModel>(),
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    ProfileScreenContent(
        currentUser = currentUser,
        onNavigateBack = onNavigateBack,
        onNavigateToAuth = onNavigateToAuth,
        onLogout = { viewModel.logout() },
    )
}

/**
 * Stateless profile presentation for an optional [currentUser].
 *
 * [onLogout] performs the session mutation; [onNavigateToAuth] remains separate so navigation is
 * owned by the application module.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreenContent(
    currentUser: AuthUser?,
    onNavigateBack: () -> Unit,
    onNavigateToAuth: () -> Unit,
    onLogout: () -> Unit,
) {
    val dimensions = LocalDimensions.current
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.profile_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.cd_back),
                        )
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues = paddingValues)
                    .padding(all = dimensions.paddingLarge)
                    .verticalScroll(state = rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.size(size = dimensions.imageSizeProfile),
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = stringResource(id = R.string.profile_title),
                    modifier = Modifier.padding(all = dimensions.paddingLarge),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }

            Spacer(modifier = Modifier.height(height = dimensions.paddingLarge))

            if (currentUser != null) {
                if (!currentUser.displayName.isNullOrBlank()) {
                    Text(
                        text = currentUser.displayName!!,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Spacer(modifier = Modifier.height(height = dimensions.paddingSmall))
                }

                Text(
                    text = currentUser.email ?: "No email provided",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                Text(
                    text = stringResource(id = R.string.profile_not_logged_in),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }

            Spacer(modifier = Modifier.height(height = dimensions.paddingExtraLarge))

            Button(
                onClick = {
                    onLogout()
                    onNavigateToAuth()
                },
                modifier = Modifier.fillMaxWidth(),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError,
                    ),
            ) {
                Text(
                    text = stringResource(id = R.string.profile_logout),
                    modifier = Modifier.padding(vertical = dimensions.paddingSmall),
                )
            }
        }
    }
}

/** Design-time preview of an authenticated profile. */
@DevicePreviews
@Composable
fun ProfileScreenPreview() {
    PokedexTheme {
        ProfileScreenContent(
            currentUser = AuthUser("1", "ash@ketchum.com", "Ash Ketchum"),
            onNavigateBack = {},
            onNavigateToAuth = {},
            onLogout = {},
        )
    }
}
