package com.example.activityworld.home.ui

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.activityworld.databinding.FragmentFieldGridListBinding
import com.example.activityworld.home.adapter.FieldCardAdapter
import com.example.activityworld.home.adapter.FieldCardListener
import com.example.activityworld.home.constant.FieldType
import com.example.activityworld.home.datasource.DataSource
import com.example.activityworld.home.model.TypeViewModel

/**
 * [FieldListFragment] allows users to search [PlayingField] by [FieldType].
 */
class FieldListFragment: Fragment() {

    // Binding object instance corresponding to the fragment_start_order.xml layout
    // This property is non-null between the onCreateView() and onDestroyView() lifecycle callbacks,
    // when the view hierarchy is attached to the fragment.
    private var _binding: FragmentFieldGridListBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // Use the 'by activityViewModels()' Kotlin property delegate from the fragment-ktx artifact
    private val typeViewModel: TypeViewModel by activityViewModels()

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ensure the viewModel gets reset when the user goes back to the Home
        typeViewModel.reset()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFieldGridListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.apply {
            // Specify the fragment view as the lifecycle owner of the binding.
            // This is used so that the binding can observe LiveData updates
            lifecycleOwner = viewLifecycleOwner

            // Set the viewModel for dataBinding - this allows the bound layout access
            // to all the data in the ViewModel
            viewModel = typeViewModel
        }

        // Set the RecyclerView for dataBinging
        recyclerView = binding.recyclerView
        // Configures the RecyclerView LayoutManager to be a GridLayout with 3 columns (Items x row)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        val adapter = FieldCardAdapter( FieldCardListener { fieldType ->
            Toast.makeText(context, fieldType.name, Toast.LENGTH_LONG).show()
            onFieldClick(fieldType)
        })
        recyclerView.adapter = adapter
        adapter.submitList(typeViewModel.types)

        recyclerView.setHasFixedSize(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onFieldClick(type: FieldType) {
        typeViewModel.setFieldType(type)

        // Create an action from WordList to DetailList
        // using the required arguments
        val action = FieldListFragmentDirections.actionFieldListFragmentToAvailabilityListFragment(fieldType = type.name)

        // Navigate using that action
        findNavController().navigate(action)
    }
}
