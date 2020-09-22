package marco.a.aguilar.hourly

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

// AppCompatActivity extends FragmentActivity so we don't need to make TestActivity
// extend from FragmentActivity directly
class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

    }

}