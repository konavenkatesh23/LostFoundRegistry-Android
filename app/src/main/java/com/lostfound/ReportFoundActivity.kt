package com.lostfound

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.lostfound.model.FoundItem
import com.lostfound.model.FoundItemRequest
import com.lostfound.model.UploadResponse
import com.lostfound.network.RetrofitClient
import com.lostfound.session.SessionManager
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ReportFoundActivity : AppCompatActivity() {

    // ============================================================
    // Input Fields
    // ============================================================

    private lateinit var etItemName: EditText
    private lateinit var etCategory: EditText
    private lateinit var etDescription: EditText
    private lateinit var etColor: EditText
    private lateinit var etLocationFound: EditText
    private lateinit var etDateFound: EditText


    // ============================================================
    // Views
    // ============================================================

    private lateinit var ivSelectedImage: ImageView
    private lateinit var btnSelectImage: Button
    private lateinit var btnSubmit: Button
    private lateinit var progressBar: ProgressBar


    // ============================================================
    // Session
    // ============================================================

    private lateinit var sessionManager: SessionManager


    // ============================================================
    // Selected Image
    // ============================================================

    private var selectedImageUri: Uri? = null


    // ============================================================
    // Image Picker
    // ============================================================

    private val imagePicker =
        registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->

            if (uri != null) {

                selectedImageUri = uri

                Glide.with(this)
                    .load(uri)
                    .into(ivSelectedImage)

                Toast.makeText(
                    this,
                    "Image selected successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


    // ============================================================
    // onCreate
    // ============================================================

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_report_found
        )


        // ========================================================
        // Toolbar
        // ========================================================

        val toolbar =
            findViewById<Toolbar>(
                R.id.toolbar
            )

        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(
            true
        )

        supportActionBar?.title =
            "Report Found Item"

        toolbar.setNavigationOnClickListener {
            finish()
        }


        // ========================================================
        // Find Views
        // ========================================================

        etItemName =
            findViewById(R.id.etItemName)

        etCategory =
            findViewById(R.id.etCategory)

        etDescription =
            findViewById(R.id.etDescription)

        etColor =
            findViewById(R.id.etColor)

        etLocationFound =
            findViewById(R.id.etLocationFound)

        etDateFound =
            findViewById(R.id.etDateFound)

        ivSelectedImage =
            findViewById(R.id.ivSelectedImage)

        btnSelectImage =
            findViewById(R.id.btnSelectImage)

        btnSubmit =
            findViewById(R.id.btnSubmit)

        progressBar =
            findViewById(R.id.progressBar)

        sessionManager =
            SessionManager(this)


        // ========================================================
        // Date Picker
        // ========================================================

        etDateFound.setOnClickListener {

            showDatePicker()
        }


        // ========================================================
        // Select Image
        // ========================================================

        btnSelectImage.setOnClickListener {

            imagePicker.launch(
                "image/*"
            )
        }


        // ========================================================
        // Submit
        // ========================================================

        btnSubmit.setOnClickListener {

            submitFoundItem()
        }
    }


    // ============================================================
    // Date Picker
    // ============================================================

    private fun showDatePicker() {

        val calendar =
            Calendar.getInstance()


        // ========================================================
        // Current Date
        // ========================================================

        val year =
            calendar.get(Calendar.YEAR)

        val month =
            calendar.get(Calendar.MONTH)

        val day =
            calendar.get(Calendar.DAY_OF_MONTH)


        // ========================================================
        // Create DatePickerDialog
        // ========================================================

        val datePicker =
            DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->

                    // ------------------------------------------------
                    // Selected Date
                    // ------------------------------------------------

                    val selectedCalendar =
                        Calendar.getInstance()

                    selectedCalendar.set(
                        selectedYear,
                        selectedMonth,
                        selectedDay
                    )


                    // ------------------------------------------------
                    // Format Date
                    //
                    // yyyy-MM-dd
                    //
                    // Example:
                    // 2026-08-11
                    // ------------------------------------------------

                    val format =
                        SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.getDefault()
                        )


                    etDateFound.setText(
                        format.format(
                            selectedCalendar.time
                        )
                    )

                },
                year,
                month,
                day
            )


        // ========================================================
        // IMPORTANT
        //
        // Maximum selectable date = TODAY
        //
        // Therefore:
        //
        // Past dates  -> ALLOWED
        // Today        -> ALLOWED
        // Future dates -> NOT ALLOWED
        // ========================================================

        datePicker
            .datePicker
            .maxDate =
            System.currentTimeMillis()


        // ========================================================
        // Show Calendar
        // ========================================================

        datePicker.show()
    }


    // ============================================================
    // Validate Found Date
    // ============================================================

    private fun isValidFoundDate(
        dateString: String
    ): Boolean {

        return try {

            // ----------------------------------------------------
            // Parse date
            // ----------------------------------------------------

            val format =
                SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.getDefault()
                )

            format.isLenient = false

            val parsedDate =
                format.parse(
                    dateString
                ) ?: return false


            // ----------------------------------------------------
            // Today at midnight
            // ----------------------------------------------------

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


            // ----------------------------------------------------
            // Selected date at midnight
            // ----------------------------------------------------

            val selectedDate =
                Calendar.getInstance()

            selectedDate.time =
                parsedDate

            selectedDate.set(
                Calendar.HOUR_OF_DAY,
                0
            )

            selectedDate.set(
                Calendar.MINUTE,
                0
            )

            selectedDate.set(
                Calendar.SECOND,
                0
            )

            selectedDate.set(
                Calendar.MILLISECOND,
                0
            )


            // ----------------------------------------------------
            // Today or past = TRUE
            // Future = FALSE
            // ----------------------------------------------------

            !selectedDate.after(
                today
            )

        } catch (e: Exception) {

            false
        }
    }


    // ============================================================
    // Submit Found Item
    // ============================================================

    private fun submitFoundItem() {

        // ========================================================
        // Read Fields
        // ========================================================

        val itemName =
            etItemName.text
                .toString()
                .trim()

        val category =
            etCategory.text
                .toString()
                .trim()

        val description =
            etDescription.text
                .toString()
                .trim()

        val color =
            etColor.text
                .toString()
                .trim()

        val locationFound =
            etLocationFound.text
                .toString()
                .trim()

        val dateFound =
            etDateFound.text
                .toString()
                .trim()


        // ========================================================
        // Item Name Validation
        // ========================================================

        if (itemName.isEmpty()) {

            etItemName.error =
                "Enter item name"

            etItemName.requestFocus()

            return
        }


        // ========================================================
        // Category Validation
        // ========================================================

        if (category.isEmpty()) {

            etCategory.error =
                "Enter category"

            etCategory.requestFocus()

            return
        }


        // ========================================================
        // Location Validation
        // ========================================================

        if (locationFound.isEmpty()) {

            etLocationFound.error =
                "Enter location where item was found"

            etLocationFound.requestFocus()

            return
        }


        // ========================================================
        // Date Validation
        // ========================================================

        if (dateFound.isEmpty()) {

            etDateFound.error =
                "Select found date"

            etDateFound.requestFocus()

            return
        }


        // ========================================================
        // Future Date Protection
        // ========================================================

        if (!isValidFoundDate(dateFound)) {

            etDateFound.error =
                "Future dates are not allowed"

            Toast.makeText(
                this,
                "Please select today or a previous date.",
                Toast.LENGTH_SHORT
            ).show()

            etDateFound.requestFocus()

            return
        }


        // ========================================================
        // Image Validation
        // ========================================================

        if (selectedImageUri == null) {

            Toast.makeText(
                this,
                "Please select an image",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        // ========================================================
        // Upload Image First
        // ========================================================

        uploadImage()
    }


    // ============================================================
    // Upload Image
    // ============================================================

    private fun uploadImage() {

        progressBar.visibility =
            View.VISIBLE

        btnSubmit.isEnabled =
            false

        btnSelectImage.isEnabled =
            false


        // ========================================================
        // Selected URI
        // ========================================================

        val uri =
            selectedImageUri


        if (uri == null) {

            resetButtons()

            Toast.makeText(
                this,
                "Please select an image",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        try {

            // ====================================================
            // Convert URI to File
            // ====================================================

            val file =
                uriToFile(uri)


            // ====================================================
            // Request Body
            // ====================================================

            val requestFile =
                file.asRequestBody(
                    "image/*".toMediaTypeOrNull()
                )


            // ====================================================
            // Multipart
            // ====================================================

            val imagePart =
                MultipartBody.Part.createFormData(
                    "image",
                    file.name,
                    requestFile
                )


            // ====================================================
            // Authorization
            // ====================================================

            val token =
                "Bearer ${sessionManager.getToken()}"


            // ====================================================
            // Upload
            // ====================================================

            RetrofitClient.uploadApi
                .uploadImage(
                    token,
                    imagePart
                )
                .enqueue(
                    object :
                        Callback<UploadResponse> {

                        override fun onResponse(
                            call: Call<UploadResponse>,
                            response: Response<UploadResponse>
                        ) {

                            if (
                                response.isSuccessful &&
                                response.body() != null
                            ) {

                                val imageUrl =
                                    response.body()!!
                                        .imageUrl


                                // --------------------------------
                                // Create Found Item
                                // --------------------------------

                                createFoundItem(
                                    imageUrl
                                )

                            } else {

                                resetButtons()

                                Toast.makeText(
                                    this@ReportFoundActivity,
                                    "Image upload failed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }


                        override fun onFailure(
                            call: Call<UploadResponse>,
                            t: Throwable
                        ) {

                            resetButtons()

                            Toast.makeText(
                                this@ReportFoundActivity,
                                t.localizedMessage
                                    ?: "Image upload failed",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                )

        } catch (e: Exception) {

            resetButtons()

            Toast.makeText(
                this,
                e.localizedMessage
                    ?: "Unable to process image",
                Toast.LENGTH_LONG
            ).show()
        }
    }


    // ============================================================
    // Create Found Item
    // ============================================================

    private fun createFoundItem(
        imageUrl: String
    ) {

        // ========================================================
        // Create Request
        // ========================================================

        val request =
            FoundItemRequest(

                itemName =
                    etItemName.text
                        .toString()
                        .trim(),

                category =
                    etCategory.text
                        .toString()
                        .trim(),

                description =
                    etDescription.text
                        .toString()
                        .trim()
                        .ifEmpty {
                            null
                        },

                color =
                    etColor.text
                        .toString()
                        .trim()
                        .ifEmpty {
                            null
                        },

                locationFound =
                    etLocationFound.text
                        .toString()
                        .trim(),

                dateFound =
                    etDateFound.text
                        .toString()
                        .trim(),

                imageUrl =
                    imageUrl
            )


        // ========================================================
        // Authorization
        // ========================================================

        val token =
            "Bearer ${sessionManager.getToken()}"


        // ========================================================
        // Backend API
        // ========================================================

        RetrofitClient.foundItemApi
            .reportFoundItem(
                token,
                request
            )
            .enqueue(
                object :
                    Callback<FoundItem> {

                    override fun onResponse(
                        call: Call<FoundItem>,
                        response: Response<FoundItem>
                    ) {

                        resetButtons()


                        if (response.isSuccessful) {

                            Toast.makeText(
                                this@ReportFoundActivity,
                                "Found item reported successfully",
                                Toast.LENGTH_LONG
                            ).show()

                            finish()

                        } else {

                            Toast.makeText(
                                this@ReportFoundActivity,
                                "Failed to report found item",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }


                    override fun onFailure(
                        call: Call<FoundItem>,
                        t: Throwable
                    ) {

                        resetButtons()

                        Toast.makeText(
                            this@ReportFoundActivity,
                            t.localizedMessage
                                ?: "Network error",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
    }


    // ============================================================
    // URI → File
    // ============================================================

    private fun uriToFile(
        uri: Uri
    ): File {

        val inputStream =
            contentResolver.openInputStream(
                uri
            )
                ?: throw Exception(
                    "Unable to open selected image"
                )


        // ========================================================
        // Temporary File
        // ========================================================

        val file =
            File(
                cacheDir,
                "found_${System.currentTimeMillis()}.jpg"
            )


        // ========================================================
        // Copy Image
        // ========================================================

        FileOutputStream(file).use { output ->

            inputStream.use { input ->

                input.copyTo(
                    output
                )
            }
        }


        return file
    }


    // ============================================================
    // Reset Buttons
    // ============================================================

    private fun resetButtons() {

        progressBar.visibility =
            View.GONE

        btnSubmit.isEnabled =
            true

        btnSelectImage.isEnabled =
            true
    }
}