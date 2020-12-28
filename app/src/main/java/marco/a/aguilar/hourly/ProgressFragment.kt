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
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import marco.a.aguilar.hourly.adapter.ProgressAdapter
import marco.a.aguilar.hourly.models.HourBlock
import marco.a.aguilar.hourly.viewmodel.ProgressViewModel

import java.util.*


@RequiresApi(Build.VERSION_CODES.N)
class ProgressFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: ProgressAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    /**
     * One way to delay creation of the viewModel until an appropriate lifecycle method is to use
     * lazy. This requires that viewModel not be referenced before onActivityCreated, which we
     * do in this Fragment.
     */

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

        // Inflate layout for this fragment
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        /**
         * Once "it" is no longer null call
         *      viewAdapter.setHourBlocks(it)
         */
        viewModel.hourBlocks.observe(viewLifecycleOwner) {
            Log.d(TAG, "ProgressFragment viewModel item is being Observed")
            Log.d(TAG, "ProgressFragment onViewCreated: it " + it)
//            Log.d(TAG, "Progress Fragment onViewCreated: it.size" + it.size)
            viewAdapter.setHourBlocks(it)
        }


    }



    fun initRecyclerView(view: View) {
        viewManager = GridLayoutManager(activity, 4)

        viewAdapter = ProgressAdapter(HourBlock.generateFakeHourBlocks())

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