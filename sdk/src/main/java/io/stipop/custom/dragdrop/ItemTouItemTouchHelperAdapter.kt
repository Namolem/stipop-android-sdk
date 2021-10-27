package io.stipop

internal interface ItemTouchHelperDelegate {
    fun onItemMove(fromPosition: Int, toPosition: Int): Boolean
    fun onItemMoveCompleted()
    fun onItemRemove(position: Int)
}