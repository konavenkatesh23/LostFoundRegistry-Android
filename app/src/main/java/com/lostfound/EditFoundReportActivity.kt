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

class EditFoundReportActivity : AppCompatActivity() {

    // ============================================================
    // Views
    // ============================================================

    private lateinit var etItemName: EditText
    private lateinit var etCategory: EditText
    private lateinit var etDescription: EditText
    private lateinit var etColor: EditText
    private lateinit var etLocationFound: EditText
    private lateinit var etDateFound: EditText

    private lateinit var ivSelectedImage: ImageView
    private lateinit var btnSelectImage: Button
    private lateinit var btnUpdate: Button
    private lateinit var progressBar: ProgressBar

    // ============================================================
    // Session
    // ============================================================

    private lateinit var sessionManager: SessionManager

    // ============================================================
    // Data
    // ============================================================

    private var itemId: Long = -1L

    private var existingImageUrl: String? = null

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
                    .placeholder(
                        R.drawable.ic_launcher_foreground
                    )
                    .error(
                        R.drawable.ic_launcher_foreground
                    )
                    .centerCrop()
                    .into(ivSelectedImage)
            }
        }

    // ============================================================
    // onCreate
    // ============================================================

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_edit_found_report
        )

        // ========================================================
        // Toolbar
        // ========================================================

        val toolbar =
            findViewById<Toolbar>(R.id.toolbar)

        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Edit Found Report"

        toolbar.setNavigationOnClickListener {
            finish()
        }

        // ========================================================
        // Views
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

        btnUpdate =
            findViewById(R.id.btnUpdate)

        progressBar =
            findViewById(R.id.progressBar)

        // ========================================================
        // Session
        // ========================================================

        sessionManager =
            SessionManager(this)

        // ========================================================
        // Receive Existing Report
        // ========================================================

        receiveReportData()

        // ========================================================
        // Date Picker
        // ========================================================

        etDateFound.setOnClickListener {

            showDatePicker()
        }

        // Also allow tapping the surrounding EditText area
        etDateFound.setOnFocusChangeListener { _, hasFocus ->

            if (hasFocus) {

                etDateFound.clearFocus()

                showDatePicker()
            }
        }

        // ========================================================
        // Select Image
        // ========================================================

        btnSelectImage.setOnClickListener {

            imagePicker.launch("image/*")
        }

        // ========================================================
        // Update
        // ========================================================

        btnUpdate.setOnClickListener {

            validateAndUpdate()
        }
    }

    // ============================================================
    // Receive Existing Report
    // ============================================================

    private fun receiveReportData() {

        itemId =
            intent.getLongExtra(
                "itemId",
                -1L
            )

        val itemName =
            intent.getStringExtra("itemName")

        val category =
            intent.getStringExtra("category")

        val description =
            intent.getStringExtra("description")

        val color =
            intent.getStringExtra("color")

        val locationFound =
            intent.getStringExtra("locationFound")

        val dateFound =
            intent.getStringExtra("dateFound")

        existingImageUrl =
            intent.getStringExtra("imageUrl")

        // ========================================================
        // Validate ID
        // ========================================================

        if (itemId == -1L) {

            Toast.makeText(
                this,
                "Invalid found report.",
                Toast.LENGTH_LONG
            ).show()

            finish()

            return
        }

        // ========================================================
        // Populate Fields
        // ========================================================

        etItemName.setText(
            itemName ?: ""
        )

        etCategory.setText(
            category ?: ""
        )

        etDescription.setText(
            description ?: ""
        )

        etColor.setText(
            color ?: ""
        )

        etLocationFound.setText(
            locationFound ?: ""
        )

        etDateFound.setText(
            dateFound ?: ""
        )

        // ========================================================
        // Load Existing Image
        // ========================================================

        val imageUrl =
            buildImageUrl(existingImageUrl)

        if (!imageUrl.isNullOrBlank()) {

            Glide.with(this)
                .load(imageUrl)
                .placeholder(
                    R.drawable.ic_launcher_foreground
                )
                .error(
                    R.drawable.ic_launcher_foreground
                )
                .centerCrop()
                .into(ivSelectedImage)
        }
    }

    // ============================================================
    // Build Image URL
    // ============================================================

    private fun buildImageUrl(
        imageUrl: String?
    ): String? {

        if (imageUrl.isNullOrBlank()) {
            return null
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

    // ============================================================
    // Date Picker
    // ============================================================
    //
    // Allowed:
    //   Past dates  ✅
    //   Today       ✅
    //
    // Not allowed:
    //   Future dates ❌
    //
    // ============================================================

    private fun showDatePicker() {

        val calendar =
            Calendar.getInstance()

        // ========================================================
        // If an existing date is present, open calendar on it
        // ========================================================

        val existingDate =
            etDateFound.text
                .toString()
                .trim()

        if (existingDate.isNotEmpty()) {

            try {

                val format =
                    SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                    )

                format.isLenient = false

                val parsedDate =
                    format.parse(existingDate)

                if (parsedDate != null) {

                    calendar.time =
                        parsedDate
                }

            } catch (_: Exception) {

                // If parsing fails,
                // open today's date.
                calendar.timeInMillis =
                    System.currentTimeMillis()
            }
        }

        // ========================================================
        // Create Date Picker
        // ========================================================

        val datePicker =
            DatePickerDialog(
                this,

                { _, year, month, dayOfMonth ->

                    val selectedCalendar =
                        Calendar.getInstance()

                    selectedCalendar.set(
                        Calendar.YEAR,
                        year
                    )

                    selectedCalendar.set(
                        Calendar.MONTH,
                        month
                    )

                    selectedCalendar.set(
                        Calendar.DAY_OF_MONTH,
                        dayOfMonth
                    )

                    // Remove time component
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

                    // ====================================================
                    // Today's date
                    // ====================================================

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

                    // ====================================================
                    // Extra safety check
                    // ====================================================

                    if (selectedCalendar.after(today)) {

                        Toast.makeText(
                            this,
                            "Future dates are not allowed.",
                            Toast.LENGTH_SHORT
                        ).show()

                        return@DatePickerDialog
                    }

                    // ====================================================
                    // Format date
                    // ====================================================

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

                    etDateFound.error = null
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

        // ========================================================
        // IMPORTANT:
        // Maximum selectable date = TODAY
        // ========================================================

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

        datePicker.datePicker.maxDate =
            today.timeInMillis

        // ========================================================
        // Show Calendar
        // ========================================================

        datePicker.show()
    }

    // ============================================================
    // Validate Date
    // ============================================================

    private fun isValidDate(
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
                format.parse(dateString)
                    ?: return false

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

            // Selected date must be today or before today
            !selectedCalendar.after(today)

        } catch (_: Exception) {

            false
        }
    }

    // ============================================================
    // Validate
    // ============================================================

    private fun validateAndUpdate() {

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
        // Item Name
        // ========================================================

        if (itemName.isEmpty()) {

            etItemName.error =
                "Enter item name"

            etItemName.requestFocus()

            return
        }

        // ========================================================
        // Category
        // ========================================================

        if (category.isEmpty()) {

            etCategory.error =
                "Enter category"

            etCategory.requestFocus()

            return
        }

        // ========================================================
        // Location
        // ========================================================

        if (locationFound.isEmpty()) {

            etLocationFound.error =
                "Enter location where item was found"

            etLocationFound.requestFocus()

            return
        }

        // ========================================================
        // Date Empty
        // ========================================================

        if (dateFound.isEmpty()) {

            etDateFound.error =
                "Select found date"

            etDateFound.requestFocus()

            return
        }

        // ========================================================
        // Date Format + Future Date Validation
        // ========================================================

        if (!isValidDate(dateFound)) {

            etDateFound.error =
                "Select today or a previous date"

            Toast.makeText(
                this,
                "Future dates are not allowed. Please select today or a previous date.",
                Toast.LENGTH_LONG
            ).show()

            etDateFound.requestFocus()

            return
        }

        // ========================================================
        // Image Changed?
        // ========================================================

        if (selectedImageUri != null) {

            uploadNewImage()

        } else {

            updateFoundItem(
                existingImageUrl
            )
        }
    }

    // ============================================================
    // Upload New Image
    // ============================================================

    private fun uploadNewImage() {

        progressBar.visibility =
            View.VISIBLE

        btnUpdate.isEnabled =
            false

        btnSelectImage.isEnabled =
            false

        val uri =
            selectedImageUri

        if (uri == null) {

            updateFoundItem(
                existingImageUrl
            )

            return
        }

        try {

            val file =
                uriToFile(uri)

            val requestFile =
                file.asRequestBody(
                    "image/*".toMediaTypeOrNull()
                )

            val imagePart =
                MultipartBody.Part.createFormData(
                    "image",
                    file.name,
                    requestFile
                )

            val token =
                "Bearer ${sessionManager.getToken()}"

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

                                val newImageUrl =
                                    response.body()!!
                                        .imageUrl

                                updateFoundItem(
                                    newImageUrl
                                )

                            } else {

                                resetButtons()

                                Toast.makeText(
                                    this@EditFoundReportActivity,
                                    "Image upload failed.",
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
                                this@EditFoundReportActivity,
                                t.localizedMessage
                                    ?: "Image upload failed.",
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
                    ?: "Unable to process image.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // ============================================================
    // Update Found Item
    // ============================================================

    private fun updateFoundItem(
        imageUrl: String?
    ) {

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

        val token =
            "Bearer ${sessionManager.getToken()}"

        RetrofitClient.foundItemApi
            .updateFoundItem(
                token,
                itemId,
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
                                this@EditFoundReportActivity,
                                "Found report updated successfully.",
                                Toast.LENGTH_LONG
                            ).show()

                            finish()

                        } else {

                            Toast.makeText(
                                this@EditFoundReportActivity,
                                "Failed to update found report.",
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
                            this@EditFoundReportActivity,
                            t.localizedMessage
                                ?: "Network error.",
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
            contentResolver.openInputStream(uri)
                ?: throw Exception(
                    "Unable to open selected image."
                )

        val file =
            File(
                cacheDir,
                "found_edit_${System.currentTimeMillis()}.jpg"
            )

        FileOutputStream(file).use { output ->

            inputStream.use { input ->

                input.copyTo(output)
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

        btnUpdate.isEnabled =
            true

        btnSelectImage.isEnabled =
            true
    }
}