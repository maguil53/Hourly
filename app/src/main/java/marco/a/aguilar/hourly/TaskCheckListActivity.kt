package marco.a.aguilar.hourly

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import marco.a.aguilar.hourly.models.TasksCompletedInfo

class TaskCheckListActivity : AppCompatActivity() {

    private val TAG = "TaskCheckListActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_check_list)

        val tasksCompletedInfo = intent.getParcelableExtra<TasksCompletedInfo>("Tasks")

        /**
         * TasksCompletedInfo will most likely never be null since we prepopulate the db with HourBlocks.
         * We only need to check List<Task> for null.
         *
         * Also, I don't think we're ever using the List<Task> inside HourBlock so we might want
         * to get rid of that too.
         */
        if(tasksCompletedInfo != null) {
            Log.d(TAG, "onCreate: tasksCompletedInfo: $tasksCompletedInfo")
        }

        Log.d(TAG, "onCreate: ")
    }
}