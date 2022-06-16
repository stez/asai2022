package it.stez78.asai.ml

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.label.Category

class StreetSignsPredictor(private val context: Context) {

     enum class AvailableModels {
        MOBILENETV2_80,
        MOBILENETV2_TUNED_90,
    }

    private var activeModel = AvailableModels.MOBILENETV2_80

    fun getProbability(bitmap: Bitmap, normalized: Boolean = false) : List<Category>{
        val image = TensorImage.fromBitmap(bitmap)
        val probability = mutableListOf<Category>()
        when (activeModel){
            AvailableModels.MOBILENETV2_80 -> {
                val model = ModelStandard80WithLabelsMetadata.newInstance(context)
                val outputs = model.process(image)
                model.close()
                probability.addAll(outputs.probabilityAsCategoryList)
            }
            AvailableModels.MOBILENETV2_TUNED_90 -> {
                val model = StreetSignalModel90TunedNoNormMetadata.newInstance(context)
                val outputs = model.process(image)
                model.close()
                probability.addAll(outputs.probabilityAsCategoryList)            }
        }
        if (!normalized){
            return probability
        }
        val max = probability.maxOf { p -> p.score }
        val min = probability.minOf { p -> p.score }
        return probability.map { p ->
            val normalizedScore = (p.score - min) / (max - min)
            Category(p.label,normalizedScore)
        }.toList()
    }

    fun setActiveModel(newModel: AvailableModels){
        activeModel = newModel
    }

    fun setActiveModel(index: Int){
        activeModel = getModel(index)
    }

    fun getModel(index: Int): AvailableModels{
        return AvailableModels.values()[index]
    }
}