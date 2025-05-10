package com.pixelbrew.qredi.ui.components.sidemenu.components

sealed class Screen(val route: String) {
    object Admin : Screen("admin")
    object Collect : Screen("collect")
    object Reprint : Screen("reprint")
    object Statistics : Screen("statistics")
    object Settings : Screen("settings")
    object Customer : Screen("customer")
    object Loan : Screen("loan")
    object Spent : Screen("spent")

    companion object {
        fun fromRoute(route: String): Screen {
            return when (route) {
                "admin" -> Admin
                "collect" -> Collect
                "reprint" -> Reprint
                "customer" -> Customer
                "loan" -> Loan
                "statistics" -> Statistics
                "settings" -> Settings
                "spent" -> Spent
                else -> Admin // default
            }
        }
    }
}
