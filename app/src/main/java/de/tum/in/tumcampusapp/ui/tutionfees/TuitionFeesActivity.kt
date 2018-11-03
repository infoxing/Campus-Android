package de.tum.`in`.tumcampusapp.ui.tutionfees

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import de.tum.`in`.tumcampusapp.R
import de.tum.`in`.tumcampusapp.api.shared.CacheControl
import de.tum.`in`.tumcampusapp.ui.generic.activity.ActivityForAccessingTumOnline
import de.tum.`in`.tumcampusapp.model.tuition.TuitionList
import de.tum.`in`.tumcampusapp.ui.tutionfees.viewmodel.TuitionViewEntity

/**
 * Activity to show the user's tuition fees status
 */
class TuitionFeesActivity : ActivityForAccessingTumOnline<TuitionList>(R.layout.activity_tuitionfees) {

    private val amountTextView by lazy { findViewById<TextView>(R.id.amountTextView) }
    private val deadlineTextView by lazy { findViewById<TextView>(R.id.deadlineTextView) }
    private val semesterTextView by lazy { findViewById<TextView>(R.id.semesterTextView) }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val button = findViewById<MaterialButton>(R.id.financialAidButton)
        button.setOnClickListener {
            val url = getString(R.string.student_financial_aid_link)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }

        refreshData(CacheControl.USE_CACHE)
    }

    override fun onRefresh() {
        refreshData(CacheControl.BYPASS_CACHE)
    }

    private fun refreshData(cacheControl: CacheControl) {
        val apiCall = apiClient.getTuitionFeesStatus(cacheControl)
        fetch(apiCall)
    }

    override fun onDownloadSuccessful(response: TuitionList) {
        val tuition = response.tuitions.first()
        val viewEntity = TuitionViewEntity.create(this, tuition)

        amountTextView.text = viewEntity.formattedAmountText

        val formattedDeadline = viewEntity.longFormattedDeadline
        deadlineTextView.text = getString(R.string.due_on_format_string, formattedDeadline)

        semesterTextView.text = tuition.semester
        amountTextView.setTextColor(viewEntity.color)
    }

}
