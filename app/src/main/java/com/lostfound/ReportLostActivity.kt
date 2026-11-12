package com.lostfound

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.lostfound.model.LostItem
import com.lostfound.model.LostItemRequest
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

class ReportLostActivity : AppCompatActivity() {

    // ============================================================
    // Session
    // ============================================================

    private lateinit var sessionManager: SessionManager


    // ============================================================
    // Input Fields
    // ============================================================

    private lateinit var etItemName: TextInputEditText
    private lateinit var etCategory: TextInputEditText
    private lateinit var etDescription: TextInputEditText
    private lateinit var etColor: TextInputEditText
    private lateinit var etLocation: TextInputEditText
    private lateinit var etDate: TextInputEditText


    // ============================================================
    // Image Views / Buttons
    // ============================================================

    private lateinit var ivPreview: ImageView
    private lateinit var btnUploadImage: MaterialButton
    private lateinit var btnSubmit: MaterialButton


    // ============================================================
    // Selected Image
    // ============================================================

    private var selectedImageUri: Uri? = null

    private var uploadedImageUrl: String? = null


    // ============================================================
    // Gallery Launcher
    // ============================================================

    private val galleryLauncher =
        registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->

            if (uri != null) {

                selectedImageUri = uri

                // Show selected image immediately
                ivPreview.setImageURI(uri)

                Toast.makeText(
                    this,
                    "Image selected successfully",
                    Toast.LENGTH_SHORT
                ).show()

                // Upload image to server
                uploadImage(uri)
            }
        }


    // ============================================================
    // onCreate
    // ============================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_report_lost)


        // ========================================================
        // Session
        // ========================================================

        sessionManager =
            SessionManager(this)


        // ========================================================
        // Toolbar
        // ========================================================

        val toolbar =
            findViewById<Toolbar>(R.id.toolbar)

        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportActionBar?.title =
            "Report Lost Item"

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

        etLocation =
            findViewById(R.id.etLocation)

        etDate =
            findViewById(R.id.etDate)

        ivPreview =
            findViewById(R.id.ivPreview)

        btnUploadImage =
            findViewById(R.id.btnUploadImage)

        btnSubmit =
            findViewById(R.id.btnSubmit)


        // ========================================================
        // Date Picker
        // ========================================================

        etDate.setOnClickListener {

            showLostDatePicker(etDate)
        }


        // ========================================================
        // Upload Image
        // ========================================================

        btnUploadImage.setOnClickListener {

            openGallery()
        }


        // ========================================================
        // Submit Report
        // ========================================================

        btnSubmit.setOnClickListener {

            submitLostItem()
        }
    }


    // ============================================================
    // Date Picker
    // ============================================================

    private fun showLostDatePicker(
        dateEditText: EditText
    ) {

        // --------------------------------------------------------
        // Current date
        // --------------------------------------------------------

        val calendar =
            Calendar.getInstance()

        val year =
            calendar.get(Calendar.YEAR)

        val month =
            calendar.get(Calendar.MONTH)

        val day =
            calendar.get(Calendar.DAY_OF_MONTH)


        // --------------------------------------------------------
        // Create DatePicker
        // --------------------------------------------------------

        val datePickerDialog =
            DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->

                    // --------------------------------------------
                    // Create selected date
                    // --------------------------------------------

                    val selectedDate =
                        Calendar.getInstance()

                    selectedDate.set(
                        selectedYear,
                        selectedMonth,
                        selectedDay
                    )


                    // --------------------------------------------
                    // Format:
                    // yyyy-MM-dd
                    // --------------------------------------------

                    val dateFormat =
                        SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.getDefault()
                        )


                    dateEditText.setText(
                        dateFormat.format(
                            selectedDate.time
                        )
                    )

                },
                year,
                month,
                day
            )


        // --------------------------------------------------------
        // IMPORTANT
        //
        // Future dates are disabled.
        //
        // Today = allowed
        // Past dates = allowed
        // Future dates = NOT allowed
        // --------------------------------------------------------

        datePickerDialog
            .datePicker
            .maxDate =
            System.currentTimeMillis()


        // --------------------------------------------------------
        // Show Calendar
        // --------------------------------------------------------

        datePickerDialog.show()
    }


    // ============================================================
    // Open Gallery
    // ============================================================

    private fun openGallery() {

        galleryLauncher.launch(
            "image/*"
        )
    }


    // ============================================================
    // Upload Image
    // ============================================================

    private fun uploadImage(
        uri: Uri
    ) {

        // --------------------------------------------------------
        // Convert URI to File
        // --------------------------------------------------------

        val imageFile =
            uriToFile(uri)


        if (imageFile == null) {

            Toast.makeText(
                this,
                "Unable to read selected image.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        // --------------------------------------------------------
        // Request Body
        // --------------------------------------------------------

        val requestBody =
            imageFile.asRequestBody(
                "image/*".toMediaTypeOrNull()
            )


        // --------------------------------------------------------
        // Multipart
        // --------------------------------------------------------

        val multipartBody =
            MultipartBody.Part.createFormData(
                "image",
                imageFile.name,
                requestBody
            )


        // --------------------------------------------------------
        // Authorization
        // --------------------------------------------------------

        val token =
            "Bearer ${sessionManager.getToken()}"


        Toast.makeText(
            this,
            "Uploading image...",
            Toast.LENGTH_SHORT
        ).show()


        // --------------------------------------------------------
        // API Call
        // --------------------------------------------------------

        RetrofitClient.uploadApi
            .uploadImage(
                token,
                multipartBody
            )
            .enqueue(
                object : Callback<UploadResponse> {

                    override fun onResponse(
                        call: Call<UploadResponse>,
                        response: Response<UploadResponse>
                    ) {

                        if (
                            response.isSuccessful &&
                            response.body() != null
                        ) {

                            // ------------------------------------
                            // Save uploaded image URL
                            // ------------------------------------

                            uploadedImageUrl =
                                response.body()!!.imageUrl


                            Toast.makeText(
                                this@ReportLostActivity,
                                "Image uploaded successfully.",
                                Toast.LENGTH_SHORT
                            ).show()

                        } else {

                            uploadedImageUrl = null

                            Toast.makeText(
                                this@ReportLostActivity,
                                "Image upload failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }


                    override fun onFailure(
                        call: Call<UploadResponse>,
                        t: Throwable
                    ) {

                        uploadedImageUrl = null

                        Toast.makeText(
                            this@ReportLostActivity,
                            "Upload Error: ${
                                t.localizedMessage
                            }",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
    }


    // ============================================================
    // Convert URI to File
    // ============================================================

    private fun uriToFile(
        uri: Uri
    ): File? {

        return try {

            val inputStream =
                contentResolver.openInputStream(uri)

            if (inputStream == null) {
                return null
            }


            // ----------------------------------------------------
            // Temporary file
            // ----------------------------------------------------

            val fileName =
                "upload_${System.currentTimeMillis()}.jpg"

            val tempFile =
                File(
                    cacheDir,
                    fileName
                )


            // ----------------------------------------------------
            // Copy URI content to file
            // ----------------------------------------------------

            val outputStream =
                FileOutputStream(tempFile)


            inputStream.use { input ->

                outputStream.use { output ->

                    input.copyTo(output)
                }
            }


            tempFile

        } catch (e: Exception) {

            e.printStackTrace()

            null
        }
    }


    // ============================================================
    // Submit Lost Item
    // ============================================================

    private fun submitLostItem() {

        // --------------------------------------------------------
        // Read fields
        // --------------------------------------------------------

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

        val location =
            etLocation.text
                .toString()
                .trim()

        val date =
            etDate.text
                .toString()
                .trim()


        // ========================================================
        // Validate fields
        // ========================================================

        if (
            itemName.isEmpty() ||
            category.isEmpty() ||
            description.isEmpty() ||
            color.isEmpty() ||
            location.isEmpty() ||
            date.isEmpty()
        ) {

            Toast.makeText(
                this,
                "Please fill all required fields",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        // ========================================================
        // Validate Date
        // ========================================================

        if (!isValidLostDate(date)) {

            Toast.makeText(
                this,
                "Please select today or a previous date.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        // ========================================================
        // Check Image Upload
        // ========================================================

        if (
            selectedImageUri != null &&
            uploadedImageUrl == null
        ) {

            Toast.makeText(
                this,
                "Please wait until the image upload is complete.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        // ========================================================
        // Create Request
        // ========================================================

        val request =
            LostItemRequest(

                itemName = itemName,

                category = category,

                description = description,

                color = color,

                locationLost = location,

                dateLost = date,

                imageUrl = uploadedImageUrl
            )


        // ========================================================
        // Authorization
        // ========================================================

        val token =
            "Bearer ${sessionManager.getToken()}"


        // ========================================================
        // Submit to Backend
        // ========================================================

        RetrofitClient.lostItemApi
            .reportLostItem(
                token,
                request
            )
            .enqueue(
                object : Callback<LostItem> {

                    override fun onResponse(
                        call: Call<LostItem>,
                        response: Response<LostItem>
                    ) {

                        if (response.isSuccessful) {

                            Toast.makeText(
                                this@ReportLostActivity,
                                "Lost item reported successfully!",
                                Toast.LENGTH_LONG
                            ).show()


                            // ------------------------------------
                            // Clear temporary image information
                            // ------------------------------------

                            uploadedImageUrl = null

                            selectedImageUri = null


                            // ------------------------------------
                            // Return to previous screen
                            // ------------------------------------

                            finish()

                        } else {

                            Toast.makeText(
                                this@ReportLostActivity,
                                "Failed to report lost item.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }


                    override fun onFailure(
                        call: Call<LostItem>,
                        t: Throwable
                    ) {

                        Toast.makeText(
                            this@ReportLostActivity,
                            "Error: ${
                                t.localizedMessage
                            }",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
    }


    // ============================================================
    // Validate Lost Date
    // ============================================================
    //
    // This is an additional safety check.
    //
    // Even though DatePickerDialog already prevents future dates,
    // this makes sure a future date cannot reach the backend if
    // something somehow changes the EditText value.
    //
    // ============================================================

    private fun isValidLostDate(
        dateString: String
    ): Boolean {

        return try {

            val dateFormat =
                SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.getDefault()
                )

            dateFormat.isLenient = false


            val selectedDate =
                dateFormat.parse(dateString)
                    ?: return false


            // ----------------------------------------------------
            // Today's date at midnight
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
            // Selected date
            // ----------------------------------------------------

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


            // ----------------------------------------------------
            // Selected date must be today or earlier
            // ----------------------------------------------------
            !selectedCalendar.after(today)

        } catch (e: Exception) {

            false
        }
    }
}