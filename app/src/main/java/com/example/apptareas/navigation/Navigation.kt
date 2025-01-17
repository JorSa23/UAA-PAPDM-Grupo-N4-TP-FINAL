package com.example.apptareas.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.rememberNavController
import com.example.apptareas.login.LoginScreen
import com.example.apptareas.login.LoginViewModel
import com.example.apptareas.login.SignUpScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.example.apptareas.detail.DetailScreen
import com.example.apptareas.detail.DetailViewModel
import com.example.apptareas.home.Home
import com.example.apptareas.home.HomeViewMode


enum class LoginRoutes{
    Signup,
    SignIn
}
enum class HomeRoutes{
    Home,
    Detail
}
enum class NestedRoutes{
    Main,
    Login
}

@Composable
fun Navigation(
    navController: NavHostController = rememberNavController(),
    loginViewModel: LoginViewModel,
    detailViewModel: DetailViewModel,
    homeViewMode: HomeViewMode
) {
    NavHost(
        navController = navController,
        startDestination = NestedRoutes.Main.name
    ) {
        authGraph(navController, loginViewModel)
        homeGraph(
            navController =
            navController,
            detailViewModel,
            homeViewMode
        )
    }
}

fun NavGraphBuilder.authGraph(
    navController: NavHostController,
    loginViewModel: LoginViewModel,
){
    navigation(
        startDestination = LoginRoutes.SignIn.name,
        route = NestedRoutes.Login.name
    ){
        composable(route = LoginRoutes.SignIn.name) {
            LoginScreen(onNavToHomePage = {
                navController.navigate(NestedRoutes.Main.name) {
                    launchSingleTop = true
                    popUpTo(route = LoginRoutes.SignIn.name) {
                        inclusive = true
                    }
                }
            },
                loginViewModel = loginViewModel

            ) {
                navController.navigate(LoginRoutes.Signup.name) {
                    launchSingleTop = true
                    popUpTo(LoginRoutes.SignIn.name) {
                        inclusive = true
                    }
                }
            }
        }

        composable(route = LoginRoutes.Signup.name) {
            SignUpScreen(onNavToHomePage = {
                navController.navigate(NestedRoutes.Main.name) {
                    popUpTo(LoginRoutes.Signup.name) {
                        inclusive = true
                    }
                }
            },
                loginViewModel = loginViewModel

            ) {
                navController.navigate(LoginRoutes.SignIn.name)

            }
        }
    }
}

fun NavGraphBuilder.homeGraph(
    navController: NavHostController,
    detailViewModel: DetailViewModel,
    homeViewMode: HomeViewMode
){
    navigation(
        startDestination = HomeRoutes.Home.name,
        route = NestedRoutes.Main.name)
    {
        composable(HomeRoutes.Home.name){
            Home(
                homeViewMode = homeViewMode,
                onExamenClick = { examenId ->
                    navController.navigate(
                        HomeRoutes.Detail.name + "?id=$examenId"
                    ){
                        launchSingleTop = true
                    }
                },
                navToDetailPage = {
                    navController.navigate(HomeRoutes.Detail.name)
                }
            ){
                navController.navigate(NestedRoutes.Login.name){
                    launchSingleTop = true
                    popUpTo(0){
                        inclusive = true
                    }
                }
            }
        }

        composable(
            route = HomeRoutes.Detail.name + "?id={id}",
            arguments = listOf(navArgument("id"){
                type = NavType.StringType
                defaultValue = ""
            })
        ){entry ->
            DetailScreen(
                detailViewModel = detailViewModel,
                examenId = entry.arguments?.getString("id") as String,
            ) {
               navController.navigateUp()

            }

        }
    }
}