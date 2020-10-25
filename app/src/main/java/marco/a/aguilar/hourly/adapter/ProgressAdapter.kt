package marco.a.aguilar.hourly.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.progress_item.view.*
import marco.a.aguilar.hourly.R
import marco.a.aguilar.hourly.models.HourBlock

class ProgressAdapter(private val hourBlocks: List<HourBlock>) :
    RecyclerView.Adapter<ProgressAdapter.ProgressViewHolder>() {

    class ProgressViewHolder(private val progressItem: View): RecyclerView.ViewHolder(progressItem)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProgressAdapter.ProgressViewHolder {

        val progressItem = LayoutInflater.from(parent.context)
            .inflate(R.layout.progress_item, parent, false)

        return ProgressViewHolder(progressItem)
    }

    override fun onBindViewHolder(holder: ProgressViewHolder, position: Int) {
        holder.itemView.textview_progress_hour.text = (hourBlocks[position].time).toString()
    }

    override fun getItemCount() = hourBlocks.size
}