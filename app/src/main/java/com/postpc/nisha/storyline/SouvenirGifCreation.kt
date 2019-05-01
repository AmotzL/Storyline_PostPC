package com.postpc.nisha.storyline

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.postpc.nisha.storyline.classifier.*
import com.postpc.nisha.storyline.classifier.tensorflow.ImageClassifierFactory
import java.io.File


private const val REQUEST_PERMISSIONS = 1

/**
 * Creates the souvenir gif by invoking an instance of SouvenirGifCreationThread.
 * this activity is not visually shown (it only performs the creation).
 */
class SouvenirGifCreation : Activity() {

    private lateinit var classifier: Classifier
    private var curStoryName: String = ""
    private var storyStartDate: String = ""
    private var storyStartTime: String = ""
    private var storyEndTime: String = ""
    private var storyEndDate: String = ""
    private var appDirectory: String = ""
    private var gifFolderPath: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.souvenir_gif_creation_layout)
        curStoryName = getIntent().getStringExtra(getString(R.string.intentKey_cur_story_name))
        storyStartDate = getIntent().getStringExtra(getString(R.string.intentKey_cur_story_start_date))
        storyEndDate = getIntent().getStringExtra(getString(R.string.intentKey_cur_story_end_date))
        storyEndTime = getIntent().getStringExtra(getString(R.string.end_time_key))
        storyStartTime = getIntent().getStringExtra(getString(R.string.start_time_key))

        appDirectory = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).absolutePath + "/Storyline/"
        val appPath = File(appDirectory)
        appPath.mkdirs()

        gifFolderPath = appDirectory + curStoryName + "/"
        val folderPath = File(gifFolderPath)
        folderPath.mkdirs()

        checkPermissions()
    }

    /**
     * check permissions for gif creation
     */
    private fun checkPermissions() {
        if (arePermissionsAlreadyGranted()) {
            createSouvenirGif()
        } else {
            requestPermissions()
        }
    }

    /**
     * check if permissions are already granted
     */
    private fun arePermissionsAlreadyGranted() =
            ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

    /**
     * request permissions if needed
     */
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_PERMISSIONS)
    }

    /**
     * if permissions are granted - create the gif, otherwise - request permissions
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSIONS && arePermissionGranted(grantResults)) {
            createSouvenirGif()
        } else {
            requestPermissions()
        }
    }

    /**
     * check if permissions are granted
     */
    private fun arePermissionGranted(grantResults: IntArray) =
            grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED

    /**
     * create the gif souvenir by creating a souvenirGifCreationThread for this task
     */
    private fun createSouvenirGif() {
        createClassifier()
        val souvenirGifCreationThread = SouvenirGifCreationThread(classifier, applicationContext,
                curStoryName, storyStartDate, storyEndDate, storyStartTime, storyEndTime)
        souvenirGifCreationThread.start()
    }

    /**
     * create the image classifier
     */
    private fun createClassifier() {
        classifier = ImageClassifierFactory.create(
                assets,
                GRAPH_FILE_PATH,
                LABELS_FILE_PATH,
                IMAGE_SIZE,
                GRAPH_INPUT_NAME,
                GRAPH_OUTPUT_NAME
        )
    }

}





