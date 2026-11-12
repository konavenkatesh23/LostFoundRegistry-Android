package com.lostfound

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

import com.lostfound.model.LostItem
import com.lostfound.model.LostItemRequest
import com.lostfound.network.RetrofitClient
import com.lostfound.session.SessionManager

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class EditReportActivity : AppCompatActivity() {

    // =========================================================
    // VIEWS
    // =========================================================

    private lateinit var etItemName: TextInputEditText
    private lateinit var etCategory: TextInputEditText
    private lateinit var etDescription: TextInputEditText
    private lateinit var etColor: TextInputEditText
    private lateinit var etLocation: TextInputEditText
    private lateinit var etDate: TextInputEditText

    private lateinit var ivItemImage: ImageView
    private lateinit var btnChangeImage: MaterialButton
    private lateinit var btnSave: MaterialButton
    private lateinit var btnCancel: MaterialButton
    private lateinit var progressBar: ProgressBar

    // =========================================================
    // SESSION
    // =========================================================

    private lateinit var sessionManager: SessionManager

    // =========================================================
    // REPORT DATA
    // =========================================================

    private var itemId: Long = -1L

    private var existingImageUrl: String? = null

    private var selectedImageUri: Uri? = null

    companion object {
        private const val PICK_IMAGE_REQUEST = 1001
    }


    // =========================================================
    // ON CREATE
    // =========================================================

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_edit_report
        )

        sessionManager =
            SessionManager(this)

        initializeViews()

        setupToolbar()

        loadExistingData()

        setupListeners()
    }


    // =========================================================
    // INITIALIZE VIEWS
    // =========================================================

    private fun initializeViews() {

        etItemName =
            findViewById(R.id.etItemName)

        etCategory =
            findViewById(R.id.etCategory)

        etDescription =
            findViewById(R.id.etDescription)

        etColor =
            findViewById(R.id.etColor)

        etLocation =
            findViewById(R.id.etLocation)

        etDate =
            findViewById(R.id.etDate)

        ivItemImage =
            findViewById(R.id.ivItemImage)

        btnChangeImage =
            findViewById(R.id.btnChangeImage)

        btnSave =
            findViewById(R.id.btnSave)

        btnCancel =
            findViewById(R.id.btnCancel)

        progressBar =
            findViewById(R.id.progressBar)
    }


    // =========================================================
    // TOOLBAR
    // =========================================================

    private fun setupToolbar() {

        val toolbar =
            findViewById<Toolbar>(
                R.id.toolbar
            )

        setSupportActionBar(toolbar)

        supportActionBar
            ?.setDisplayHomeAsUpEnabled(true)

        supportActionBar
            ?.title = "Edit Lost Report"

        toolbar.setNavigationOnClickListener {

            finish()
        }
    }


    // =========================================================
    // LOAD EXISTING DATA
    // =========================================================

    private fun loadExistingData() {

        itemId =
            intent.getLongExtra(
                "itemId",
                -1L
            )

        if (itemId == -1L) {

            Toast.makeText(
                this,
                "Invalid report.",
                Toast.LENGTH_SHORT
            ).show()

            finish()

            return
        }


        etItemName.setText(
            intent.getStringExtra(
                "itemName"
            ) ?: ""
        )

        etCategory.setText(
            intent.getStringExtra(
                "category"
            ) ?: ""
        )

        etDescription.setText(
            intent.getStringExtra(
                "description"
            ) ?: ""
        )

        etColor.setText(
            intent.getStringExtra(
                "color"
            ) ?: ""
        )

        etLocation.setText(
            intent.getStringExtra(
                "locationLost"
            ) ?: ""
        )

        etDate.setText(
            intent.getStringExtra(
                "dateLost"
            ) ?: ""
        )

        existingImageUrl =
            intent.getStringExtra(
                "imageUrl"
            )

        loadExistingImage()
    }


    // =========================================================
    // LOAD EXISTING IMAGE
    // =========================================================

    private fun loadExistingImage() {

        if (existingImageUrl.isNullOrBlank()) {

            ivItemImage.setImageResource(
                R.drawable.ic_launcher_foreground
            )

            return
        }

        val completeUrl =
            buildImageUrl(
                existingImageUrl
            )

        Glide.with(this)
            .load(completeUrl)
            .placeholder(
                R.drawable.ic_launcher_foreground
            )
            .error(
                R.drawable.ic_launcher_foreground
            )
            .centerCrop()
            .into(ivItemImage)
    }


    // =========================================================
    // BUILD IMAGE URL
    // =========================================================

    private fun buildImageUrl(
        imageUrl: String?
    ): String {

        if (imageUrl.isNullOrBlank()) {

            return ""
        }

        return when {

            imageUrl.startsWith("http://") ||
                    imageUrl.startsWith("https://") -> {

                imageUrl
            }

            imageUrl.startsWith("/") -> {

                RetrofitClient.IMAGE_BASE_URL +
                        imageUrl
            }

            else -> {

                "${RetrofitClient.IMAGE_BASE_URL}/$imageUrl"
            }
        }
    }


    // =========================================================
    // LISTENERS
    // =========================================================

    private fun setupListeners() {

        // =====================================================
        // DATE
        // =====================================================

        etDate.setOnClickListener {

            showDatePicker()
        }


        // =====================================================
        // CHANGE IMAGE
        // =====================================================

        btnChangeImage.setOnClickListener {

            openImagePicker()
        }


        // =====================================================
        // SAVE
        // =====================================================

        btnSave.setOnClickListener {

            updateReport()
        }


        // =====================================================
        // CANCEL
        // =====================================================

        btnCancel.setOnClickListener {

            finish()
        }


        // =====================================================
        // VIEW IMAGE
        // =====================================================

        ivItemImage.setOnClickListener {

            if (!existingImageUrl.isNullOrBlank()) {

                val completeUrl =
                    buildImageUrl(
                        existingImageUrl
                    )

                val intent =
                    Intent(
                        this,
                        ImageViewerActivity::class.java
                    )

                intent.putExtra(
                    "imageUrl",
                    completeUrl
                )

                startActivity(intent)
            }
        }
    }


    // =========================================================
    // DATE PICKER
    // =========================================================

    private fun showDatePicker() {

        val calendar =
            Calendar.getInstance()


        val currentDate =
            etDate.text
                ?.toString()
                ?.trim()


        if (!currentDate.isNullOrBlank()) {

            try {

                val parts =
                    currentDate.split("-")

                if (parts.size == 3) {

                    val year =
                        parts[0].toInt()

                    val month =
                        parts[1].toInt() - 1

                    val day =
                        parts[2].toInt()

                    calendar.set(
                        year,
                        month,
                        day
                    )
                }

            } catch (_: Exception) {

                // Use today's date if parsing fails.
            }
        }


        val datePickerDialog =
            DatePickerDialog(
                this,

                { _, year, month, dayOfMonth ->

                    val formattedDate =
                        String.format(
                            Locale.getDefault(),
                            "%04d-%02d-%02d",
                            year,
                            month + 1,
                            dayOfMonth
                        )

                    etDate.setText(
                        formattedDate
                    )

                    etDate.error = null
                },

                calendar.get(
                    Calendar.YEAR
                ),

                calendar.get(
                    Calendar.MONTH
                ),

                calendar.get(
                    Calendar.DAY_OF_MONTH
                )
            )


        val today =
            Calendar.getInstance()

        today.set(
            Calendar.HOUR_OF_DAY,
            23
        )

        today.set(
            Calendar.MINUTE,
            59
        )

        today.set(
            Calendar.SECOND,
            59
        )

        today.set(
            Calendar.MILLISECOND,
            999
        )


        datePickerDialog.datePicker.maxDate =
            today.timeInMillis


        datePickerDialog.show()
    }


    // =========================================================
    // VALIDATE DATE
    // =========================================================

    private fun isValidLostDate(
        dateString: String
    ): Boolean {

        return try {

            val format =
                SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.getDefault()
                )

            format.isLenient = false


            val selectedDate =
                format.parse(
                    dateString
                ) ?: return false


            val today =
                Calendar.getInstance()

            today.set(
                Calendar.HOUR_OF_DAY,
                0
            )

            today.set(
                Calendar.MINUTE,
                0
            )

            today.set(
                Calendar.SECOND,
                0
            )

            today.set(
                Calendar.MILLISECOND,
                0
            )


            val selectedCalendar =
                Calendar.getInstance()

            selectedCalendar.time =
                selectedDate

            selectedCalendar.set(
                Calendar.HOUR_OF_DAY,
                0
            )

            selectedCalendar.set(
                Calendar.MINUTE,
                0
            )

            selectedCalendar.set(
                Calendar.SECOND,
                0
            )

            selectedCalendar.set(
                Calendar.MILLISECOND,
                0
            )


            !selectedCalendar.after(
                today
            )

        } catch (_: Exception) {

            false
        }
    }


    // =========================================================
    // IMAGE PICKER
    // =========================================================

    private fun openImagePicker() {

        val intent =
            Intent(
                Intent.ACTION_OPEN_DOCUMENT
            )

        intent.addCategory(
            Intent.CATEGORY_OPENABLE
        )

        intent.type =
            "image/*"

        startActivityForResult(
            intent,
            PICK_IMAGE_REQUEST
        )
    }


    // =========================================================
    // IMAGE PICKER RESULT
    // =========================================================

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {

        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )

        if (
            requestCode ==
            PICK_IMAGE_REQUEST &&
            resultCode ==
            RESULT_OK &&
            data?.data != null
        ) {

            selectedImageUri =
                data.data

            ivItemImage.setImageURI(
                selectedImageUri
            )
        }
    }


    // =========================================================
    // UPDATE REPORT
    // =========================================================

    private fun updateReport() {

        val itemName =
            etItemName.text
                ?.toString()
                ?.trim()
                ?: ""

        val category =
            etCategory.text
                ?.toString()
                ?.trim()
                ?: ""

        val description =
            etDescription.text
                ?.toString()
                ?.trim()
                ?: ""

        val color =
            etColor.text
                ?.toString()
                ?.trim()
                ?: ""

        val location =
            etLocation.text
                ?.toString()
                ?.trim()
                ?: ""

        val date =
            etDate.text
                ?.toString()
                ?.trim()
                ?: ""


        if (itemName.isBlank()) {

            etItemName.error =
                "Enter item name"

            etItemName.requestFocus()

            return
        }


        if (category.isBlank()) {

            etCategory.error =
                "Enter category"

            etCategory.requestFocus()

            return
        }


        if (location.isBlank()) {

            etLocation.error =
                "Enter lost location"

            etLocation.requestFocus()

            return
        }


        if (date.isBlank()) {

            etDate.error =
                "Select date"

            etDate.requestFocus()

            return
        }


        if (!isValidLostDate(date)) {

            etDate.error =
                "Future dates are not allowed"

            Toast.makeText(
                this,
                "Please select today or a previous date.",
                Toast.LENGTH_SHORT
            ).show()

            etDate.requestFocus()

            return
        }


        if (selectedImageUri != null) {

            updateWithNewImage(
                itemName,
                category,
                description,
                color,
                location,
                date,
                selectedImageUri!!
            )

        } else {

            updateWithoutNewImage(
                itemName,
                category,
                description,
                color,
                location,
                date
            )
        }
    }


    // =========================================================
    // UPDATE WITHOUT NEW IMAGE
    // =========================================================

    private fun updateWithoutNewImage(

        itemName: String,
        category: String,
        description: String,
        color: String,
        location: String,
        date: String

    ) {

        val request =
            LostItemRequest(

                itemName = itemName,

                category = category,

                description = description,

                color = color,

                locationLost = location,

                dateLost = date,

                imageUrl =
                    existingImageUrl ?: ""
            )


        progressBar.visibility =
            View.VISIBLE

        btnSave.isEnabled =
            false

        btnChangeImage.isEnabled =
            false


        val token =
            "Bearer ${sessionManager.getToken()}"


        RetrofitClient.lostItemApi
            .updateLostItem(
                token,
                itemId,
                request
            )
            .enqueue(
                object : Callback<LostItem> {

                    override fun onResponse(
                        call: Call<LostItem>,
                        response: Response<LostItem>
                    ) {

                        progressBar.visibility =
                            View.GONE

                        btnSave.isEnabled =
                            true

                        btnChangeImage.isEnabled =
                            true


                        if (
                            response.isSuccessful
                        ) {

                            Toast.makeText(
                                this@EditReportActivity,
                                "Report updated successfully.",
                                Toast.LENGTH_SHORT
                            ).show()

                            finish()

                        } else {

                            Toast.makeText(
                                this@EditReportActivity,
                                "Failed to update report. Code: ${response.code()}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }


                    override fun onFailure(
                        call: Call<LostItem>,
                        t: Throwable
                    ) {

                        progressBar.visibility =
                            View.GONE

                        btnSave.isEnabled =
                            true

                        btnChangeImage.isEnabled =
                            true

                        Toast.makeText(
                            this@EditReportActivity,
                            t.localizedMessage
                                ?: "Network error",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
    }


    // =========================================================
    // UPDATE WITH NEW IMAGE
    // =========================================================

    private fun updateWithNewImage(

        itemName: String,
        category: String,
        description: String,
        color: String,
        location: String,
        date: String,
        imageUri: Uri

    ) {

        progressBar.visibility =
            View.VISIBLE

        btnSave.isEnabled =
            false

        btnChangeImage.isEnabled =
            false


        try {

            // =================================================
            // URI → TEMP FILE
            // =================================================

            val imageFile =
                createTempFileFromUri(
                    imageUri
                )


            // =================================================
            // IMAGE REQUEST BODY
            // =================================================

            val requestBody =
                imageFile.asRequestBody(
                    "image/*".toMediaTypeOrNull()
                )


            val imagePart =
                MultipartBody.Part.createFormData(
                    "image",
                    imageFile.name,
                    requestBody
                )


            // =================================================
            // TEXT REQUEST BODIES
            // =================================================

            val itemNameBody =
                itemName.toRequestBody(
                    "text/plain".toMediaTypeOrNull()
                )

            val categoryBody =
                category.toRequestBody(
                    "text/plain".toMediaTypeOrNull()
                )

            val descriptionBody =
                description.toRequestBody(
                    "text/plain".toMediaTypeOrNull()
                )

            val colorBody =
                color.toRequestBody(
                    "text/plain".toMediaTypeOrNull()
                )

            val locationBody =
                location.toRequestBody(
                    "text/plain".toMediaTypeOrNull()
                )

            val dateBody =
                date.toRequestBody(
                    "text/plain".toMediaTypeOrNull()
                )


            // =================================================
            // TOKEN
            // =================================================

            val token =
                "Bearer ${sessionManager.getToken()}"


            // =================================================
            // UPDATE REPORT + IMAGE
            // =================================================

            RetrofitClient.lostItemApi
                .updateLostItemWithImage(

                    authorization = token,

                    id = itemId,

                    itemName = itemNameBody,

                    category = categoryBody,

                    description = descriptionBody,

                    color = colorBody,

                    locationLost = locationBody,

                    dateLost = dateBody,

                    image = imagePart

                )
                .enqueue(
                    object : Callback<LostItem> {

                        override fun onResponse(
                            call: Call<LostItem>,
                            response: Response<LostItem>
                        ) {

                            progressBar.visibility =
                                View.GONE

                            btnSave.isEnabled =
                                true

                            btnChangeImage.isEnabled =
                                true


                            if (
                                response.isSuccessful
                            ) {

                                Toast.makeText(
                                    this@EditReportActivity,
                                    "Report and image updated successfully.",
                                    Toast.LENGTH_SHORT
                                ).show()

                                finish()

                            } else {

                                Toast.makeText(
                                    this@EditReportActivity,
                                    "Image update failed. Code: ${response.code()}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }


                        override fun onFailure(
                            call: Call<LostItem>,
                            t: Throwable
                        ) {

                            progressBar.visibility =
                                View.GONE

                            btnSave.isEnabled =
                                true

                            btnChangeImage.isEnabled =
                                true


                            Toast.makeText(
                                this@EditReportActivity,
                                t.localizedMessage
                                    ?: "Network error",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                )

        } catch (e: Exception) {

            progressBar.visibility =
                View.GONE

            btnSave.isEnabled =
                true

            btnChangeImage.isEnabled =
                true


            Toast.makeText(
                this,
                "Could not prepare image: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }


    // =========================================================
    // URI → TEMPORARY FILE
    // =========================================================

    private fun createTempFileFromUri(
        uri: Uri
    ): File {

        val fileName =
            getFileName(uri)
                ?: "edit_image.jpg"


        val extension =
            if (
                fileName.contains(".")
            ) {

                "." +
                        fileName
                            .substringAfterLast(".")

            } else {

                ".jpg"
            }


        val tempFile =
            File.createTempFile(
                "edit_image_",
                extension,
                cacheDir
            )


        contentResolver
            .openInputStream(uri)
            .use { input ->

                if (input == null) {

                    throw Exception(
                        "Unable to open selected image."
                    )
                }


                FileOutputStream(
                    tempFile
                ).use { output ->

                    input.copyTo(
                        output
                    )
                }
            }


        return tempFile
    }


    // =========================================================
    // GET FILE NAME
    // =========================================================

    private fun getFileName(
        uri: Uri
    ): String? {

        var result: String? = null


        if (
            uri.scheme ==
            "content"
        ) {

            contentResolver
                .query(
                    uri,
                    null,
                    null,
                    null,
                    null
                )
                ?.use { cursor ->

                    val nameIndex =
                        cursor.getColumnIndex(
                            OpenableColumns.DISPLAY_NAME
                        )


                    if (
                        cursor.moveToFirst() &&
                        nameIndex >= 0
                    ) {

                        result =
                            cursor.getString(
                                nameIndex
                            )
                    }
                }
        }


        if (result == null) {

            result =
                uri.path
                    ?.substringAfterLast("/")
        }


        return result
    }
}