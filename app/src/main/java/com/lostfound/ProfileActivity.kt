package com.lostfound

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.WindowCompat
import androidx.core.widget.NestedScrollView
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.lostfound.model.ProfileResponse
import com.lostfound.model.ProfileUpdateRequest
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

class ProfileActivity : AppCompatActivity() {

    // ============================================================
    // Views
    // ============================================================

    private lateinit var profileScrollView: NestedScrollView

    private lateinit var ivProfile: ImageView

    private lateinit var etFullName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPhone: TextInputEditText
    private lateinit var etAddress: TextInputEditText

    private lateinit var actvCountryCode: AutoCompleteTextView

    private lateinit var btnChangePhoto: MaterialButton
    private lateinit var btnSaveProfile: MaterialButton

    private lateinit var progressBar: ProgressBar

    // ============================================================
    // Session
    // ============================================================

    private lateinit var sessionManager: SessionManager

    // ============================================================
    // Profile Image
    // ============================================================

    private var uploadedProfileImageUrl: String? = null

    private var selectedImageUri: Uri? = null

    // ============================================================
    // Country Code
    // ============================================================

    private var selectedCountryCode = "+91"

    // ============================================================
    // Gallery Launcher
    // ============================================================

    private val galleryLauncher =
        registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->

            if (uri != null) {

                selectedImageUri = uri

                ivProfile.setImageURI(uri)

                uploadProfileImage(uri)
            }
        }


    // ============================================================
    // onCreate
    // ============================================================

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        // --------------------------------------------------------
        // Allow Android to resize the window when keyboard opens
        // --------------------------------------------------------

        WindowCompat.setDecorFitsSystemWindows(
            window,
            true
        )

        window.setSoftInputMode(
            android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        )

        setContentView(
            R.layout.activity_profile
        )

        sessionManager =
            SessionManager(this)

        setupToolbar()

        initializeViews()

        setupCountryCodes()

        setupListeners()

        setupKeyboardScrolling()

        loadProfile()
    }


    // ============================================================
    // Toolbar
    // ============================================================

    private fun setupToolbar() {

        val toolbar =
            findViewById<Toolbar>(
                R.id.toolbar
            )

        setSupportActionBar(toolbar)

        supportActionBar?.title =
            "My Profile"

        supportActionBar?.setDisplayHomeAsUpEnabled(
            true
        )

        toolbar.setNavigationOnClickListener {

            finish()
        }
    }


    // ============================================================
    // Initialize Views
    // ============================================================

    private fun initializeViews() {

        profileScrollView =
            findViewById(
                R.id.profileScrollView
            )

        ivProfile =
            findViewById(
                R.id.ivProfile
            )

        etFullName =
            findViewById(
                R.id.etFullName
            )

        etEmail =
            findViewById(
                R.id.etEmail
            )

        etPhone =
            findViewById(
                R.id.etPhone
            )

        etAddress =
            findViewById(
                R.id.etAddress
            )

        actvCountryCode =
            findViewById(
                R.id.actvCountryCode
            )

        btnChangePhoto =
            findViewById(
                R.id.btnChangePhoto
            )

        btnSaveProfile =
            findViewById(
                R.id.btnSaveProfile
            )

        progressBar =
            findViewById(
                R.id.progressBar
            )
    }


    // ============================================================
    // Country Code Dropdown
    // ============================================================

    private fun setupCountryCodes() {

        val countryCodes =
            listOf(

                "🇮🇳  +91",
                "🇺🇸  +1",
                "🇨🇦  +1",
                "🇬🇧  +44",
                "🇦🇺  +61",
                "🇳🇿  +64",
                "🇦🇪  +971",
                "🇸🇦  +966",
                "🇶🇦  +974",
                "🇴🇲  +968",
                "🇰🇼  +965",
                "🇧🇭  +973",
                "🇸🇬  +65",
                "🇲🇾  +60",
                "🇮🇩  +62",
                "🇹🇭  +66",
                "🇵🇭  +63",
                "🇯🇵  +81",
                "🇰🇷  +82",
                "🇨🇳  +86",
                "🇭🇰  +852",
                "🇹🇼  +886",
                "🇩🇪  +49",
                "🇫🇷  +33",
                "🇮🇹  +39",
                "🇪🇸  +34",
                "🇵🇹  +351",
                "🇳🇱  +31",
                "🇧🇪  +32",
                "🇨🇭  +41",
                "🇦🇹  +43",
                "🇸🇪  +46",
                "🇳🇴  +47",
                "🇩🇰  +45",
                "🇫🇮  +358",
                "🇮🇪  +353",
                "🇵🇱  +48",
                "🇷🇺  +7",
                "🇺🇦  +380",
                "🇹🇷  +90",
                "🇿🇦  +27",
                "🇳🇬  +234",
                "🇰🇪  +254",
                "🇪🇬  +20",
                "🇧🇷  +55",
                "🇦🇷  +54",
                "🇲🇽  +52",
                "🇨🇱  +56",
                "🇨🇴  +57"
            )


        val adapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                countryCodes
            )


        actvCountryCode.setAdapter(
            adapter
        )

        actvCountryCode.setText(
            "🇮🇳  +91",
            false
        )


        actvCountryCode.setOnItemClickListener { _, _, position, _ ->

            val selected =
                countryCodes[position]

            selectedCountryCode =
                selected.substringAfter(" ")
                    .trim()
        }


        actvCountryCode.setOnClickListener {

            actvCountryCode.showDropDown()
        }
    }


    // ============================================================
    // Listeners
    // ============================================================

    private fun setupListeners() {

        // --------------------------------------------------------
        // Change Profile Photo
        // --------------------------------------------------------

        btnChangePhoto.setOnClickListener {

            openGallery()
        }


        // --------------------------------------------------------
        // Save Profile
        // --------------------------------------------------------

        btnSaveProfile.setOnClickListener {

            hideKeyboard()

            updateProfile()
        }
    }


    // ============================================================
    // Keyboard / Address Scrolling
    // ============================================================

    private fun setupKeyboardScrolling() {

        // --------------------------------------------------------
        // Address field
        // --------------------------------------------------------

        etAddress.setOnFocusChangeListener { view, hasFocus ->

            if (hasFocus) {

                view.postDelayed({

                    profileScrollView.smoothScrollTo(
                        0,
                        view.bottom + 350
                    )

                }, 300)
            }
        }


        // --------------------------------------------------------
        // Phone field
        // --------------------------------------------------------

        etPhone.setOnFocusChangeListener { view, hasFocus ->

            if (hasFocus) {

                view.postDelayed({

                    profileScrollView.smoothScrollTo(
                        0,
                        view.bottom + 250
                    )

                }, 300)
            }
        }


        // --------------------------------------------------------
        // Make sure address stays visible while typing
        // --------------------------------------------------------

        etAddress.setOnClickListener {

            etAddress.postDelayed({

                profileScrollView.smoothScrollTo(
                    0,
                    etAddress.bottom + 350
                )

            }, 250)
        }
    }


    // ============================================================
    // Hide Keyboard
    // ============================================================

    private fun hideKeyboard() {

        val imm =
            getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager

        imm.hideSoftInputFromWindow(
            currentFocus?.windowToken,
            0
        )

        currentFocus?.clearFocus()
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
    // Load Profile
    // ============================================================

    private fun loadProfile() {

        showLoading(true)

        val token =
            "Bearer ${sessionManager.getToken()}"


        RetrofitClient.profileApi
            .getProfile(token)
            .enqueue(
                object :
                    Callback<ProfileResponse> {

                    override fun onResponse(
                        call: Call<ProfileResponse>,
                        response: Response<ProfileResponse>
                    ) {

                        showLoading(false)

                        if (
                            response.isSuccessful &&
                            response.body() != null
                        ) {

                            val profile =
                                response.body()!!

                            displayProfile(
                                profile
                            )

                        } else {

                            Toast.makeText(
                                this@ProfileActivity,
                                "Unable to load profile.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }


                    override fun onFailure(
                        call: Call<ProfileResponse>,
                        t: Throwable
                    ) {

                        showLoading(false)

                        Toast.makeText(
                            this@ProfileActivity,
                            t.localizedMessage
                                ?: "Network error",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
    }


    // ============================================================
    // Display Profile
    // ============================================================

    private fun displayProfile(
        profile: ProfileResponse
    ) {

        // --------------------------------------------------------
        // Read-only
        // --------------------------------------------------------

        etFullName.setText(
            profile.fullName
        )

        etEmail.setText(
            profile.email
        )


        // --------------------------------------------------------
        // Phone
        // --------------------------------------------------------

        val fullPhone =
            profile.phone ?: ""

        val parsed =
            parsePhoneNumber(
                fullPhone
            )

        selectedCountryCode =
            parsed.first

        etPhone.setText(
            parsed.second
        )

        setCountryCodeDisplay(
            selectedCountryCode
        )


        // --------------------------------------------------------
        // Address
        // --------------------------------------------------------

        etAddress.setText(
            profile.address ?: ""
        )


        // --------------------------------------------------------
        // Profile Image
        // --------------------------------------------------------

        uploadedProfileImageUrl =
            profile.profileImageUrl

        loadProfileImage(
            profile.profileImageUrl
        )
    }


    // ============================================================
    // Parse Phone Number
    // ============================================================

    private fun parsePhoneNumber(
        phone: String
    ): Pair<String, String> {

        val supportedCodes =
            listOf(
                "+971",
                "+966",
                "+974",
                "+968",
                "+965",
                "+973",
                "+852",
                "+886",
                "+358",
                "+351",
                "+380",
                "+234",
                "+254",
                "+591",
                "+593",
                "+595",
                "+598",
                "+54",
                "+55",
                "+56",
                "+57",
                "+52",
                "+61",
                "+64",
                "+81",
                "+82",
                "+86",
                "+65",
                "+60",
                "+62",
                "+66",
                "+63",
                "+91",
                "+44",
                "+49",
                "+33",
                "+39",
                "+34",
                "+31",
                "+32",
                "+41",
                "+43",
                "+46",
                "+47",
                "+45",
                "+353",
                "+48",
                "+7",
                "+90",
                "+27",
                "+20",
                "+1"
            )

        for (code in supportedCodes) {

            if (phone.startsWith(code)) {

                return Pair(
                    code,
                    phone.removePrefix(code)
                )
            }
        }

        return Pair(
            "+91",
            phone.removePrefix("+91")
        )
    }


    // ============================================================
    // Set Country Code Display
    // ============================================================

    private fun setCountryCodeDisplay(
        code: String
    ) {

        val display =
            when (code) {

                "+91" ->
                    "🇮🇳  +91"

                "+1" ->
                    "🇺🇸  +1"

                "+44" ->
                    "🇬🇧  +44"

                "+61" ->
                    "🇦🇺  +61"

                "+971" ->
                    "🇦🇪  +971"

                "+966" ->
                    "🇸🇦  +966"

                "+974" ->
                    "🇶🇦  +974"

                "+968" ->
                    "🇴🇲  +968"

                "+965" ->
                    "🇰🇼  +965"

                "+973" ->
                    "🇧🇭  +973"

                "+65" ->
                    "🇸🇬  +65"

                "+60" ->
                    "🇲🇾  +60"

                "+62" ->
                    "🇮🇩  +62"

                "+66" ->
                    "🇹🇭  +66"

                "+63" ->
                    "🇵🇭  +63"

                "+81" ->
                    "🇯🇵  +81"

                "+82" ->
                    "🇰🇷  +82"

                "+86" ->
                    "🇨🇳  +86"

                "+852" ->
                    "🇭🇰  +852"

                "+886" ->
                    "🇹🇼  +886"

                "+49" ->
                    "🇩🇪  +49"

                "+33" ->
                    "🇫🇷  +33"

                "+39" ->
                    "🇮🇹  +39"

                "+34" ->
                    "🇪🇸  +34"

                "+31" ->
                    "🇳🇱  +31"

                "+32" ->
                    "🇧🇪  +32"

                "+41" ->
                    "🇨🇭  +41"

                "+43" ->
                    "🇦🇹  +43"

                "+46" ->
                    "🇸🇪  +46"

                "+47" ->
                    "🇳🇴  +47"

                "+45" ->
                    "🇩🇰  +45"

                "+358" ->
                    "🇫🇮  +358"

                "+353" ->
                    "🇮🇪  +353"

                "+48" ->
                    "🇵🇱  +48"

                "+7" ->
                    "🇷🇺  +7"

                "+380" ->
                    "🇺🇦  +380"

                "+90" ->
                    "🇹🇷  +90"

                "+27" ->
                    "🇿🇦  +27"

                "+234" ->
                    "🇳🇬  +234"

                "+254" ->
                    "🇰🇪  +254"

                "+20" ->
                    "🇪🇬  +20"

                "+55" ->
                    "🇧🇷  +55"

                "+54" ->
                    "🇦🇷  +54"

                "+52" ->
                    "🇲🇽  +52"

                "+56" ->
                    "🇨🇱  +56"

                "+57" ->
                    "🇨🇴  +57"

                else ->
                    code
            }

        actvCountryCode.setText(
            display,
            false
        )
    }


    // ============================================================
    // Load Profile Image
    // ============================================================

    private fun loadProfileImage(
        imageUrl: String?
    ) {

        if (imageUrl.isNullOrBlank()) {

            ivProfile.setImageResource(
                R.drawable.ic_launcher_foreground
            )

            return
        }

        val fullImageUrl =
            buildImageUrl(
                imageUrl
            )

        Glide.with(this)
            .load(fullImageUrl)
            .placeholder(
                R.drawable.ic_launcher_foreground
            )
            .error(
                R.drawable.ic_launcher_foreground
            )
            .centerCrop()
            .into(ivProfile)
    }


    // ============================================================
    // Build Image URL
    // ============================================================

    private fun buildImageUrl(
        imageUrl: String
    ): String {

        return when {

            imageUrl.startsWith(
                "http://"
            ) ||
                    imageUrl.startsWith(
                        "https://"
                    ) -> {

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
    // Upload / Replace Profile Image
    // ============================================================

    private fun uploadProfileImage(
        uri: Uri
    ) {

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


        val requestBody =
            imageFile.asRequestBody(
                "image/*".toMediaTypeOrNull()
            )


        val multipartBody =
            MultipartBody.Part.createFormData(
                "image",
                imageFile.name,
                requestBody
            )


        val token =
            "Bearer ${sessionManager.getToken()}"


        showLoading(true)


        RetrofitClient.profileApi
            .replaceProfileImage(
                token,
                multipartBody
            )
            .enqueue(
                object :
                    Callback<ProfileResponse> {

                    override fun onResponse(
                        call: Call<ProfileResponse>,
                        response: Response<ProfileResponse>
                    ) {

                        showLoading(false)

                        if (
                            response.isSuccessful &&
                            response.body() != null
                        ) {

                            val profile =
                                response.body()!!

                            uploadedProfileImageUrl =
                                profile.profileImageUrl

                            loadProfileImage(
                                profile.profileImageUrl
                            )

                            Toast.makeText(
                                this@ProfileActivity,
                                "Profile picture replaced successfully.",
                                Toast.LENGTH_SHORT
                            ).show()

                        } else {

                            Toast.makeText(
                                this@ProfileActivity,
                                "Profile picture replacement failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }


                    override fun onFailure(
                        call: Call<ProfileResponse>,
                        t: Throwable
                    ) {

                        showLoading(false)

                        Toast.makeText(
                            this@ProfileActivity,
                            "Upload error: ${
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
                    ?: return null


            val fileName =
                "profile_${System.currentTimeMillis()}.jpg"


            val tempFile =
                File(
                    cacheDir,
                    fileName
                )


            val outputStream =
                FileOutputStream(
                    tempFile
                )


            inputStream.use { input ->

                outputStream.use { output ->

                    input.copyTo(
                        output
                    )
                }
            }


            tempFile

        } catch (e: Exception) {

            e.printStackTrace()

            null
        }
    }


    // ============================================================
    // Update Profile
    // ============================================================

    private fun updateProfile() {

        val phoneNumber =
            etPhone.text
                .toString()
                .trim()

        val address =
            etAddress.text
                .toString()
                .trim()


        // --------------------------------------------------------
        // Validate Phone
        // --------------------------------------------------------

        if (phoneNumber.isEmpty()) {

            etPhone.error =
                "Enter your phone number"

            etPhone.requestFocus()

            return
        }


        if (phoneNumber.length < 6) {

            etPhone.error =
                "Enter a valid phone number"

            etPhone.requestFocus()

            return
        }


        // --------------------------------------------------------
        // Validate Address
        // --------------------------------------------------------

        if (address.isEmpty()) {

            etAddress.error =
                "Enter your address"

            etAddress.requestFocus()

            return
        }


        // --------------------------------------------------------
        // Complete Phone Number
        // --------------------------------------------------------

        val completePhone =
            selectedCountryCode +
                    phoneNumber


        // --------------------------------------------------------
        // Request
        // --------------------------------------------------------

        val request =
            ProfileUpdateRequest(

                phone =
                    completePhone,

                profileImageUrl =
                    uploadedProfileImageUrl,

                address =
                    address
            )


        val token =
            "Bearer ${sessionManager.getToken()}"


        // --------------------------------------------------------
        // API
        // --------------------------------------------------------

        showLoading(true)


        RetrofitClient.profileApi
            .updateProfile(
                token,
                request
            )
            .enqueue(
                object :
                    Callback<ProfileResponse> {

                    override fun onResponse(
                        call: Call<ProfileResponse>,
                        response: Response<ProfileResponse>
                    ) {

                        showLoading(false)

                        if (
                            response.isSuccessful &&
                            response.body() != null
                        ) {

                            displayProfile(
                                response.body()!!
                            )

                            Toast.makeText(
                                this@ProfileActivity,
                                "Profile updated successfully.",
                                Toast.LENGTH_SHORT
                            ).show()

                        } else {

                            Toast.makeText(
                                this@ProfileActivity,
                                "Unable to update profile.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }


                    override fun onFailure(
                        call: Call<ProfileResponse>,
                        t: Throwable
                    ) {

                        showLoading(false)

                        Toast.makeText(
                            this@ProfileActivity,
                            "Update error: ${
                                t.localizedMessage
                            }",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
    }


    // ============================================================
    // Loading State
    // ============================================================

    private fun showLoading(
        loading: Boolean
    ) {

        if (loading) {

            progressBar.visibility =
                View.VISIBLE

            btnSaveProfile.isEnabled =
                false

            btnChangePhoto.isEnabled =
                false

        } else {

            progressBar.visibility =
                View.GONE

            btnSaveProfile.isEnabled =
                true

            btnChangePhoto.isEnabled =
                true
        }
    }


    // ============================================================
    // onDestroy
    // ============================================================

    override fun onDestroy() {

        super.onDestroy()

        selectedImageUri = null
    }
}