package com.ddmyb.shalendar.util
import androidx.lifecycle.MutableLiveData

class MutableLiveListData<T> : MutableLiveData<MutableList<T>>() {
    private val list = mutableListOf<T>()

    private var insertObserver: (Int) -> Unit = {}
    private var removeObserver: (Int) -> Unit = {}
    private var changeObserver: (Int) -> Unit = {}

    init {
        value = list
    }

    fun add(element: T) {
        list.add(element)
        insertObserver(list.size-1)

        value = list
    }

    fun add(index: Int, element: T) {
        list.add(index, element)
        insertObserver(index)

        value = list
    }

    fun addAll(index: Int, elements: Collection<T>) {
        for ((i, element) in elements.withIndex()) {
            val insertIndex = index + i

            list.add(insertIndex, element)
            insertObserver(insertIndex)
        }

        value = list
    }

    fun addAll(elements: Collection<T>) {
        val lastIndex = list.size

        for ((i, element) in elements.withIndex()) {
            val insertIndex = lastIndex + i

            list.add(insertIndex, element)
            insertObserver(insertIndex)
        }

        value = list
    }

    fun remove(element: T): Boolean {
        val index = list.indexOf(element)

        if (index == -1) return false

        list.remove(element)
        removeObserver(index)

        value = list

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

        value = list

        return returnValue
    }

    fun removeAt(index: Int): T {
        val returnValue = list.removeAt(index)
        removeObserver(index)

        value = list

        return returnValue
    }


    fun replaceAll(operator: (T) -> Pair<T, Boolean>) {
        for (i in list.indices) {
            val operatorResult = operator(list[i])

            if (!operatorResult.second) continue

            list[i] = operatorResult.first
            changeObserver(i)
        }

        value = list
    }

    fun retainAll(elements: Collection<T>): Boolean {
        var returnValue = false

        for ((i, element) in list.withIndex()) {
            if (elements.contains(element)) continue

            list.removeAt(i)
            removeObserver(i)
            returnValue = true
        }

        value = list

        return returnValue
    }

    fun clear() {
        list.clear()
        value = list
    }

    fun observeInsert(insertedAt: (Int) -> Unit) {
        insertObserver = insertedAt //callback 함수 등록
    }
    fun observeRemove(removedAt: (Int) -> Unit) {
        removeObserver = removedAt
    }
    fun observeChange(changedAt: (Int) -> Unit) {
        changeObserver = changedAt
    }

}