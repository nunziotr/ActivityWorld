package com.example.activityworld.home.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.activityworld.R
import com.example.activityworld.databinding.FragmentAvailabilityListBinding
import com.example.activityworld.home.adapter.AvailabilityAdapter
import com.example.activityworld.home.adapter.AvailabilityListener
import com.example.activityworld.home.model.FieldViewModel
import com.example.activityworld.home.model.FieldViewModelFactory
import com.example.activityworld.home.model.TAG_FIELD_MODEL
import com.google.android.material.datepicker.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * [AvailabilityListFragment] allows users to search [FieldAvailability] by [PlayingField] and date
 */
class AvailabilityListFragment : Fragment() {

    // Binding object instance corresponding to the fragment_start_order.xml layout
    // This property is non-null between the onCreateView() and onDestroyView() lifecycle callbacks,
    // when the view hierarchy is attached to the fragment.
    private var _binding: FragmentAvailabilityListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // Use the 'by activityViewModels()' Kotlin property delegate from the fragment-ktx artifact
    private lateinit var fieldViewModel: FieldViewModel

    private lateinit var recyclerView: RecyclerView

    // This property holds the AvailabilityAdapter to use Data binding on RecyclerView
    private lateinit var adapter: AvailabilityAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v("AVAILABILITY_FRAGMENT", "onCreate CALLED!")

