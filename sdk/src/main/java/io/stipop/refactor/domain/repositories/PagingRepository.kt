package io.stipop.refactor.domain.repositories

import android.util.Log
import io.reactivex.rxjava3.core.Observable
import io.stipop.refactor.domain.entities.SPPageMap
import io.stipop.refactor.domain.entities.SPUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

interface PagingRepository<T> {
    var list: List<T>?
    var pageMap: SPPageMap?

    val listChanges: Observable<List<T>>

    fun getPageNumber(offset: Int?, pageMap: SPPageMap?): Int {
        return (pageMap?.pageNumber ?: 0)
    }

    fun getLimit(pageMap: SPPageMap?): Int {
        return pageMap?.onePageCountRow ?: 20
    }

    fun getHasMore(list: List<T>?, pageMap: SPPageMap?, offset: Int): Boolean {
        return list?.let {

            if (it.size - 1 == offset) {
                return true
            }

            pageMap?.let {

                pageMap.pageCount > pageMap.pageNumber

            } ?: false


        } ?: true
    }

    val isEmpty: Boolean
        get() {
            return list?.isEmpty() ?: true
        }

    fun onLoadList(
        user: SPUser,
        keyword: String,
        offset: Int?,
        limit: Int? = 20,
    )

    fun onLoadMoreList(
        user: SPUser,
        keyword: String,
        offset: Int,
        limit: Int? = 20,
    ) {
        Log.d(this::class.simpleName, "onLoadMoreList : \n" +
                "user -> $user \n" +
                "keyword -> $keyword \n" +
                "offset -> $offset \n" +
                "limit -> $limit \n"
        )
        Log.d(this::class.simpleName, "has more = ${getHasMore(list, pageMap, offset)}")
        Log.d(this::class.simpleName, "has valid position = ${getValidLoadPosition(list, pageMap, offset)}")


        val _offset = if (offset < 0) {
            list = null
            pageMap = null
            0
        } else {
            offset
        }

        if (getHasMore(list, pageMap, _offset) && getValidLoadPosition(list, pageMap, _offset)) {
            runBlocking(Dispatchers.IO) {



                onLoadList(user, keyword, _offset, limit)
            }
        }
    }

    fun getValidLoadPosition(list: List<T>?, pageMap: SPPageMap?, offset: Int): Boolean {
        return list?.let {

                list ->

            pageMap?.let {

                    pageMap ->

                list.size - pageMap.onePageCountRow * 2 < offset


            } ?: false


        } ?: true
    }
}