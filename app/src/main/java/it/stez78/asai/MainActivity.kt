package it.stez78.asai

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import it.stez78.asai.databinding.ActivityMainBinding
import it.stez78.asai.ml.StreetSignalModel90TunedNoNormMetadata
import org.tensorflow.lite.support.image.TensorImage

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.extras?.get("drawableId")?.let { drawableId ->
                    val bitmap = ContextCompat.getDrawable(baseContext, drawableId as Int)?.toBitmap();
                    binding.anteprima.setImageBitmap(bitmap)
                    binding.anteprima.scaleType=ImageView.ScaleType.CENTER_CROP
                }
                // Handle the Intent

            }
        }

        binding.takePhotoButton.setOnClickListener {
            startForResult.launch(Intent(this, PhotoSelectionActivity::class.java))
        }

        binding.evaluateButton.setOnClickListener {
            val image = TensorImage.fromBitmap(binding.anteprima.drawable.toBitmap())
            val model = StreetSignalModel90TunedNoNormMetadata.newInstance(baseContext)
            val outputs = model.process(image)
            val probability = outputs.probabilityAsCategoryList
            val result = probability
                .sortedByDescending { p -> p.score }
                .take(5)
                .map { p -> "${p.label} -> S: ${p.score}"}
                .joinToString("\n")
            binding.outputText.text = result
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