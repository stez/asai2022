package it.stez78.asai

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import it.stez78.asai.databinding.ActivityPhotoSelectionBinding

class PhotoSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPhotoSelectionBinding

    private val testDrawableIds = listOf(
        R.drawable.test0,
        R.drawable.test1,
        R.drawable.test2,
        R.drawable.test3,
        R.drawable.test4,
        R.drawable.test5,
        R.drawable.test6,
        R.drawable.test7,
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val recycler = binding.recyclerView
        recycler.layoutManager = GridLayoutManager(baseContext,2)
        recycler.adapter = CustomAdapter(testDrawableIds, baseContext) { drawableId ->
            val data = Intent()
            data.putExtra("drawableId", drawableId);
            setResult(Activity.RESULT_OK, data);
            finish()
        }
    }
}