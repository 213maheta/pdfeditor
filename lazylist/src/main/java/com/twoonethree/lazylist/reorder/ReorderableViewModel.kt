package com.twoonethree.lazylist.reorder

import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class ReorderableViewModel:ViewModel() {

    lateinit var listState:LazyListState
    val selectedIndex = mutableStateOf(-1)
    val hoverIndex = mutableStateOf(-1)
    var size = 0
    var delta = 0f
    val initialOffset = mutableStateOf(-1)
    val offsetVertical = mutableStateOf(0f)
    val draggedValue = mutableStateOf(0f)
    var scrollLock = false
    lateinit var swap:(selected:Int, hover:Int)->Unit
    fun swapPos(down: Boolean)
    {
        if(hoverIndex.value == selectedIndex.value)
           return

        swap(selectedIndex.value, hoverIndex.value)
        selectedIndex.value = hoverIndex.value

        delta = if(down)offsetVertical.value + size/2 else offsetVertical.value - size/2
    }

    fun checkforOverScroll(value: Int) = viewModelScope.launch {
        scrollLock = true
        if(listState.layoutInfo.viewportEndOffset < value+size)
        {
            listState.scrollBy(value.toFloat()+size - listState.layoutInfo.viewportEndOffset )
        }
        else if(listState.layoutInfo.viewportStartOffset > value)
        {
            listState.scrollBy( value - listState.layoutInfo.viewportStartOffset.toFloat())
        }
        scrollLock = false
        cancel()
    }

    fun onDragStart(offset: Offset)
    {
        listState.layoutInfo.visibleItemsInfo
            .firstOrNull { item ->
                offset.y.toInt() in item.offset..item.offset + item.size
            }
            ?.also {
                selectedIndex.value = it.index
                initialOffset.value = it.offset
            }
    }

    fun onDragCancelOrEnd()
    {
        selectedIndex.value = -1
        offsetVertical.value = 0f
        delta = 0f
        draggedValue.value = -1f
    }

    fun onDrag(dragAmount: Offset)
    {
        draggedValue.value += dragAmount.y
        offsetVertical.value = draggedValue.value

        listState.layoutInfo.visibleItemsInfo
            .firstOrNull { item ->
                (offsetVertical.value.toInt() + initialOffset.value + (item.size / 2)) in item.offset..item.offset + item.size
            }
            ?.also {
                hoverIndex.value = it.index
                swapPos(dragAmount.y > 0)

                if (!scrollLock) {
                    checkforOverScroll(offsetVertical.value.toInt() + initialOffset.value)
                }
            }
    }
}