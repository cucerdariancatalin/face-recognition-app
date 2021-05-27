package brain_factory.face_recognition

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.nio.FloatBuffer

class ImageProcess
{
    companion object
    {
        private fun extractFace(imageBitmap: Bitmap): Rect
        {
            var faceBounds = Rect(0, 0, 1944, 2592)
            val options = FaceDetectorOptions.Builder().setClassificationMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL).build()
            val detector = FaceDetection.getClient(options)

            val image = InputImage.fromBitmap(imageBitmap, 0)
            detector.process(image).addOnSuccessListener { faces ->
                for (face in faces)
                {
                    faceBounds = face.boundingBox
                }
            }
            detector.close()
            Log.d("[embeddings]", faceBounds.toShortString()) // DEBUG
            return faceBounds
        }

        fun getEmbeddings(currentPhotoPath: String, assetManager: AssetManager): FloatBuffer
        {
            val faceNet = FaceNet(assetManager)
            val bitmapImage = ImageUtils.handleSamplingAndRotationBitmap(currentPhotoPath)
            val faceBounds = extractFace(bitmapImage)
            val embeddings = faceNet.getEmbeddings(bitmapImage, faceBounds)
            faceNet.close()
            return embeddings
        }
    }
}