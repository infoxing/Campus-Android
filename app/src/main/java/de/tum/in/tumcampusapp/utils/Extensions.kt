package de.tum.`in`.tumcampusapp.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import com.google.android.material.button.MaterialButton
import androidx.drawerlayout.widget.DrawerLayout
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Callback
import com.squareup.picasso.RequestCreator
import de.tum.`in`.tumcampusapp.ui.generic.drawer.SideNavigationItem

/**
 * Executes the block and return null in case of an [Exception].
 *
 * @param block The block of code to execute
 */
inline fun <T> tryOrNull(block: () -> T): T? {
    return try {
        block()
    } catch (_: Exception) {
        null
    }
}

fun RequestCreator.into(target: ImageView, completion: () -> Unit) {
    into(target, object : Callback {
        override fun onSuccess() {
            completion()
        }

        override fun onError(e: java.lang.Exception?) {
            completion()
        }
    })
}

val Menu.items: List<MenuItem>
    get() = (0 until size()).map { getItem(it) }

val Menu.allItems: List<MenuItem>
    get() {
        return items.flatMap { item ->
            if (item.hasSubMenu()) {
                item.subMenu.allItems
            } else {
                listOf(item)
            }
        }
    }

fun Menu.add(context: Context, item: SideNavigationItem, options: Bundle = Bundle()) {
    add(item.titleRes)
            .apply {
                setIcon(item.iconRes)
                intent = Intent(context, item.activity).apply { putExtras(options) }
            }
}

fun TextView.setTextOrHide(resId: Int?) {
    resId?.let {
        text = context.getString(it)
        return
    }

    visibility = View.GONE
}

fun MaterialButton.setTextOrHide(resId: Int?) {
    resId?.let {
        text = context.getString(it)
        return
    }

    visibility = View.GONE
}

fun ImageView.setImageResourceOrHide(resId: Int?) {
    resId?.let {
        setImageResource(it)
        return
    }

    visibility = View.GONE
}

fun DrawerLayout.closeDrawers(callback: () -> Unit) {
    addDrawerListener(object : DrawerLayout.DrawerListener {
        override fun onDrawerStateChanged(newState: Int) = Unit

        override fun onDrawerSlide(drawerView: View, slideOffset: Float) = Unit

        override fun onDrawerClosed(drawerView: View) {
            callback()
            removeDrawerListener(this)
        }

        override fun onDrawerOpened(drawerView: View) = Unit
    })
    closeDrawers()
}

fun <T> LiveData<T>.observeNonNull(owner: LifecycleOwner, callback: (T) -> Unit) {
    observe(owner, Observer<T> { value ->
        value?.let {
            callback(it)
        }
    })
}

fun TextView.addCompoundDrawablesWithIntrinsicBounds(
        start: Drawable? = null, top: Drawable? = null, right: Drawable? = null, bottom: Drawable? = null) {
    setCompoundDrawablesWithIntrinsicBounds(start, top, right, bottom)
}

fun TextView.addCompoundDrawablesWithIntrinsicBounds(
        start: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0) {
    setCompoundDrawablesWithIntrinsicBounds(start, top, right, bottom)
}
