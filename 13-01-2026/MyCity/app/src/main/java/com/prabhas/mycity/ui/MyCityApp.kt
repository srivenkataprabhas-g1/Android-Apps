package com.prabhas.mycity.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.prabhas.mycity.R
import com.prabhas.mycity.ui.utils.MyCityContentType
import com.prabhas.mycity.ui.utils.MyCityNavigationType

enum class MyCityScreen {
    Categories,
    Recommendations,
    Details
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCityApp(
    windowSize: WindowWidthSizeClass,
    modifier: Modifier = Modifier
) {
    val navigationType: MyCityNavigationType
    val contentType: MyCityContentType

    when (windowSize) {
        WindowWidthSizeClass.Compact -> {
            navigationType = MyCityNavigationType.BOTTOM_NAVIGATION
            contentType = MyCityContentType.LIST_ONLY
        }
        WindowWidthSizeClass.Medium -> {
            navigationType = MyCityNavigationType.NAVIGATION_RAIL
            contentType = MyCityContentType.LIST_ONLY
        }
        WindowWidthSizeClass.Expanded -> {
            navigationType = MyCityNavigationType.PERMANENT_NAVIGATION_DRAWER
            contentType = MyCityContentType.LIST_AND_DETAIL
        }
        else -> {
            navigationType = MyCityNavigationType.BOTTOM_NAVIGATION
            contentType = MyCityContentType.LIST_ONLY
        }
    }

    val viewModel: MyCityViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = MyCityScreen.valueOf(
        backStackEntry?.destination?.route ?: MyCityScreen.Categories.name
    )

    Scaffold(
        topBar = {
            MyCityTopAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                onBackClick = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        if (contentType == MyCityContentType.LIST_AND_DETAIL) {
            MyCityExpandedScreen(
                uiState = uiState,
                onCategoryClick = { viewModel.updateCurrentCategory(it) },
                onRecommendationClick = { viewModel.updateSelectedRecommendation(it) },
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            MyCityNavHost(
                navController = navController,
                uiState = uiState,
                viewModel = viewModel,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCityTopAppBar(
    currentScreen: MyCityScreen,
    canNavigateBack: Boolean,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(stringResource(R.string.app_name)) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        }
    )
}

@Composable
fun MyCityNavHost(
    navController: NavHostController,
    uiState: MyCityUiState,
    viewModel: MyCityViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = MyCityScreen.Categories.name,
        modifier = modifier
    ) {
        composable(route = MyCityScreen.Categories.name) {
            CategoryListScreen(
                categories = uiState.categories,
                onCategoryClick = {
                    viewModel.updateCurrentCategory(it)
                    navController.navigate(MyCityScreen.Recommendations.name)
                },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        composable(route = MyCityScreen.Recommendations.name) {
            RecommendationListScreen(
                recommendations = uiState.recommendations,
                onRecommendationClick = {
                    viewModel.updateSelectedRecommendation(it)
                    navController.navigate(MyCityScreen.Details.name)
                },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        composable(route = MyCityScreen.Details.name) {
            uiState.selectedRecommendation?.let { recommendation ->
                RecommendationDetailScreen(
                    recommendation = recommendation
                )
            }
        }
    }
}

@Composable
fun MyCityExpandedScreen(
    uiState: MyCityUiState,
    onCategoryClick: (com.prabhas.mycity.model.Category) -> Unit,
    onRecommendationClick: (com.prabhas.mycity.model.Recommendation) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxSize()) {
        CategoryListScreen(
            categories = uiState.categories,
            onCategoryClick = onCategoryClick,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        )
        RecommendationListScreen(
            recommendations = uiState.recommendations,
            onRecommendationClick = onRecommendationClick,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        )
        Surface(
            modifier = Modifier
                .weight(1.5f)
                .fillMaxHeight(),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            if (uiState.selectedRecommendation != null) {
                RecommendationDetailScreen(
                    recommendation = uiState.selectedRecommendation
                )
            } else {
                Text(
                    text = "Select a recommendation",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
