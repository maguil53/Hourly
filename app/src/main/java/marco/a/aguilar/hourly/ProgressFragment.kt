package marco.a.aguilar.hourly

import android.content.ContentValues.TAG
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import marco.a.aguilar.hourly.adapter.ProgressAdapter
import marco.a.aguilar.hourly.adapter.TasksAdapter
import marco.a.aguilar.hourly.models.HourBlock
import marco.a.aguilar.hourly.viewmodel.ProgressViewModel

@RequiresApi(Build.VERSION_CODES.N)
class ProgressFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: ProgressAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    private val viewModel: ProgressViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewmodel after onActivityCreted()"
        }

            ViewModelProvider(this, ProgressViewModel.ProgressViewModelFactory(activity.application))
        .get(ProgressViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        /**
         * @4:16pm the value for calendar.get(Calendar.HOUR_OF_DAY) was 16.
         * This means the value could be from 1-24
         *
         *      val calendar: Calendar = Calendar.getInstance()
         *      Log.d(TAG, "onCreateView: Calendar.Hour: " + calendar.get(Calendar.HOUR_OF_DAY))
         */
        val view: View = inflater.inflate(R.layout.fragment_progress, container, false)

        initRecyclerView(view)

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.hourBlocks.observe(viewLifecycleOwner) {
            viewAdapter.setHourBlocks(it)
        }
    }


    fun initRecyclerView(view: View) {
        viewManager = GridLayoutManager(activity, 4)

        /**
         * These will be temporary HourBlocks that will just show Grey
         * squares (According to our logic inside Progress Adapter) while the
         * database gets set for the first time.
         */
        viewAdapter = ProgressAdapter(HourBlock.generateBlankHourBlocks())


        recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_progress).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a grid layout manager
            layoutManager = viewManager

            // specify an viewAdapter
            adapter = viewAdapter
        }
    }

}