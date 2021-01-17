package marco.a.aguilar.hourly.adapter

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.progress_item.view.*
import marco.a.aguilar.hourly.R
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
        holder.itemView.textview_progress_hour.text = (hourBlocks[position].time).toString()

        /**
         * Let's try changing the color here first. We can change this later depending on
         * things we discover later. Not sure if this code will be executed again when
         * there's an update to the Database but I'm gonna put it here for now.
         *
         * Damn, not sure how I'm gonna handle Red blocks since all HourBlocks start off
         * with isComplete equal to false.
         *
         * Wait I think this will work, we can just check the HourBlock.time attribute
         * and compare it to the current hour...Later on our scheduled code will be able to
         * make isComplete == true. Also, this is probably the behavior we want since
         * when the user opens the app we want them to see they're already behind.
         *
         * Hmmm, we should probably see if we can figure out how to make the user create their
         * to-do list when the user opens the app for the very first time (if that's possible,
         * I think it should be though.)
         */

        val calendar: Calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        // Log.d(TAG, "onCreateView: Calendar.Hour: " + calendar.get(Calendar.HOUR_OF_DAY))

        if((hour > hourBlocks[position].time)) {
            Log.d(TAG, "onBindViewHolder: hour = $hour")
            Log.d(TAG, "onBindViewHolder: hourBlocks[position].time = " + hourBlocks[position].time)
            Log.d(TAG, "onBindViewHolder: isComplete value: " + hourBlocks[position].isComplete)

            /**
             * We only want to make a decision to change a Block color once the time has changed,
             * otherwise we leave it in a state of limbo. This is to prevent tasks from
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


    }

    override fun getItemCount() = hourBlocks.size


    fun setHourBlocks(newHourBlocks: List<HourBlock>) {
        hourBlocks = newHourBlocks
        notifyDataSetChanged()
    }
}