package com.example.payndrink

import com.example.payndrink.database.Item

class ShoppingCart

{fun addItem(item) {
    val cart = ShoppingCart.getCart()

    val targetItem = cart.singleOrNull { it.product.id == Item.product.id }

    if (targetItem == null) {
        Item.quantity++
        cart.add(Item)
    } else {

        targetItem.quantity++
    }
    ShoppingCart.saveCart(cart)

}

    fun removeItem(Item: Item, context: Context) {

        val cart = ShoppingCart.getCart()


        val targetItem = cart.singleOrNull { it.product.id == Item.product.id }

        if (targetItem != null) {

            if (targetItem.quantity > 0) {

                Toast.makeText(context, ”Hienoa”, Toast.LENGTH_SHORT).show()
                targetItem.quantity--
            } else {
                cart.remove(targetItem)
            }

        }


    }