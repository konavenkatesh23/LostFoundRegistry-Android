package com.lostfound

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.android.material.button.MaterialButton

import com.lostfound.adapter.ClaimAdapter
import com.lostfound.adapter.MyReportsAdapter
import com.lostfound.adapter.NotificationAdapter

import com.lostfound.model.FoundItem
import com.lostfound.model.LostItem
import com.lostfound.model.NotificationResponse

import com.lostfound.network.RetrofitClient
import com.lostfound.session.SessionManager

import kotlinx.coroutines.launch

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MyReportsActivity : AppCompatActivity() {

    // ============================================================
    // VIEWS
    // ============================================================

    private lateinit var recyclerView: RecyclerView

    private lateinit var progressBar: ProgressBar

    private lateinit var tvEmpty: TextView

    private lateinit var tvSectionTitle: TextView

    private lateinit var btnLostReports: MaterialButton

    private lateinit var btnFoundReports: MaterialButton

    private lateinit var btnClaims: MaterialButton

    private lateinit var btnReminders: MaterialButton


    // ============================================================
    // SESSION
    // ============================================================

    private lateinit var sessionManager: SessionManager


    // ============================================================
    // ADAPTERS
    // ============================================================

    private lateinit var reportsAdapter: MyReportsAdapter

    private lateinit var claimAdapter: ClaimAdapter

    private lateinit var notificationAdapter: NotificationAdapter


    // ============================================================
    // SECTION
    // ============================================================

    private enum class Section {

        LOST,

        FOUND,

        CLAIMS,

        REMINDERS
    }


    private var currentSection =
        Section.LOST


    // ============================================================
    // CLAIM DATA
    // ============================================================

    private val claimItems =
        mutableListOf<ClaimAdapter.ClaimDisplayItem>()


    // ============================================================
    // ON CREATE
    // ============================================================

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )

        setContentView(
            R.layout.activity_my_reports
        )


        // ========================================================
        // TOOLBAR
        // ========================================================

        val toolbar =
            findViewById<Toolbar>(
                R.id.toolbar
            )

        setSupportActionBar(
            toolbar
        )

        supportActionBar?.setDisplayHomeAsUpEnabled(
            true
        )

        supportActionBar?.title =
            "My Reports"

        toolbar.setNavigationOnClickListener {

            finish()
        }


        // ========================================================
        // FIND VIEWS
        // ========================================================

        recyclerView =
            findViewById(
                R.id.recyclerMyReports
            )

        progressBar =
            findViewById(
                R.id.progressBar
            )

        tvEmpty =
            findViewById(
                R.id.tvEmpty
            )

        tvSectionTitle =
            findViewById(
                R.id.tvSectionTitle
            )

        btnLostReports =
            findViewById(
                R.id.btnLostItems
            )

        btnFoundReports =
            findViewById(
                R.id.btnFoundItems
            )

        btnClaims =
            findViewById(
                R.id.btnClaims
            )

        btnReminders =
            findViewById(
                R.id.btnReminders
            )


        // ========================================================
        // RECYCLER VIEW
        // ========================================================

        recyclerView.layoutManager =
            LinearLayoutManager(
                this
            )

        recyclerView.setHasFixedSize(
            false
        )


        // ========================================================
        // SESSION
        // ========================================================

        sessionManager =
            SessionManager(
                this
            )


        // ========================================================
        // LOST BUTTON
        // ========================================================

        btnLostReports.setOnClickListener {

            if (
                currentSection !=
                Section.LOST
            ) {

                currentSection =
                    Section.LOST

                updateButtonAppearance()

                loadMyLostReports()
            }
        }


        // ========================================================
        // FOUND BUTTON
        // ========================================================

        btnFoundReports.setOnClickListener {

            if (
                currentSection !=
                Section.FOUND
            ) {

                currentSection =
                    Section.FOUND

                updateButtonAppearance()

                loadMyFoundReports()
            }
        }


        // ========================================================
        // CLAIMS BUTTON
        // ========================================================

        btnClaims.setOnClickListener {

            if (
                currentSection !=
                Section.CLAIMS
            ) {

                currentSection =
                    Section.CLAIMS

                updateButtonAppearance()

                loadClaims()
            }
        }


        // ========================================================
        // REMINDERS BUTTON
        // ========================================================

        btnReminders.setOnClickListener {

            currentSection =
                Section.REMINDERS

            updateButtonAppearance()

            loadReminders()
        }


        // ========================================================
        // DEFAULT SECTION
        // ========================================================

        currentSection =
            Section.LOST

        updateButtonAppearance()

        loadMyLostReports()
    }


    // ============================================================
    // BUTTON APPEARANCE
    // ============================================================

    private fun updateButtonAppearance() {

        val unselectedColor =
            ColorStateList.valueOf(
                Color.rgb(
                    85,
                    81,
                    95
                )
            )


        // --------------------------------------------------------
        // RESET ALL BUTTONS
        // --------------------------------------------------------

        btnLostReports.backgroundTintList =
            unselectedColor

        btnFoundReports.backgroundTintList =
            unselectedColor

        btnClaims.backgroundTintList =
            unselectedColor

        btnReminders.backgroundTintList =
            unselectedColor


        btnLostReports.setTextColor(
            Color.WHITE
        )

        btnFoundReports.setTextColor(
            Color.WHITE
        )

        btnClaims.setTextColor(
            Color.WHITE
        )

        btnReminders.setTextColor(
            Color.WHITE
        )


        // --------------------------------------------------------
        // SELECTED BUTTON
        // --------------------------------------------------------

        when (
            currentSection
        ) {

            Section.LOST -> {

                btnLostReports.backgroundTintList =
                    ColorStateList.valueOf(
                        Color.rgb(
                            21,
                            101,
                            192
                        )
                    )
            }


            Section.FOUND -> {

                btnFoundReports.backgroundTintList =
                    ColorStateList.valueOf(
                        Color.rgb(
                            76,
                            175,
                            80
                        )
                    )
            }


            Section.CLAIMS -> {

                btnClaims.backgroundTintList =
                    ColorStateList.valueOf(
                        Color.rgb(
                            156,
                            39,
                            176
                        )
                    )
            }


            Section.REMINDERS -> {

                btnReminders.backgroundTintList =
                    ColorStateList.valueOf(
                        Color.rgb(
                            142,
                            68,
                            173
                        )
                    )
            }
        }


        // --------------------------------------------------------
        // SECTION TITLE
        // --------------------------------------------------------

        tvSectionTitle.text =
            when (
                currentSection
            ) {

                Section.LOST ->
                    "My Lost Reports"

                Section.FOUND ->
                    "My Found Reports"

                Section.CLAIMS ->
                    "My Claims"

                Section.REMINDERS ->
                    "My Reminders"
            }
    }


    // ============================================================
    // SHOW LOADING
    // ============================================================

    private fun showLoading(
        message: String
    ) {

        progressBar.visibility =
            View.VISIBLE

        recyclerView.visibility =
            View.VISIBLE

        tvEmpty.visibility =
            View.GONE
    }


    // ============================================================
    // SHOW EMPTY
    // ============================================================

    private fun showEmpty(
        message: String
    ) {

        progressBar.visibility =
            View.GONE

        recyclerView.visibility =
            View.GONE

        tvEmpty.visibility =
            View.VISIBLE

        tvEmpty.text =
            message
    }


    // ============================================================
    // AUTHORIZATION TOKEN
    // ============================================================

    private fun getAuthorizationToken(): String? {

        val token =
            sessionManager.getToken()

        if (
            token.isNullOrBlank()
        ) {

            return null
        }


        return if (
            token.startsWith(
                "Bearer ",
                ignoreCase = true
            )
        ) {

            token

        } else {

            "Bearer $token"
        }
    }


    // ============================================================
    // LOAD MY LOST REPORTS
    // ============================================================

    private fun loadMyLostReports() {

        showLoading(
            "Loading your lost reports..."
        )


        val token =
            getAuthorizationToken()


        if (
            token == null
        ) {

            showEmpty(
                "Your session has expired. Please login again."
            )

            return
        }


        RetrofitClient
            .lostItemApi
            .getMyLostItems(
                token
            )
            .enqueue(
                object :
                    Callback<List<LostItem>> {

                    override fun onResponse(
                        call: Call<List<LostItem>>,
                        response: Response<List<LostItem>>
                    ) {

                        if (
                            currentSection !=
                            Section.LOST
                        ) {

                            return
                        }


                        progressBar.visibility =
                            View.GONE


                        if (
                            response.isSuccessful &&
                            response.body() != null
                        ) {

                            val items =
                                response.body()!!


                            if (
                                items.isEmpty()
                            ) {

                                showEmpty(
                                    "You have not reported any lost items."
                                )

                                return
                            }


                            val reportItems =
                                items.map { item ->

                                    MyReportsAdapter.ReportItem(

                                        id =
                                            item.id,

                                        itemName =
                                            item.itemName,

                                        category =
                                            item.category,

                                        description =
                                            item.description,

                                        color =
                                            item.color,

                                        location =
                                            item.locationLost,

                                        date =
                                            item.dateLost,

                                        imageUrl =
                                            item.imageUrl,

                                        isLost =
                                            true,

                                        status =
                                            item.status
                                    )

                                }.toMutableList()


                            showReports(
                                reportItems
                            )

                        } else {

                            showEmpty(
                                "Unable to load your lost reports."
                            )
                        }
                    }


                    override fun onFailure(
                        call: Call<List<LostItem>>,
                        t: Throwable
                    ) {

                        if (
                            currentSection !=
                            Section.LOST
                        ) {

                            return
                        }


                        progressBar.visibility =
                            View.GONE


                        showEmpty(
                            "Unable to connect to server."
                        )


                        Toast.makeText(
                            this@MyReportsActivity,
                            t.localizedMessage
                                ?: "Network error",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
    }


    // ============================================================
    // LOAD MY FOUND REPORTS
    // ============================================================

    private fun loadMyFoundReports() {

        showLoading(
            "Loading your found reports..."
        )


        val token =
            getAuthorizationToken()


        if (
            token == null
        ) {

            showEmpty(
                "Your session has expired. Please login again."
            )

            return
        }


        RetrofitClient
            .foundItemApi
            .getMyFoundItems(
                token
            )
            .enqueue(
                object :
                    Callback<List<FoundItem>> {

                    override fun onResponse(
                        call: Call<List<FoundItem>>,
                        response: Response<List<FoundItem>>
                    ) {

                        if (
                            currentSection !=
                            Section.FOUND
                        ) {

                            return
                        }


                        progressBar.visibility =
                            View.GONE


                        if (
                            response.isSuccessful &&
                            response.body() != null
                        ) {

                            val items =
                                response.body()!!


                            if (
                                items.isEmpty()
                            ) {

                                showEmpty(
                                    "You have not reported any found items."
                                )

                                return
                            }


                            val reportItems =
                                items.map { item ->

                                    MyReportsAdapter.ReportItem(

                                        id =
                                            item.id,

                                        itemName =
                                            item.itemName,

                                        category =
                                            item.category,

                                        description =
                                            item.description,

                                        color =
                                            item.color,

                                        location =
                                            item.locationFound,

                                        date =
                                            item.dateFound,

                                        imageUrl =
                                            item.imageUrl,

                                        isLost =
                                            false,

                                        status =
                                            item.status
                                    )

                                }.toMutableList()


                            showReports(
                                reportItems
                            )

                        } else {

                            showEmpty(
                                "Unable to load your found reports."
                            )
                        }
                    }


                    override fun onFailure(
                        call: Call<List<FoundItem>>,
                        t: Throwable
                    ) {

                        if (
                            currentSection !=
                            Section.FOUND
                        ) {

                            return
                        }


                        progressBar.visibility =
                            View.GONE


                        showEmpty(
                            "Unable to connect to server."
                        )


                        Toast.makeText(
                            this@MyReportsActivity,
                            t.localizedMessage
                                ?: "Network error",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
    }


    // ============================================================
    // LOAD CLAIMS
    // ============================================================

    private fun loadClaims() {

        showLoading(
            "Loading claims..."
        )


        val token =
            getAuthorizationToken()


        if (
            token == null
        ) {

            showEmpty(
                "Your session has expired. Please login again."
            )

            return
        }


        lifecycleScope.launch {

            try {

                val myResponse =
                    RetrofitClient
                        .claimApi
                        .getMyClaims(
                            token
                        )


                if (
                    currentSection !=
                    Section.CLAIMS
                ) {

                    return@launch
                }


                val receivedResponse =
                    RetrofitClient
                        .claimApi
                        .getReceivedClaims(
                            token
                        )


                if (
                    currentSection !=
                    Section.CLAIMS
                ) {

                    return@launch
                }


                claimItems.clear()


                if (
                    myResponse.isSuccessful
                ) {

                    myResponse
                        .body()
                        ?.forEach { claim ->

                            claimItems.add(

                                ClaimAdapter.ClaimDisplayItem(

                                    claim =
                                        claim,

                                    isReceived =
                                        false
                                )
                            )
                        }
                }


                if (
                    receivedResponse.isSuccessful
                ) {

                    receivedResponse
                        .body()
                        ?.forEach { claim ->

                            claimItems.add(

                                ClaimAdapter.ClaimDisplayItem(

                                    claim =
                                        claim,

                                    isReceived =
                                        true
                                )
                            )
                        }
                }


                if (
                    !myResponse.isSuccessful &&
                    !receivedResponse.isSuccessful
                ) {

                    showEmpty(
                        "Unable to load claims."
                    )

                    return@launch
                }


                if (
                    claimItems.isEmpty()
                ) {

                    showEmpty(
                        "You have no claims yet."
                    )

                    return@launch
                }


                progressBar.visibility =
                    View.GONE

                recyclerView.visibility =
                    View.VISIBLE

                tvEmpty.visibility =
                    View.GONE


                claimAdapter =
                    ClaimAdapter(

                        context =
                            this@MyReportsActivity,

                        items =
                            claimItems
                    )


                recyclerView.adapter =
                    claimAdapter

            } catch (
                e: Exception
            ) {

                progressBar.visibility =
                    View.GONE


                if (
                    currentSection !=
                    Section.CLAIMS
                ) {

                    return@launch
                }


                showEmpty(
                    "Unable to connect to server."
                )


                Toast.makeText(
                    this@MyReportsActivity,
                    e.localizedMessage
                        ?: "Network error",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    // ============================================================
    // LOAD REMINDERS
    // ============================================================

    private fun loadReminders() {

        showLoading(
            "Loading reminders..."
        )


        val token =
            getAuthorizationToken()


        if (
            token == null
        ) {

            showEmpty(
                "Your session has expired. Please login again."
            )

            return
        }


        lifecycleScope.launch {

            try {

                val response =
                    RetrofitClient
                        .notificationApi
                        .getMyNotifications(
                            token
                        )


                if (
                    currentSection !=
                    Section.REMINDERS
                ) {

                    return@launch
                }


                progressBar.visibility =
                    View.GONE


                if (
                    response.isSuccessful
                ) {

                    val notifications =
                        response.body()
                            ?: emptyList()


                    if (
                        notifications.isEmpty()
                    ) {

                        showEmpty(
                            "You have no reminders."
                        )

                        return@launch
                    }


                    recyclerView.visibility =
                        View.VISIBLE

                    tvEmpty.visibility =
                        View.GONE


                    notificationAdapter =
                        NotificationAdapter(
                            notifications.toMutableList()
                        ) { notification ->

                            markNotificationAsRead(
                                notification
                            )
                        }


                    recyclerView.adapter =
                        notificationAdapter

                } else {

                    showEmpty(
                        "Unable to load reminders. Code: ${response.code()}"
                    )
                }

            } catch (
                e: Exception
            ) {

                progressBar.visibility =
                    View.GONE


                if (
                    currentSection !=
                    Section.REMINDERS
                ) {

                    return@launch
                }


                showEmpty(
                    "Unable to connect to server."
                )


                Toast.makeText(
                    this@MyReportsActivity,
                    e.localizedMessage
                        ?: "Network error",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    // ============================================================
    // MARK NOTIFICATION AS READ
    // ============================================================

    private fun markNotificationAsRead(
        notification: NotificationResponse
    ) {

        if (
            notification.isRead
        ) {

            return
        }


        val token =
            getAuthorizationToken()


        if (
            token == null
        ) {

            return
        }


        lifecycleScope.launch {

            try {

                val response =
                    RetrofitClient
                        .notificationApi
                        .markAsRead(
                            token,
                            notification.id
                        )


                if (
                    response.isSuccessful &&
                    currentSection ==
                    Section.REMINDERS
                ) {

                    loadReminders()
                }

            } catch (
                _: Exception
            ) {
                // Ignore read-status failure.
            }
        }
    }


    // ============================================================
    // DISPLAY LOST / FOUND REPORTS
    // ============================================================

    private fun showReports(
        items:
        MutableList<MyReportsAdapter.ReportItem>
    ) {

        recyclerView.visibility =
            View.VISIBLE

        tvEmpty.visibility =
            View.GONE


        reportsAdapter =
            MyReportsAdapter(

                this,

                items,

                onEditClick = { item ->

                    // ------------------------------------------------
                    // CLAIMED ITEM PROTECTION
                    // ------------------------------------------------

                    if (
                        item.status.equals(
                            "CLAIMED",
                            ignoreCase = true
                        )
                    ) {

                        Toast.makeText(
                            this,
                            "Claimed items cannot be edited.",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {

                        openEditScreen(
                            item
                        )
                    }
                },


                onDeleteClick = {
                        item,
                        position ->

                    // ------------------------------------------------
                    // CLAIMED ITEM PROTECTION
                    // ------------------------------------------------

                    if (
                        item.status.equals(
                            "CLAIMED",
                            ignoreCase = true
                        )
                    ) {

                        Toast.makeText(
                            this,
                            "Claimed items cannot be deleted.",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else if (
                        item.isLost
                    ) {

                        showLostDeleteConfirmation(
                            item,
                            position
                        )

                    } else {

                        showFoundDeleteConfirmation(
                            item,
                            position
                        )
                    }
                }
            )


        recyclerView.adapter =
            reportsAdapter
    }


    // ============================================================
    // OPEN EDIT SCREEN
    // ============================================================

    private fun openEditScreen(
        item:
        MyReportsAdapter.ReportItem
    ) {

        // --------------------------------------------------------
        // EXTRA CLAIMED PROTECTION
        // --------------------------------------------------------

        if (
            item.status.equals(
                "CLAIMED",
                ignoreCase = true
            )
        ) {

            Toast.makeText(
                this,
                "Claimed items cannot be edited.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        // ========================================================
        // LOST REPORT
        // ========================================================

        if (
            item.isLost
        ) {

            val intent =
                Intent(
                    this,
                    EditReportActivity::class.java
                )


            intent.putExtra(
                "itemId",
                item.id
            )

            intent.putExtra(
                "reportId",
                item.id
            )

            intent.putExtra(
                "itemName",
                item.itemName
            )

            intent.putExtra(
                "category",
                item.category
            )

            intent.putExtra(
                "description",
                item.description
            )

            intent.putExtra(
                "color",
                item.color
            )

            intent.putExtra(
                "locationLost",
                item.location
            )

            intent.putExtra(
                "dateLost",
                item.date
            )

            intent.putExtra(
                "imageUrl",
                item.imageUrl
            )

            intent.putExtra(
                "isLost",
                true
            )


            startActivity(
                intent
            )

            return
        }


        // ========================================================
        // FOUND REPORT
        // ========================================================

        val intent =
            Intent(
                this,
                EditFoundReportActivity::class.java
            )


        intent.putExtra(
            "itemId",
            item.id
        )

        intent.putExtra(
            "reportId",
            item.id
        )

        intent.putExtra(
            "itemName",
            item.itemName
        )

        intent.putExtra(
            "category",
            item.category
        )

        intent.putExtra(
            "description",
            item.description
        )

        intent.putExtra(
            "color",
            item.color
        )

        intent.putExtra(
            "locationFound",
            item.location
        )

        intent.putExtra(
            "dateFound",
            item.date
        )

        intent.putExtra(
            "imageUrl",
            item.imageUrl
        )

        intent.putExtra(
            "isLost",
            false
        )


        startActivity(
            intent
        )
    }


    // ============================================================
    // LOST DELETE CONFIRMATION
    // ============================================================

    private fun showLostDeleteConfirmation(
        item:
        MyReportsAdapter.ReportItem,

        position: Int
    ) {

        androidx.appcompat.app.AlertDialog.Builder(
            this
        )

            .setTitle(
                "Delete Lost Report"
            )

            .setMessage(
                "Are you sure you want to delete " +
                        "\"${item.itemName}\"?"
            )

            .setNegativeButton(
                "Cancel",
                null
            )

            .setPositiveButton(
                "Delete"
            ) { _, _ ->

                deleteLostItem(
                    item.id,
                    position
                )
            }

            .show()
    }


    // ============================================================
    // FOUND DELETE CONFIRMATION
    // ============================================================

    private fun showFoundDeleteConfirmation(
        item:
        MyReportsAdapter.ReportItem,

        position: Int
    ) {

        androidx.appcompat.app.AlertDialog.Builder(
            this
        )

            .setTitle(
                "Delete Found Report"
            )

            .setMessage(
                "Are you sure you want to delete " +
                        "\"${item.itemName}\"?"
            )

            .setNegativeButton(
                "Cancel",
                null
            )

            .setPositiveButton(
                "Delete"
            ) { _, _ ->

                deleteFoundItem(
                    item.id,
                    position
                )
            }

            .show()
    }


    // ============================================================
    // DELETE LOST ITEM
    // ============================================================

    private fun deleteLostItem(
        itemId: Long,
        position: Int
    ) {

        val token =
            getAuthorizationToken()


        if (
            token == null
        ) {

            Toast.makeText(
                this,
                "Session expired. Please login again.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        progressBar.visibility =
            View.VISIBLE


        RetrofitClient
            .lostItemApi
            .deleteLostItem(
                token,
                itemId
            )
            .enqueue(
                object :
                    Callback<String> {

                    override fun onResponse(
                        call: Call<String>,
                        response: Response<String>
                    ) {

                        progressBar.visibility =
                            View.GONE


                        if (
                            response.isSuccessful
                        ) {

                            removeReport(
                                position
                            )


                            Toast.makeText(
                                this@MyReportsActivity,
                                "Lost report deleted successfully.",
                                Toast.LENGTH_SHORT
                            ).show()

                        } else {

                            Toast.makeText(
                                this@MyReportsActivity,
                                "Unable to delete lost report. Code: ${response.code()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }


                    override fun onFailure(
                        call: Call<String>,
                        t: Throwable
                    ) {

                        progressBar.visibility =
                            View.GONE


                        Toast.makeText(
                            this@MyReportsActivity,
                            t.localizedMessage
                                ?: "Network error",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
    }


    // ============================================================
    // DELETE FOUND ITEM
    // ============================================================

    private fun deleteFoundItem(
        itemId: Long,
        position: Int
    ) {

        val token =
            getAuthorizationToken()


        if (
            token == null
        ) {

            Toast.makeText(
                this,
                "Session expired. Please login again.",
                Toast.LENGTH_SHORT
            ).show()

            return
        }


        progressBar.visibility =
            View.VISIBLE


        RetrofitClient
            .foundItemApi
            .deleteFoundItem(
                token,
                itemId
            )
            .enqueue(
                object :
                    Callback<String> {

                    override fun onResponse(
                        call: Call<String>,
                        response: Response<String>
                    ) {

                        progressBar.visibility =
                            View.GONE


                        if (
                            response.isSuccessful
                        ) {

                            removeReport(
                                position
                            )


                            Toast.makeText(
                                this@MyReportsActivity,
                                "Found report deleted successfully.",
                                Toast.LENGTH_SHORT
                            ).show()

                        } else {

                            Toast.makeText(
                                this@MyReportsActivity,
                                "Unable to delete found report. Code: ${response.code()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }


                    override fun onFailure(
                        call: Call<String>,
                        t: Throwable
                    ) {

                        progressBar.visibility =
                            View.GONE


                        Toast.makeText(
                            this@MyReportsActivity,
                            t.localizedMessage
                                ?: "Network error",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
    }


    // ============================================================
    // REMOVE REPORT
    // ============================================================

    private fun removeReport(
        position: Int
    ) {

        if (
            ::reportsAdapter.isInitialized
        ) {

            reportsAdapter.removeItem(
                position
            )


            if (
                reportsAdapter.itemCount == 0
            ) {

                recyclerView.visibility =
                    View.GONE


                when (
                    currentSection
                ) {

                    Section.LOST -> {

                        showEmpty(
                            "You have not reported any lost items."
                        )
                    }


                    Section.FOUND -> {

                        showEmpty(
                            "You have not reported any found items."
                        )
                    }


                    Section.CLAIMS -> {

                        showEmpty(
                            "You have no claims yet."
                        )
                    }


                    Section.REMINDERS -> {

                        showEmpty(
                            "You have no reminders."
                        )
                    }
                }
            }
        }
    }


    // ============================================================
    // ON RESUME
    // ============================================================

    override fun onResume() {

        super.onResume()


        if (
            !::sessionManager.isInitialized
        ) {

            return
        }


        when (
            currentSection
        ) {

            Section.LOST -> {

                loadMyLostReports()
            }


            Section.FOUND -> {

                loadMyFoundReports()
            }


            Section.CLAIMS -> {

                loadClaims()
            }


            Section.REMINDERS -> {

                loadReminders()
            }
        }
    }
}