package com.cityof.glendale.utils

import com.cityof.glendale.network.responses.Result
import com.cityof.glendale.network.responses.onComplete
import com.cityof.glendale.network.responses.onError
import com.cityof.glendale.network.responses.onSuccess


interface Paginator<R, T> {
    suspend fun loadItems()
    fun reset()
}


class DefaultPaginator<Page, Item>(
    private val page: Page, //PAGE NUMBER
    private inline val onRequest: suspend (nextPage: Page) -> Result<List<Item>>,
    private inline val onSuccess: suspend (items: List<Item>, nextPage: Page) -> Unit,
    private inline val onError: suspend (Throwable?) -> Unit,
    private inline val onLoadUpdated: (Boolean) -> Unit,
    private inline val getNextPage: suspend (List<Item>) -> Page,
) : Paginator<Page, Item> {

    private var currentPage = page
    private var isMakingRequest = false

    override suspend fun loadItems() {

        if (isMakingRequest) {
            return
        }

        isMakingRequest = true
        onLoadUpdated(true)
        val result = onRequest(currentPage)
        isMakingRequest = false
        result.onSuccess { items->
            currentPage = getNextPage(items)
            onSuccess(items, currentPage)
        }.onError {
           onError(it)
        }.onComplete {
           onLoadUpdated(false)
        }

    }

    override fun reset() {
        currentPage = page
    }

}