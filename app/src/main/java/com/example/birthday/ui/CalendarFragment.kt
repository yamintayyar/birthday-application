package com.example.birthday.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.birthday.BirthdayApplication
import com.example.birthday.BirthdayViewModel
import com.example.birthday.BirthdayViewModelFactory
import com.example.birthday.adapter.CalendarListAdapter
import com.example.birthday.databinding.FragmentCalendarBinding

class CalendarFragment : Fragment() {

    private val viewModel: BirthdayViewModel by activityViewModels {
        BirthdayViewModelFactory(
            (activity?.application as BirthdayApplication).database.birthdayDao()
        )
    }

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Creates binding
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val addBirthdayFAB = binding.addBirthdayButton
        addBirthdayFAB.setOnClickListener {
            val action = CalendarFragmentDirections.actionCalendarFragmentToAddBirthday(-1)

            findNavController().navigate(action)
        }

        val adapter =
            CalendarListAdapter { //the function assigned to the onClickListener must be passed from outside, because the
                //ListAdapter does not have access to the navController (because its not a fragment)
                val action = CalendarFragmentDirections.actionCalendarFragmentToAddBirthday(it)

                findNavController().navigate(action)
            }

        val recyclerView = binding.recyclerView

        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.adapter = adapter

        viewModel.allBirthdays.observe(viewLifecycleOwner) { birthdays -> //updates the recyclerview with any updates in the database
            adapter.submitList(birthdays)
        }
    }
}
