package com.cityof.glendale.navigation

import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import timber.log.Timber


class MyNavActions(navHostController: NavHostController?) {
    val navigateTo = { navBackStackEntry: NavBackStackEntry, route: String ->
        if (navBackStackEntry.lifecycleIsResumed()) {
            navHostController?.navigate(route)
        }
    }

    val navigateUp = { navBackStackEntry: NavBackStackEntry ->
        if (navBackStackEntry.lifecycleIsResumed()) {
            navHostController?.navigateUp()
        }
    }

    val popBackStackAndNavigate =
        { navBackStackEntry: NavBackStackEntry, route: String?, popUpTo: String, inclusive: Boolean ->
            if (navBackStackEntry.lifecycleIsResumed()) {
                navHostController?.popBackStack(popUpTo, inclusive)
                route?.let {
                    navHostController?.navigate(route)
                }
            }
        }


    fun NavHostController.navigateTo(route: String, popUpTo: String? = null) {
        navigate(route) {
            popUpTo?.let {
                popUpTo(it)
            }
        }
    }

    fun NavHostController.navigateAndReplaceStartRoute(newHomeRoute: String) {
        val isTrue = popBackStack(graph.startDestinationId, true)
        Timber.d("$isTrue")
        graph.setStartDestination(newHomeRoute)
        navigate(newHomeRoute)
    }

    fun NavHostController.navigateAndClean(route: String) {
        navigate(route = route) {
            popUpTo(graph.startDestinationId) { inclusive = true }
        }
        graph.setStartDestination(route)
    }




    fun NavOptionsBuilder.popUpToTop(navController: NavController) {
        popUpTo(navController.currentBackStackEntry?.destination?.route ?: return) {
            inclusive = true
        }
    }
}


fun NavHostController.navLogin(popUpTo: String = Routes.Dashboard.name){
    navigate(Routes.Login.name){
        popUpTo(popUpTo){
            inclusive = true
        }
    }
}

fun NavHostController.removeBackStackEntries(){
    currentBackStackEntry?.savedStateHandle?.let {savedStateHandle ->
        savedStateHandle.keys().forEach {
            savedStateHandle.remove<Any>(it)
        }
    }
}

fun NavHostController.printDetails() {
    val currentBackStackEntry = this.currentBackStackEntry?.destination.toString()
    val previousBackStackEntry = this.previousBackStackEntry?.destination.toString()
    val navGraph = this.currentBackStack.value.toString()
    val currentDestination = this.currentDestination?.displayName
    val currentRoot = this.graph.route

    Timber.d("NavGraph: CurrentRoot: $currentRoot")
    Timber.d("NavGraph: currentBackStackEntry: $currentBackStackEntry")
    Timber.d("NavGraph: previousBackStackEntry: $previousBackStackEntry")
    Timber.d("NavGraph: currentDestination: $currentDestination")
    Timber.d("NavGraph: CurrentGraph: $navGraph")
}

/**
 * If the lifecycle is not resumed it means this NavBackStackEntry already processed a nav event.
 *
 * This is used to de-duplicate navigation events.
 */
private fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED