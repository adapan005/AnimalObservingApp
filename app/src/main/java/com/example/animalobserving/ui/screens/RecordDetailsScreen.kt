package com.example.animalobserving.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

@Composable
fun RecordDetailsScreen (
    navController: NavHostController,
    recordId: Int,
    modifier: Modifier,
) {
    Text(text = "Record; ID $recordId")
}