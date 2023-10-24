package com.twoonethree.pdfeditor.reorderblelist

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.twoonethree.pdfeditor.R

@Composable
fun ReorderbleVerticalList(

) {
    val vm = viewModel<ReorderableViewModel>()
    vm.listState = rememberLazyListState()

    LaunchedEffect(key1 = Unit)
    {
        vm.size = vm.listState.layoutInfo.visibleItemsInfo[0].size
    }

    LazyColumn(
        state = vm.listState,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .pointerInput(Unit) {
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

    ) {
        itemsIndexed(vm.tempList) { index, value ->
            ItemReorderable(index, value)
        }
    }
}

@Composable
fun ItemReorderable(
    index: Int,
    value: Any,
) {
    val vm = viewModel<ReorderableViewModel>()
    val offsetY: Float by animateFloatAsState(if (index == vm.selectedIndex.value) vm.offsetVertical.value - vm.delta else 0f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                translationY = offsetY
                shadowElevation = if (index == vm.selectedIndex.value) 10f else 0f
            }
            .padding(4.dp)
            .background(color = Color.Gray)
            .border(
                width = 2.dp,
                color = colorResource(id = R.color.orange),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(10.dp)
    )
    {
        Text(
            text = value.toString(),
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier
                .padding(start = 20.dp)
                .weight(0.4f)
        )

        Icon(
            imageVector = Icons.Default.KeyboardArrowUp,
            contentDescription = stringResource(R.string.up),
            modifier = Modifier
                .padding(end = 20.dp)
                .weight(0.2f)
        )

        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = stringResource(R.string.down),
            modifier = Modifier
                .padding(end = 20.dp)
                .weight(0.2f)
        )

        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = stringResource(R.string.delete),
            modifier = Modifier
                .padding(end = 20.dp)
                .weight(0.2f)
        )
    }

}