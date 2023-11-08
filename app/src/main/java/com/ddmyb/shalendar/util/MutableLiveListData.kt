package com.ddmyb.shalendar.util

import androidx.lifecycle.MutableLiveData

class MutableLiveListData<T> : MutableLiveData<MutableList<T>>() {
    val list = mutableListOf<T>()

    private var insertObserver: (Int) -> Unit = {}
    private var removeObserver: (Int) -> Unit = {}
    private var changeObserver: (Int) -> Unit = {}

    init {
        postValue(list)
    }

    fun add(element: T) {
        list.add(element)
        insertObserver(list.size-1)

        postValue(list)
    }

    fun add(index: Int, element: T) {
        list.add(index, element)
        insertObserver(index)

        postValue(list)
    }

    fun addAll(index: Int, elements: Collection<T>) {
        for ((i, element) in elements.withIndex()) {
            val insertIndex = index + i

            list.add(insertIndex, element)
            insertObserver(insertIndex)
        }

        postValue(list)
    }

    fun addAll(elements: Collection<T>) {
        val lastIndex = list.size

        for ((i, element) in elements.withIndex()) {
            val insertIndex = lastIndex + i

            list.add(insertIndex, element)
            insertObserver(insertIndex)
        }

        postValue(list)
    }

    fun remove(element: T): Boolean {
        val index = list.indexOf(element)

        if (index == -1) return false

        list.remove(element)
        removeObserver(index)

        postValue(list)

        return true
    }

    fun removeAll(elements: Collection<T>): Boolean {
        var returnValue = false

        for (element in elements) {
            val index = list.indexOf(element)

            if (index == -1) continue

            list.removeAt(index)
            removeObserver(index)
            returnValue = true
        }

        postValue(list)

        return returnValue
    }

    fun removeAt(index: Int): T {
        val returnValue = list.removeAt(index)
        removeObserver(index)

        postValue(list)

        return returnValue
    }

    fun replaceAt(index: Int, value: T) {
        list[index] = value
        changeObserver(index)
    }

    fun replaceAll(operator: (T) -> Pair<T, Boolean>) {
        for (i in list.indices) {
            val operatorResult = operator(list[i])

            if (!operatorResult.second) continue

            list[i] = operatorResult.first
            changeObserver(i)
        }

        postValue(list)
    }

    fun retainAll(elements: Collection<T>): Boolean {
        var returnValue = false

        for ((i, element) in list.withIndex()) {
            if (elements.contains(element)) continue

            list.removeAt(i)
            removeObserver(i)
            returnValue = true
        }

        postValue(list)

        return returnValue
    }

    fun clear() {
        while(list.size > 0) {
            removeAt(0)
        }
        postValue(list)
    }

    fun observeInsert(insertedAt: (Int) -> Unit) {
        insertObserver = insertedAt
    }
    fun observeRemove(removedAt: (Int) -> Unit) {
        removeObserver = removedAt
    }
    fun observeChange(changedAt: (Int) -> Unit) {
        changeObserver = changedAt
    }

}