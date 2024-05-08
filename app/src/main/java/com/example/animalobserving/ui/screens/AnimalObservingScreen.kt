package com.example.animalobserving.ui.screens

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
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
    NewRecord(title = R.string.new_record_screen)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalObservingAppTopBar(
    currentScreen: AppScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(stringResource(currentScreen.title)) },
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
    //viewModel: MapViewModel,
    navController: NavHostController = rememberNavController()
) {
    val mapViewModel: MapViewModel = viewModel(factory = MapViewModel.Factory)

    val backStackEntry by navController.currentBackStackEntryAsState()
    var currentScreen = AppScreen.valueOf(
        backStackEntry?.destination?.route ?: AppScreen.Home.name
    )

    Scaffold (
        topBar = {
            AnimalObservingAppTopBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() })
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
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        },
        bottomBar = {
            NavigationBar {
                val selectedItem = null
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Home, contentDescription = "A") },
                    label = { Text("Home") },
                    selected = selectedItem == "B",
                    onClick = {
                        if (currentScreen != AppScreen.Home) {
                            navController.popBackStack(AppScreen.Home.name, inclusive = false)
                            currentScreen = AppScreen.valueOf(
                                backStackEntry?.destination?.route ?: AppScreen.Home.name
                            )
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.List, contentDescription = "A") },
                    label = { Text("List") },
                    selected = selectedItem == "A",
                    onClick = {
                        if (currentScreen != AppScreen.List) {
                            navController.navigate(AppScreen.List.name)
                            currentScreen = AppScreen.valueOf(
                                backStackEntry?.destination?.route ?: AppScreen.List.name
                            )
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Build, contentDescription = "A") },
                    label = { Text("Filter") },
                    selected = selectedItem == "B",
                    onClick = {
                        if (currentScreen != AppScreen.Filter) {
                            navController.navigate(AppScreen.Filter.name)
                            currentScreen = AppScreen.valueOf(
                                backStackEntry?.destination?.route ?: AppScreen.Filter.name
                            )
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Settings, contentDescription = "A") },
                    label = { Text("Settings") },
                    selected = selectedItem == "C",
                    onClick = {
                        if (currentScreen != AppScreen.Settings) {
                            navController.navigate(AppScreen.Settings.name)
                            currentScreen = AppScreen.valueOf(
                                backStackEntry?.destination?.route ?: AppScreen.Settings.name
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
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            composable(route = AppScreen.Home.name) {
                HomeScreen(
                    mapViewModel = mapViewModel,
                    retryAction = mapViewModel::getMapMarkers,
                    modifier = Modifier.fillMaxSize()
                )
            }
            composable(route = AppScreen.List.name) {
                RecordListScreen(
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
                AddNewRecordScreen(
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}