package com.example.animalobserving.ui.screens

import android.content.ContentValues.TAG
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.animalobserving.R

enum class AppScreen(@StringRes val title: Int) {
    Home(title = R.string.home_screen),
    List(title = R.string.list_screen),
    Filter(title = R.string.filter_screen),
    Settings(title = R.string.settings_screen),
    NewRecord(title = R.string.new_record_screen),
    RecordDetails(title = R.string.record_details_screen)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalObservingAppTopBar(
    currentScreen: AppScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    refresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Row {
                Text(stringResource(currentScreen.title))
                if (currentScreen == AppScreen.Home) {
                    IconButton(onClick = refresh ) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = stringResource(R.string.back_button)
                        )
                    }
                }
            }
                },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalObservingApp(
    navController: NavHostController = rememberNavController()
) {
    val mapViewModel: MapViewModel = viewModel(factory = MapViewModel.Factory)
    var currentScreen: AppScreen = AppScreen.Home
    val backStackEntry by navController.currentBackStackEntryAsState()
    val route = backStackEntry?.destination?.route?.split("/")?.get(0)
    try {
        route?.let {
            currentScreen = AppScreen.valueOf(it)
        }
    } catch (e: Exception) {
        Log.d(TAG, "RECEIVED : ${e.toString()}")
    }

    Scaffold (
        topBar = {
            AnimalObservingAppTopBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                refresh = mapViewModel::getMapMarkers,
                navigateUp = { navController.navigateUp() }
            )
        },
        floatingActionButton = {
            if (currentScreen == AppScreen.Home) {
                FloatingActionButton(onClick = {
                    if (currentScreen != AppScreen.NewRecord) {
                        navController.navigate(AppScreen.NewRecord.name)
                        currentScreen = AppScreen.valueOf(
                            backStackEntry?.destination?.route ?: AppScreen.NewRecord.name
                        )
                    }
                }) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add))
                }
            }
        },
        bottomBar = {
            NavigationBar {
                val selectedItem = null
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Home, contentDescription = "A") },
                    label = { Text(stringResource(R.string.home)) },
                    selected = selectedItem == "B",
                    onClick = {
                        if (currentScreen != AppScreen.Home) {
                            navController.popBackStack(AppScreen.Home.name, inclusive = false)
                            currentScreen = AppScreen.valueOf(
                                backStackEntry?.destination?.route?.split("/")?.get(0) ?: AppScreen.Home.name
                            )
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.List, contentDescription = "A") },
                    label = { Text(stringResource(R.string.list)) },
                    selected = selectedItem == "A",
                    onClick = {
                        if (currentScreen != AppScreen.List) {
                            navController.navigate(AppScreen.List.name)
                            currentScreen = AppScreen.valueOf(
                                backStackEntry?.destination?.route?.split("/")?.get(0) ?: AppScreen.List.name
                            )
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Build, contentDescription = "A") },
                    label = { Text(stringResource(R.string.filter)) },
                    selected = selectedItem == "B",
                    onClick = {
                        if (currentScreen != AppScreen.Filter) {
                            navController.navigate(AppScreen.Filter.name)
                            currentScreen = AppScreen.valueOf(
                                backStackEntry?.destination?.route?.split("/")?.get(0) ?: AppScreen.Filter.name
                            )
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Settings, contentDescription = "A") },
                    label = { Text(stringResource(R.string.settings)) },
                    selected = selectedItem == "C",
                    onClick = {
                        if (currentScreen != AppScreen.Settings) {
                            navController.navigate(AppScreen.Settings.name)
                            currentScreen = AppScreen.valueOf(
                                backStackEntry?.destination?.route?.split("/")?.get(0) ?: AppScreen.Settings.name
                            )
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        //val uiState by viewModel.uiState.collectAsState()

        NavHost(
            navController = navController,
            startDestination = AppScreen.Home.name,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(route = AppScreen.Home.name) {
                HomeScreen(
                    mapViewModel = mapViewModel,
                    navController,
                    retryAction = mapViewModel::getMapMarkers,
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable(route = AppScreen.List.name) {
                RecordListScreen(
                    mapViewModel = mapViewModel,
                    navController,
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable(route = AppScreen.Filter.name) {
                FilterScreen(
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable(route = AppScreen.Settings.name) {
                SettingsScreen(
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable(route = AppScreen.NewRecord.name) {
                AddingNewRecordScreen(
                    navController,
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable(route = AppScreen.RecordDetails.name + "/{recordID}") {
                backStackEntry ->
                RecordDetailsScreen(
                        recordId = backStackEntry.arguments?.getString("recordID")?.toInt()?:-1,
                        modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}