        arguments?.let {
            // Retrieve the AvailabilityID from the Fragment arguments
            val fieldType = it.getString("fieldType")

            // Create an instance of the ViewModel Factory.
            val viewModelFactory = FieldViewModelFactory(fieldType!!)

            // Get a reference to the ViewModel associated with this fragment.
            fieldViewModel =
                ViewModelProvider(
                    this, viewModelFactory
                )[FieldViewModel::class.java]
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.v("AVAILABILITY_FRAGMENT", "onCreateView CALLED!")
        // Get a reference to the binding object and inflate the fragment views.
        _binding = FragmentAvailabilityListBinding.inflate(inflater, container, false)

        // Update the Fields spinner when [FieldViewModel.fields] changes
        fieldViewModel.fields.observe(viewLifecycleOwner) { value ->
            value?.let { fieldsList ->

                Log.v(TAG_FIELD_MODEL, "Field received, setting the first field: ${fieldsList.first()}")
                fieldViewModel.setField(fieldsList.first())

                Log.v("AVAILABILITY_FRAGMENT", "Observer called with fields: $value")
                // Creates an ArrayAdapter to use for the ExposedDropMenu
                // Context, the DropDown Item layout and array of items to display in the menu
                fieldViewModel.dropMenuItems?.keys?.let {
                    val arrayAdapter = ArrayAdapter(
                        requireContext(),
                        R.layout.dropdown_item,
                        it.toList()
                    )

                    // Set the spinner with the given Field list
                    bindSpinner(binding.fieldSpinnerInner, arrayAdapter)
                }
            }
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.v("AVAILABILITY_FRAGMENT", "onViewCreated CALLED!")


        binding.apply {
            // Specify the fragment view as the lifecycle owner of the binding.
            // This is used so that the binding can observe LiveData updates
            lifecycleOwner = viewLifecycleOwner

            // Set the viewModel for dataBinding - this allows the bound layout access
            // to all the data in the ViewModel
            viewModel = fieldViewModel
            availabilityFragment = this@AvailabilityListFragment

            // Fixes the EditText focus issue which required the EditText
            // to be clicked 2 times
            selectDateEditText.keyListener = null
        }

        // Set the RecyclerView for dataBinging
        recyclerView = binding.recyclerView
        // Configures the RecyclerView LayoutManager to be a GridLayout with 3 columns (Items x row)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)

        // Creates an instance of the adapter for the AvailabilityListFragment RecyclerView to be
        // called inside the onResume() method
        // It is needed to call the 'submitList()'
        adapter = AvailabilityAdapter(AvailabilityListener { availabilityID ->
            binding.nextButton.isEnabled = true
            fieldViewModel.onAvailabilityClicked(availabilityID)
        })

        //adapter.submitList(fieldViewModel.filteredAvailabilitiesByDate)
        recyclerView.adapter = adapter
        //recyclerView.setHasFixedSize(true)
    }


    /**
     * This fragment lifecycle method is called when the view hierarchy associated with the fragment
     * is being removed. As a result, clear out the binding object.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun bindSpinner(spinner: AutoCompleteTextView, arrayAdapter: ArrayAdapter<String>) {
        spinner.setAdapter(arrayAdapter)

        // Set the initial displayed Text inside the ExposedDropMenu
        Log.v("AVAILABILITY_FRAGMENT", "Trying to set setText on fieldSpinner")
        spinner.setText(fieldViewModel.field.value!!.name, false)

        // Configures the ClickListener when an item from the
        // ExposedDropMenu is selected
        spinner.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                // Update the ViewModel and consequently the UI
                val item = fieldViewModel.fields.value?.get(position)
                item?.let {
                    fieldViewModel.setField(it)
                }
            }
    }

    fun showDatePicker() {
        val selectedDateInMillis = fieldViewModel.currentSelectedDate.value


        // Get the calendar instance
        val calendar = Calendar.getInstance()
        // Define end date
        calendar.add(Calendar.DATE, 30)

        val dateValidatorMin: CalendarConstraints.DateValidator = DateValidatorPointForward.now()
        val dateValidatorMax: CalendarConstraints.DateValidator = DateValidatorPointBackward.before(calendar.timeInMillis)

        val listValidators = ArrayList<CalendarConstraints.DateValidator>()
        listValidators.add(dateValidatorMin)
        listValidators.add(dateValidatorMax)
        val validators = CompositeDateValidator.allOf(listValidators)

        val constraints: CalendarConstraints = CalendarConstraints.Builder()
            .setValidator(validators)
            .build()

        // Initiation date picker with
        // MaterialDatePicker.Builder.datePicker()
        // and building it using build()
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Date")
            .setSelection(selectedDateInMillis)
            .setCalendarConstraints(constraints)
            .build()
            .apply {
                // Setting up the event for when ok is clicked
                addOnPositiveButtonClickListener { dateInMillis ->
                    Log.v("DATE_PICKER", "Calling onDateSelected")
                    onDateSelected(dateInMillis)
                    Toast.makeText(requireContext(), "$headerText is selected", Toast.LENGTH_LONG)
                        .show()
                }
                // Setting up the event for when cancelled is clicked
                addOnNegativeButtonClickListener {
                    Toast.makeText(requireContext(), "$headerText is cancelled", Toast.LENGTH_LONG)
                        .show()
                }
                // Setting up the event for when back button is pressed
                addOnCancelListener {
                    Toast.makeText(requireContext(), "Date Picker Cancelled", Toast.LENGTH_LONG)
                        .show()
                }
            }

        // Showing the DatePicker
        datePicker.show(childFragmentManager, "DatePicker")
    }

    private fun onDateSelected(dateTimeStampInMillis: Long) {
        Log.v("AVAILABILITY_FRAGMENT", "Called onDateSelected")
        fieldViewModel.setDate(dateTimeStampInMillis)
        //adapter.submitList(fieldViewModel.filteredAvailabilitiesByDate)
    }

    /**
     * Navigate to the side menu fragment.
     */
    fun goToBooking() {
        fieldViewModel.availability?.let { availability ->

            // Create an action from AvailabilityList to BookingList
            // using the required arguments
            val action =
                AvailabilityListFragmentDirections.actionAvailabilityListFragmentToBookingFragment(
                    availability = availability
                )

            // Navigate using that action
            findNavController().navigate(action)
        }
    }

    /**
     * Cancel the order and start over.
     */
    fun cancelOrder() {
        // Reset booking in BookingViewModel
        fieldViewModel.resetOrder()
        // Navigate to FieldListFragment
        findNavController().navigate(R.id.action_availabilityListFragment_to_fieldListFragment)
    }
}


/* --------------------------------------------------------------------------------  */
/* --------------------------------------------------------------------------------  */
/* --------------------------------------------------------------------------------  */
/* --------------------------------------------------------------------------------  */
/* --------------------------------------------------------------------------------  */
/* --------------------------------------------------------------------------------  */
/* --------------------------------------------------------------------------------  */
/* --------------------------------------------------------------------------------  */
/* --------------------------------------------------------------------------------  */
/* --------------------------------------------------------------------------------  */
/* --------------------------------------------------------------------------------  */
/* --------------------------------------------------------------------------------  */
/* --------------------------------------------------------------------------------  */
/* --------------------------------------------------------------------------------  */
/* --------------------------------------------------------------------------------  */
/* --------------------------------------------------------------------------------  */
/* --------------------------------------------------------------------------------  */
/* --------------------------------------------------------------------------------  */
/* --------------------------------------------------------------------------------  */

/*
    override fun onResume() {
        super.onResume()
        Log.v("AVAILABILITY_FRAGMENT", "onResume CALLED!")
        Log.v("SPINNER", "onResume has been called")

        // Creates an ArrayAdapter to use for the ExposedDropMenu
        // Context, the DropDown Item layout and array of items to display in the menu
        val arrayAdapter = ArrayAdapter(
            requireContext(),
            R.layout.dropdown_item,
            fieldViewModel.dropMenuItems.keys.toList()
        )
        binding.fieldSpinnerInner.setAdapter(arrayAdapter)

        // Set the initial displayed Text inside the ExposedDropMenu
        Log.v("AVAILABILITY_FRAGMENT", "Trying to set setText on fieldSpinner")
        binding.fieldSpinnerInner.setText(fieldViewModel.field.value!!.name, false)

        // Configures the ClickListener when an item from the
        // ExposedDropMenu is selected
        binding.fieldSpinnerInner.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                // Update the ViewModel and consequently the UI
                val item = fieldViewModel.filteredFields[position]
                fieldViewModel.setField(item)

                // Send to the RecyclerView Adapter the updated availabilities
                // adapter.submitList(fieldViewModel.filteredAvailabilitiesByDate)
            }
    }
 */