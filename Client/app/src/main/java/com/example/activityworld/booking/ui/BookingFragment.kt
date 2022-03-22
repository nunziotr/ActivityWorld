package com.example.activityworld.booking.ui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.activityworld.R
import com.example.activityworld.booking.adapter.ParticipantAdapter
import com.example.activityworld.booking.adapter.ParticipantListener
import com.example.activityworld.booking.model.BookingViewModel
import com.example.activityworld.booking.model.BookingViewModelFactory
import com.example.activityworld.databinding.FragmentBookingBinding
import com.example.activityworld.home.model.FieldAvailability
import com.example.activityworld.network.ApiStatus
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.*


/**
 * [BookingFragment] allows users to book an [FieldAvailability].
 */
class BookingFragment : Fragment() {

    // Binding object instance corresponding to the fragment_start_order.xml layout
    // This property is non-null between the onCreateView() and onDestroyView() lifecycle callbacks,
    // when the view hierarchy is attached to the fragment.
    private var _binding: FragmentBookingBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // Use the 'by activityViewModels()' Kotlin property delegate from the fragment-ktx artifact
    private lateinit var bookingViewModel: BookingViewModel

    private lateinit var recyclerView: RecyclerView

    // This property holds the AvailabilityAdapter to use Data binding on RecyclerView
    private lateinit var adapter: ParticipantAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve the Availability from the Fragment arguments
        arguments?.let {
            val availability = it.getParcelable<FieldAvailability>("availability")

            // Create an instance of the ViewModel Factory.
            val viewModelFactory = BookingViewModelFactory(availability!!)

            // Get a reference to the ViewModel associated with this fragment.
            bookingViewModel =
                ViewModelProvider(
                    this, viewModelFactory
                )[BookingViewModel::class.java]
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingBinding.inflate(inflater, container, false)

        // Show the Purchase Completed Dialog when the call to DB ends
        bookingViewModel.status.observe(viewLifecycleOwner) { status ->
            Log.v("BOOKING_FRAGMENT", "Status changed: ${status.name}")
            if (status == ApiStatus.DONE) {
                Log.v("BOOKING_FRAGMENT", "Status DONE, calling onClose")
                bookingViewModel.onClose()
            }
        }

        // Add an Observer to the state variable for Navigating when a Quality icon is tapped.
        bookingViewModel.navigateToSleepTracker.observe(viewLifecycleOwner) {
            if (it == true) { // Observed state is true.
                Log.v("BOOKING_FRAGMENT", "Status DONE, showing PurchaseCompletedDialog")
                showPurchaseCompletedDialog()

                // Reset state to make sure we only navigate once, even if the device
                // has a configuration change.
                bookingViewModel.doneNavigating()
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.apply {
            // Specify the fragment view as the lifecycle owner of the binding.
            // This is used so that the binding can observe LiveData updates
            lifecycleOwner = viewLifecycleOwner

            // Set the viewModel for dataBinding - this allows the bound layout access
            // to all the data in the ViewModel
            viewModel = bookingViewModel

            bookingFragment = this@BookingFragment
        }

        // Set the RecyclerView for dataBinging
        recyclerView = binding.recyclerView
        // Configures the RecyclerView LayoutManager to be a GridLayout with 3 columns (Items x row)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Adds a [DividerItemDecoration] between items
        recyclerView.addItemDecoration(
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        )

        // Creates an instance of the adapter for the AvailabilityListFragment RecyclerView to be
        // called inside the onResume() method
        // It is needed to call the 'submitList()'
        adapter = ParticipantAdapter(ParticipantListener {
            // Listener is used to remove participant on "remove button" pressed
            onRemove(it)
        })
        recyclerView.adapter = adapter
        adapter.submitList(bookingViewModel.participants.values.toList())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun onAdd() {
        bookingViewModel.addParticipant()
        adapter.submitList(bookingViewModel.participants.values.toList())
    }

    private fun onRemove(key: Long) {
        bookingViewModel.removeParticipant(key)
        adapter.submitList(bookingViewModel.participants.values.toList())
    }

    fun onSubmit() {
        bookingViewModel.participants.forEach {
            Log.d("<Saved Participants>", "Participant ID : ${it.key} - Name : ${it.value.name}")
        }

        showConfirmationDialog()
    }

    fun onCancel() {
        showCancelDialog()
    }

    /**
     * Cancel the Booking and start over.
     */
    private fun exitBooking() {

        bookingViewModel.field.value?.let { field ->
            // Create an action from BookingFragment to AvailabilityListFragment
            // using the required arguments
            val action = BookingFragmentDirections.actionBookingFragmentToAvailabilityListFragment(fieldType = field.type.name)

            // Reset booking in BookingViewModel
            bookingViewModel.resetBooking()

            // Navigate using that action
            findNavController().navigate(action)
        }
    }

    private fun goHome() {
        // Reset booking in BookingViewModel
        bookingViewModel.resetBooking()
        // Navigate to FieldListFragment
        findNavController().navigate(R.id.action_bookingFragment_to_fieldListFragment)
    }

    /**
     * Wait one second then display the purchase completed Dialog.
     */
    @OptIn(DelicateCoroutinesApi::class)
    private fun purchaseAnimation() {
        // launch a coroutine in viewModelScope
        GlobalScope.launch(Dispatchers.Main) {
            binding.statusImage.visibility = View.VISIBLE
            //binding.statusImage.setImageResource(R.drawable.loading_animation)
            Log.v("BOOKING_FRAGMENT", "Delay started")
            // suspend this coroutine for one second
            delay(1000)
            Log.v("BOOKING_FRAGMENT", "Delay ended")
            // resume in the main dispatcher
            binding.statusImage.visibility = View.GONE
            showPurchaseCompletedDialog()
        }
    }

    /**
     * Creates and shows an AlertDialog asking for confirmation.
     */
    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle(R.string.purchase_alert_title)
            .setMessage(
                getString(
                    R.string.booking_details,
                    bookingViewModel.field.value!!.name,
                    bookingViewModel.dateInString,
                    bookingViewModel.total.value
                )
            )
            .setCancelable(false)
            .setNegativeButton(R.string.cancel) { _, _ ->
            }
            .setPositiveButton(R.string.submit_purchase) { _, _ ->
                bookingViewModel.sendBookingAndParticipants()
            }
            .show()
    }

    /**
     * Creates and shows an AlertDialog asking for purchase completed.
     */
    private fun showPurchaseCompletedDialog() {
        Log.v("BOOKING_FRAGMENT", "showPurchaseCompletedDialog CALLED")
        MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle(R.string.purchase_completed)
            .setMessage(R.string.booking_completed)
            .setCancelable(false)
            .setPositiveButton(androidx.navigation.dynamicfeatures.fragment.R.string.ok) { _, _ ->
                goHome()
            }
            .show()
    }

    /**
     * Creates and shows an AlertDialog asking for canceling booking.
     */
    private fun showCancelDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.cancel_alert_title)
            .setMessage(getString(R.string.cancel_details, getString(R.string.cancel).uppercase()))
            .setCancelable(false)
            .setNegativeButton(R.string.resume) { _, _ ->
            }
            .setPositiveButton(R.string.cancel) { _, _ ->
                exitBooking()
            }
            .show()
    }


    /**
     * Key listener for hiding the keyboard when the "Enter" button is tapped.
     */
    private fun hideKeyboardFrom(context: Context, view: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}