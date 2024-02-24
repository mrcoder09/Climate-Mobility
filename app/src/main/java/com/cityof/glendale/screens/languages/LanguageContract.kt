package com.cityof.glendale.screens.languages

import com.cityof.glendale.utils.LanguageItem

interface LanguageContract {

    data class State(
        var list: List<LanguageItem> = emptyList(),
        var isLoading: Boolean = false
    )

    sealed class Intent {
        data class LanguageSelected(var item: LanguageItem) : Intent()

        object ContinueClicked: Intent()
    }

    sealed class NavAction {
        object NavLogin : NavAction()
    }
}