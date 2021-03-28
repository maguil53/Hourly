package marco.a.aguilar.hourly.adapter

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.progress_item.view.*
import marco.a.aguilar.hourly.R
import marco.a.aguilar.hourly.enums.BlockType
import marco.a.aguilar.hourly.models.HourBlock
import java.util.*

class ProgressAdapter(private var hourBlocks: List<HourBlock>) :
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

        holder.itemView.textview_progress_hour.text = HourBlock.getTime(hourBlocks[position].time)

        val calendar: Calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        if((hour > hourBlocks[position].time)) {

            /**
             * We only want to make a decision to change a Block color once the time has changed,
             * otherwise we leave it in a state of limbo.
             */
            if(!(hourBlocks[position].isComplete)) {
                Log.d(TAG, "onBindViewHolder: Changing block to Red")
                holder.itemView.view_hour_block.setBackgroundResource(R.drawable.hour_block_background_incomplete)

            } else {
                Log.d(TAG, "onBindViewHolder: Changing block to Green")
                holder.itemView.view_hour_block.setBackgroundResource(R.drawable.hour_block_background_complete)
            }

        } else {
            Log.d(TAG, "onBindViewHolder: Changing block to Grey")
            holder.itemView.view_hour_block.setBackgroundResource(R.drawable.hour_block_background_limbo)
        }


        /**
         * Place this line AFTER the code above.
         * No we don't have to worry if an HourBlock isComplete or not,
         * as long it is of BlockType.RECOVER, then it will be colored green.
         */
        if(hourBlocks[position].blockType == BlockType.RECOVER)
            holder.itemView.view_hour_block.setBackgroundResource(R.drawable.hour_block_background_complete)


    }

    override fun getItemCount() = hourBlocks.size


    fun setHourBlocks(newHourBlocks: List<HourBlock>) {
        hourBlocks = newHourBlocks
        notifyDataSetChanged()
    }
}