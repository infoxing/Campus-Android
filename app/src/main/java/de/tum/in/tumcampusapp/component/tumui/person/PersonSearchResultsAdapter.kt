package de.tum.`in`.tumcampusapp.component.tumui.person

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.component.tumui.person.viewmodel.PersonViewEntity
import kotlinx.android.synthetic.main.person_search_result_item.view.*

class PersonSearchResultsAdapter(
        private var items: List<PersonViewEntity>,
        private val listener: PersonSearchResultsItemListener
) : RecyclerView.Adapter<PersonSearchResultsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.person_search_result_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], listener)
    }

    override fun getItemCount() = items.size

    fun update(items: List<PersonViewEntity>) {
        this.items = items
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(person: PersonViewEntity, listener: PersonSearchResultsItemListener) = with(itemView) {
            textView.text = person.fullName
            setOnClickListener { listener.onItemSelected(person) }
        }

    }

}