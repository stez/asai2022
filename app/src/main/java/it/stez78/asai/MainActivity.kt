package it.stez78.asai

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.google.android.material.snackbar.Snackbar
import it.stez78.asai.databinding.ActivityMainBinding
import it.stez78.asai.ml.StreetSignalModel90TunedMetadata
import org.tensorflow.lite.support.image.TensorImage

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.fab.setOnClickListener { view ->

            val bitmap = ContextCompat.getDrawable(baseContext, R.drawable.test_stop_sign)?.toBitmap();

            val image = TensorImage.fromBitmap(bitmap)

            val model = StreetSignalModel90TunedMetadata.newInstance(baseContext)

            val outputs = model.process(image)
            val probability = outputs.probabilityAsCategoryList
            val result = probability
                .sortedByDescending { p -> p.score }
                .take(5)
                .map { p -> "${p.label} -> S: ${p.score}"}
                .joinToString(",")

            Snackbar.make(view, "Risultato:  $result", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()

            model.close()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_info -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

}