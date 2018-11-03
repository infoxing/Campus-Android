package de.tum.`in`.tumcampusapp.component.ui.news

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.model.news.NewsSources
import de.tum.`in`.tumcampusapp.component.ui.news.viewmodel.NewsViewEntity
import de.tum.`in`.tumcampusapp.component.ui.overview.card.CardViewHolder
import de.tum.`in`.tumcampusapp.component.ui.tufilm.FilmCard
import de.tum.`in`.tumcampusapp.utils.addCompoundDrawablesWithIntrinsicBounds
import org.joda.time.format.DateTimeFormat
import java.util.regex.Pattern

class NewsViewHolder(
        itemView: View,
        private val showOptionsButton: Boolean = true
) : CardViewHolder(itemView) {

    private val optionsButtonGroup: Group by lazy { itemView.findViewById<Group>(R.id.cardMoreIconGroup) }
    private val imageView: ImageView? by lazy { itemView.findViewById<ImageView>(R.id.news_img) }
    private val titleTextView: TextView? by lazy { itemView.findViewById<TextView>(R.id.news_title) }
    private val dateTextView: TextView by lazy { itemView.findViewById<TextView>(R.id.news_src_date) }
    private val sourceTextView: TextView by lazy { itemView.findViewById<TextView>(R.id.news_src_title) }

    fun bind(newsItem: NewsViewEntity, newsSource: NewsSources) = with(itemView) {
        val card = if (newsItem.isFilm) FilmCard(context) else NewsCard(context)
        card.setNews(newsItem)
        currentCard = card

        val dateFormatter = DateTimeFormat.mediumDate()
        dateTextView.text = dateFormatter.print(newsItem.date)

        optionsButtonGroup.visibility = if (showOptionsButton) VISIBLE else GONE

        loadNewsSourceInformation(context, newsSource)

        when (itemViewType) {
            R.layout.card_news_film_item -> bindFilmItem(newsItem)
            else -> bindNews(newsItem)
        }
    }

    private fun loadNewsSourceInformation(context: Context, newsSource: NewsSources) {
        sourceTextView.text = newsSource.title

        val newsSourceIcon = newsSource.icon
        if (newsSourceIcon.isNotBlank() && newsSourceIcon != "null") {
            Picasso.get().load(newsSourceIcon).into(object : Target {
                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    val drawable = BitmapDrawable(context.resources, bitmap)
                    sourceTextView.addCompoundDrawablesWithIntrinsicBounds(start = drawable)
                }

                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) = Unit

                override fun onPrepareLoad(placeHolderDrawable: Drawable?) = Unit
            })
        }
    }

    private fun bindFilmItem(newsItem: NewsViewEntity) {
        Picasso.get()
                .load(newsItem.imageUrl)
                .into(imageView)

        titleTextView?.text = COMPILE.matcher(newsItem.title).replaceAll("")
    }

    private fun bindNews(newsItem: NewsViewEntity) {
        val imageUrl = newsItem.imageUrl
        if (imageUrl.isNotEmpty()) {
            loadNewsImage(imageUrl)
        } else {
            imageView?.visibility = View.GONE
        }

        val showTitle = newsItem.isNewspread.not()
        titleTextView?.visibility = if (showTitle) VISIBLE else View.GONE

        if (showTitle) {
            titleTextView?.text = newsItem.title
        }
    }

    private fun loadNewsImage(url: String) {
        Picasso.get()
                .load(url)
                .into(imageView, object : Callback {
                    override fun onSuccess() = Unit

                    override fun onError(e: Exception?) {
                        imageView?.visibility = View.GONE
                    }
                })
    }

    companion object {
        private val COMPILE = Pattern.compile("^[0-9]+\\. [0-9]+\\. [0-9]+:[ ]*")
    }

}
