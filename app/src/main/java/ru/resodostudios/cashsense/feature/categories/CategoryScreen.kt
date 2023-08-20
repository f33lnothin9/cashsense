package ru.resodostudios.cashsense.feature.categories

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
internal fun CategoryRoute(
    viewModel: CategoriesViewModel = hiltViewModel()
) {
    CategoryScreen()
}

@Composable
internal fun CategoryScreen() {

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

    }
}