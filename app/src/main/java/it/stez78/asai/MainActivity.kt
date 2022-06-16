package it.stez78.asai

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import it.stez78.asai.databinding.ActivityMainBinding
import it.stez78.asai.ml.StreetSignalModel90TunedNoNormMetadata
import it.stez78.asai.ml.StreetSignsPredictor
import org.tensorflow.lite.support.image.TensorImage


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var predictor: StreetSignsPredictor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        predictor = StreetSignsPredictor(baseContext)
        val spinner = binding.modelSelectSpinner
        ArrayAdapter.createFromResource(
            this,
            R.array.available_models,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                binding.outputText.text = ""
                result.data?.extras?.get("drawableId")?.let { drawableId ->
                    val bitmap = ContextCompat.getDrawable(baseContext, drawableId as Int)?.toBitmap();
                    binding.anteprima.setImageBitmap(bitmap)
                    binding.anteprima.scaleType=ImageView.ScaleType.CENTER_CROP
                    binding.evaluateButton.isEnabled = true
                }
            }
        }

        binding.takePhotoButton.setOnClickListener {
            startForResult.launch(Intent(this, PhotoSelectionActivity::class.java))
        }

        binding.evaluateButton.isEnabled = false
        binding.evaluateButton.setOnClickListener {
            predictor.setActiveModel(spinner.selectedItemPosition)
            val probability = predictor.getProbability(binding.anteprima.drawable.toBitmap())
            val spannedResult = SpannableStringBuilder()
            probability
                .sortedByDescending { p -> p.score }
                .take(5)
                .forEachIndexed { index, p ->
                    val start = spannedResult.length
                    spannedResult.append(p.label)
                    spannedResult.setSpan(
                        RelativeSizeSpan(p.score/5),
                        start,
                        spannedResult.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    if (index == 0){
                        spannedResult.setSpan(
                            StyleSpan(android.graphics.Typeface.BOLD),
                            start,
                            spannedResult.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                    spannedResult.append("\n")
                }
            binding.outputText.text = spannedResult
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