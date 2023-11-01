import android.widget.Filter
import com.ddmyb.shalendar.data.OwnedCalendar
import com.ddmyb.shalendar.view.calendar_list.adapter.ExpandableListAdapter
import com.ddmyb.shalendar.view.calendar_list.adapter.SearchAdapter

class MyFilter(private val originalList: MutableList<OwnedCalendar>, private val adapter: ExpandableListAdapter) : Filter() {
    override fun performFiltering(constraint: CharSequence?): FilterResults {
        val results = FilterResults()
        val filteredList = mutableListOf<OwnedCalendar>()

        if (constraint.isNullOrEmpty()) {
            // No filter applied, return the original list
            results.count = originalList.size
            results.values = originalList
        } else {
            val query = constraint.toString().toLowerCase()
            // Iterate through the original list and add items that match the query to the filtered list
            for (item in originalList) {
                if (item.text.toLowerCase().contains(query)) {
                    filteredList.add(item)
                }
            }
            results.count = filteredList.size
            results.values = filteredList
        }
        return results
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
        // Update the adapter with the filtered results
        if (results?.values is MutableList<*>) {
            val filteredList = results.values as MutableList<OwnedCalendar>
            //adapter.setItems(filteredList)
        }
    }
}
