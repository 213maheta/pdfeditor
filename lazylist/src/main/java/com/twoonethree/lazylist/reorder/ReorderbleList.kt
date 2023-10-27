package com.twoonethree.lazylist.reorder

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.lifecycle.viewmodel.compose.viewModel


fun Modifier.reOrderableItem(index:Int): Modifier = composed {
    val vm = viewModel<ReorderableViewModel>()
    val offsetY: Float by animateFloatAsState(if (index == vm.selectedIndex.value) vm.offsetVertical.value - vm.delta else 0f)

    this.graphicsLayer {
        translationY = offsetY
        shadowElevation = if (index == vm.selectedIndex.value) 10f else 0f
    }
}

fun Modifier.reOrderableList(
    stateList: LazyListState,
    swapFun: (Int, Int) -> Unit,
): Modifier = composed {

    val vm = viewModel<ReorderableViewModel>()

    LaunchedEffect(key1 = Unit)
    {
        vm.listState = stateList
        vm.size = stateList.layoutInfo.visibleItemsInfo[0].size
        vm.swap = swapFun
    }
    this.pointerInput(Unit) {
            this.detectDragGesturesAfterLongPress(
                onDragStart = { offset ->
                    vm.onDragStart(offset)
                },
                onDragEnd = {
                    vm.onDragCancelOrEnd()
                },
                onDragCancel = {
                    vm.onDragCancelOrEnd()
                },
                onDrag = { change: PointerInputChange, dragAmount: Offset ->
                    vm.onDrag(dragAmount)
                }
            )
        }
}

fun <T> SnapshotStateList<T>.swap(first:Int, second:Int)
{
    val temp = this[first]
    this[first] = this[second]
    this[second] = temp
}
