package com.lostfound

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.android.material.textfield.TextInputEditText

import com.lostfound.adapter.SearchItemAdapter
import com.lostfound.model.ClaimRequest
import com.lostfound.model.FoundItem
import com.lostfound.model.LostItem
import com.lostfound.model.ReminderRequest
import com.lostfound.network.RetrofitClient
import com.lostfound.session.SessionManager

import kotlinx.coroutines.launch

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LostItemsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmpty: TextView
    private lateinit var tvSearchInfo: TextView
    private lateinit var etSearch: TextInputEditText

    private lateinit var sessionManager: SessionManager

    private val searchItems =
        mutableListOf<SearchItem>()

    private lateinit var searchAdapter: SearchItemAdapter

    private val ownFoundItemIds =
        mutableSetOf<Long>()

    private val ownLostItemIds =
        mutableSetOf<Long>()

    private var firstResume = true

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_lost_items
        )

        val toolbar =
            findViewById<Toolbar>(
                R.id.toolbar
            )

        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(
            true
        )

        toolbar.setNavigationOnClickListener {
            finish()
        }

        recyclerView =
            findViewById(
                R.id.recyclerSearchItems
            )

        progressBar =
            findViewById(
                R.id.progressBar
            )

        tvEmpty =
            findViewById(
                R.id.tvEmpty
            )

        tvSearchInfo =
            findViewById(
                R.id.tvSearchInfo
            )

        etSearch =
            findViewById(
                R.id.etSearch
            )

        sessionManager =
            SessionManager(this)

        searchAdapter =
            SearchItemAdapter(
                items = emptyList(),

                currentUserId = -1L,

                ownFoundItemIds =
                    ownFoundItemIds,

                onClaimClick = { item ->
                    showClaimDialog(item)
                },

                onReminderClick = { item ->
                    showReminderConfirmation(item)
                }
            )

        recyclerView.layoutManager =
            LinearLayoutManager(this)

        recyclerView.adapter =
            searchAdapter

        etSearch.addTextChangedListener(
            object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {

                    filterItems(
                        s?.toString()
                            ?.trim()
                            ?: ""
                    )
                }

                override fun afterTextChanged(
                    s: Editable?
                ) {
                }
            }
        )

        loadAllItems()
    }

    override fun onResume() {

        super.onResume()

        if (firstResume) {

            firstResume = false

            return
        }

        if (::sessionManager.isInitialized) {

            loadAllItems()
        }
    }

    private fun loadAllItems() {

        showLoading()

        searchItems.clear()

        ownFoundItemIds.clear()

        ownLostItemIds.clear()

        loadMyLostItemIds()
    }

    // ============================================================
    // LOAD OWN LOST ITEM IDS
    // ============================================================

    private fun loadMyLostItemIds() {

        val token =
            getAuthorizationToken()

        if (token == null) {

            loadMyFoundItemIds()

            return
        }

        RetrofitClient
            .lostItemApi
            .getMyLostItems(token)
            .enqueue(
                object : Callback<List<LostItem>> {

                    override fun onResponse(
                        call: Call<List<LostItem>>,
                        response: Response<List<LostItem>>
                    ) {

                        if (
                            response.isSuccessful &&
                            response.body() != null
                        ) {

                            ownLostItemIds.clear()

                            response.body()!!
                                .forEach { item ->

                                    ownLostItemIds.add(
                                        item.id
                                    )
                                }
                        }

                        loadMyFoundItemIds()
                    }

                    override fun onFailure(
                        call: Call<List<LostItem>>,
                        t: Throwable
                    ) {

                        ownLostItemIds.clear()

                        loadMyFoundItemIds()
                    }
                }
            )
    }

    // ============================================================
    // LOAD OWN FOUND ITEM IDS
    // ============================================================

    private fun loadMyFoundItemIds() {

        val token =
            getAuthorizationToken()

        if (token == null) {

            loadLostItems()

            return
        }

        RetrofitClient
            .foundItemApi
            .getMyFoundItems(token)
            .enqueue(
                object : Callback<List<FoundItem>> {

                    override fun onResponse(
                        call: Call<List<FoundItem>>,
                        response: Response<List<FoundItem>>
                    ) {

                        if (
                            response.isSuccessful &&
                            response.body() != null
                        ) {

                            ownFoundItemIds.clear()

                            response.body()!!
                                .forEach { item ->

                                    ownFoundItemIds.add(
                                        item.id
                                    )
                                }

                            searchAdapter
                                .updateOwnFoundItemIds(
                                    ownFoundItemIds.toSet()
                                )
                        }

                        loadLostItems()
                    }

                    override fun onFailure(
                        call: Call<List<FoundItem>>,
                        t: Throwable
                    ) {

                        ownFoundItemIds.clear()

                        searchAdapter
                            .updateOwnFoundItemIds(
                                emptySet()
                            )

                        loadLostItems()
                    }
                }
            )
    }

    // ============================================================
    // LOAD LOST ITEMS
    // ============================================================

    private fun loadLostItems() {

        val token =
            getAuthorizationToken()

        if (token == null) {

            loadFoundItems()

            return
        }

        RetrofitClient
            .lostItemApi
            .getAllLostItems(token)
            .enqueue(
                object : Callback<List<LostItem>> {

                    override fun onResponse(
                        call: Call<List<LostItem>>,
                        response: Response<List<LostItem>>
                    ) {

                        if (
                            response.isSuccessful &&
                            response.body() != null
                        ) {

                            response.body()!!

                                .filter { item ->

                                    item.id !in
                                            ownLostItemIds
                                }

                                .forEach { item ->

                                    searchItems.add(
                                        SearchItem.fromLostItem(
                                            item
                                        )
                                    )
                                }
                        }

                        loadFoundItems()
                    }

                    override fun onFailure(
                        call: Call<List<LostItem>>,
                        t: Throwable
                    ) {

                        loadFoundItems()
                    }
                }
            )
    }

    // ============================================================
    // LOAD FOUND ITEMS
    // ============================================================

    private fun loadFoundItems() {

        val token =
            getAuthorizationToken()

        if (token == null) {

            progressBar.visibility =
                View.GONE

            displayAllItems()

            return
        }

        RetrofitClient
            .foundItemApi
            .getAllFoundItems(token)
            .enqueue(
                object : Callback<List<FoundItem>> {

                    override fun onResponse(
                        call: Call<List<FoundItem>>,
                        response: Response<List<FoundItem>>
                    ) {

                        progressBar.visibility =
                            View.GONE

                        if (
                            response.isSuccessful &&
                            response.body() != null
                        ) {

                            response.body()!!

                                .filter { item ->

                                    item.status.equals(
                                        "FOUND",
                                        ignoreCase = true
                                    )
                                }

                                .forEach { item ->

                                    searchItems.add(
                                        SearchItem.fromFoundItem(
                                            item
                                        )
                                    )
                                }
                        }

                        displayAllItems()
                    }

                    override fun onFailure(
                        call: Call<List<FoundItem>>,
                        t: Throwable
                    ) {

                        progressBar.visibility =
                            View.GONE

                        displayAllItems()

                        Toast.makeText(
                            this@LostItemsActivity,
                            "Could not load found items.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )
    }

    // ============================================================
    // DISPLAY
    // ============================================================

    private fun displayAllItems() {

        if (searchItems.isEmpty()) {

            showEmpty(
                "No lost or found items have been reported yet."
            )

            return
        }

        recyclerView.visibility =
            View.VISIBLE

        tvEmpty.visibility =
            View.GONE

        tvSearchInfo.text =
            "${searchItems.size} lost and found items"

        searchAdapter.updateItems(
            searchItems.toList()
        )
    }

    // ============================================================
    // FILTER
    // ============================================================

    private fun filterItems(
        query: String
    ) {

        if (query.isEmpty()) {

            displayAllItems()

            return
        }

        val filteredItems =
            searchItems.filter { item ->

                item.itemName.contains(
                    query,
                    ignoreCase = true
                )
            }

        if (filteredItems.isEmpty()) {

            recyclerView.visibility =
                View.GONE

            tvEmpty.text =
                "No item found for \"$query\""

            tvEmpty.visibility =
                View.VISIBLE

            tvSearchInfo.text =
                "No matching items"

        } else {

            recyclerView.visibility =
                View.VISIBLE

            tvEmpty.visibility =
                View.GONE

            tvSearchInfo.text =
                "${filteredItems.size} matching item(s)"

            searchAdapter.updateItems(
                filteredItems
            )
        }
    }

    // ============================================================
    // REMINDER CONFIRMATION
    // ============================================================

    private fun showReminderConfirmation(
        item: SearchItem
    ) {

        AlertDialog.Builder(this)

            .setTitle(
                "Remind User?"
            )

            .setMessage(
                "Are you sure you want to submit information " +
                        "about \"${item.itemName}\" to the admin?\n\n" +
                        "The owner of this lost item will be " +
                        "notified to contact admin."
            )

            .setNegativeButton(
                "Cancel",
                null
            )

            .setPositiveButton(
                "Remind User"
            ) { _, _ ->

                createReminder(item)
            }

            .show()
    }

    // ============================================================
    // CREATE REMINDER
    // ============================================================

    private fun createReminder(
        item: SearchItem
    ) {

        val token =
            getAuthorizationToken()

        if (token == null) {

            Toast.makeText(
                this,
                "Your session has expired. Please login again.",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        progressBar.visibility =
            View.VISIBLE

        lifecycleScope.launch {

            try {

                val response =
                    RetrofitClient
                        .notificationApi
                        .createReminder(
                            token,
                            ReminderRequest(
                                lostItemId =
                                    item.id
                            )
                        )

                progressBar.visibility =
                    View.GONE

                if (
                    response.isSuccessful &&
                    response.body() != null
                ) {

                    Toast.makeText(
                        this@LostItemsActivity,
                        "Information sent to admin successfully.",
                        Toast.LENGTH_LONG
                    ).show()

                } else {

                    Toast.makeText(
                        this@LostItemsActivity,
                        getReminderErrorMessage(
                            response.code()
                        ),
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (
                e: Exception
            ) {

                progressBar.visibility =
                    View.GONE

                Toast.makeText(
                    this@LostItemsActivity,
                    e.localizedMessage
                        ?: "Unable to send reminder.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun getReminderErrorMessage(
        code: Int
    ): String {

        return when (code) {

            400 ->
                "Unable to send reminder. Please try again."

            401 ->
                "Your session has expired. Please login again."

            404 ->
                "Lost item not found."

            else ->
                "Unable to send reminder. Server error $code."
        }
    }

    // ============================================================
    // CLAIM DIALOG
    // ============================================================

    private fun showClaimDialog(
        item: SearchItem
    ) {

        val messageInput =
            EditText(this)

        messageInput.hint =
            "Explain why this item belongs to you"

        messageInput.inputType =
            InputType.TYPE_CLASS_TEXT or
                    InputType.TYPE_TEXT_FLAG_MULTI_LINE

        messageInput.minLines =
            4

        messageInput.setPadding(
            32,
            16,
            32,
            16
        )

        val container =
            android.widget.FrameLayout(this)

        container.setPadding(
            24,
            0,
            24,
            0
        )

        container.addView(
            messageInput
        )

        AlertDialog.Builder(this)

            .setTitle(
                "Request Claim"
            )

            .setMessage(
                "Item: ${item.itemName}\n\n" +
                        "Type: ${item.type}\n\n" +
                        "Please provide a short explanation " +
                        "to support your claim."
            )

            .setView(container)

            .setNegativeButton(
                "Cancel",
                null
            )

            .setPositiveButton(
                "Submit Claim",
                null
            )

            .create()

            .also { dialog ->

                dialog.setOnShowListener {

                    val positiveButton =
                        dialog.getButton(
                            AlertDialog.BUTTON_POSITIVE
                        )

                    positiveButton.setOnClickListener {

                        val message =
                            messageInput.text
                                .toString()
                                .trim()

                        if (message.isEmpty()) {

                            messageInput.error =
                                "Please enter a claim message"

                            messageInput.requestFocus()

                            return@setOnClickListener
                        }

                        createClaim(
                            item,
                            message,
                            dialog
                        )
                    }
                }

                dialog.show()
            }
    }

    // ============================================================
    // CREATE CLAIM
    // ============================================================

    private fun createClaim(
        item: SearchItem,
        message: String,
        dialog: AlertDialog
    ) {

        val token =
            sessionManager.getToken()

        if (token.isNullOrBlank()) {

            Toast.makeText(
                this,
                "Your session has expired. Please login again.",
                Toast.LENGTH_LONG
            ).show()

            return
        }

        val authorization =
            "Bearer $token"

        dialog.getButton(
            AlertDialog.BUTTON_POSITIVE
        ).isEnabled = false

        progressBar.visibility =
            View.VISIBLE

        val request =
            ClaimRequest(
                itemId =
                    item.id,

                itemType =
                    item.type
                        .trim()
                        .uppercase(),

                claimMessage =
                    message
            )

        lifecycleScope.launch {

            try {

                val response =
                    RetrofitClient
                        .claimApi
                        .createClaim(
                            authorization,
                            request
                        )

                progressBar.visibility =
                    View.GONE

                if (
                    response.isSuccessful &&
                    response.body() != null
                ) {

                    dialog.dismiss()

                    loadAllItems()

                    Toast.makeText(
                        this@LostItemsActivity,
                        "Claim request submitted successfully.",
                        Toast.LENGTH_LONG
                    ).show()

                } else {

                    dialog.getButton(
                        AlertDialog.BUTTON_POSITIVE
                    ).isEnabled = true

                    Toast.makeText(
                        this@LostItemsActivity,
                        extractErrorMessage(response),
                        Toast.LENGTH_LONG
                    ).show()
                }

            } catch (
                e: Exception
            ) {

                progressBar.visibility =
                    View.GONE

                dialog.getButton(
                    AlertDialog.BUTTON_POSITIVE
                ).isEnabled = true

                Toast.makeText(
                    this@LostItemsActivity,
                    e.localizedMessage
                        ?: "Unable to submit claim.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun extractErrorMessage(
        response: Response<*>
    ): String {

        return when (response.code()) {

            400 ->
                "Invalid claim request."

            401 ->
                "Your session has expired. Please login again."

            403 ->
                "You are not authorized to request this claim."

            404 ->
                "Item not found."

            else ->
                "Unable to submit claim. " +
                        "Server error ${response.code()}."
        }
    }

    // ============================================================
    // AUTH TOKEN
    // ============================================================

    private fun getAuthorizationToken(): String? {

        val token =
            sessionManager.getToken()

        if (token.isNullOrBlank()) {
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
    // LOADING
    // ============================================================

    private fun showLoading() {

        progressBar.visibility =
            View.VISIBLE

        tvEmpty.visibility =
            View.GONE

        recyclerView.visibility =
            View.GONE

        tvSearchInfo.text =
            "Loading lost and found items..."
    }

    private fun showEmpty(
        message: String
    ) {

        progressBar.visibility =
            View.GONE

        recyclerView.visibility =
            View.GONE

        tvEmpty.text =
            message

        tvEmpty.visibility =
            View.VISIBLE

        tvSearchInfo.text =
            "No available items"
    }
}