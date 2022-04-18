package com.example.birthday.ui

import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.birthday.BirthdayApplication
import com.example.birthday.BirthdayViewModel
import com.example.birthday.BirthdayViewModelFactory
import com.example.birthday.adapter.CalendarListAdapter
import com.example.birthday.data.Birthday
import com.example.birthday.databinding.FragmentCalendarBinding

class CalendarFragment : Fragment() {

    private val viewModel: BirthdayViewModel by activityViewModels {
        BirthdayViewModelFactory(
            (activity?.application as BirthdayApplication).database.birthdayDao()
        )
    }

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    val dateComparator = Comparator { bday1: Birthday, bday2: Birthday ->
        ((bday1.dateMonth * 30) + bday1.dateDay) - ((bday2.dateMonth * 30) + bday2.dateDay)
        //assumes every month is 30 days long, and calculates which date comes before which
    }

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

    @RequiresApi(Build.VERSION_CODES.N)
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

            val sortedBirthdays = birthdays.sortedWith(dateComparator)

            val calendar: Calendar =
                Calendar.getInstance() //required to compare the current date against the now sorted list, to move them to the end
            val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
            val currentMonth = calendar.get(Calendar.MONTH)
            val comparableDate =
                ((currentMonth + 1) * 30) + currentDay //has a date representing the current date that is comparable against the others

            val displayList = mutableListOf<Birthday>()
            val appendList = mutableListOf<Birthday>()

            for (bday in sortedBirthdays) { //adds birthdays to the list to be displayed ONLY if they are still upcoming in the current year
                if (((bday.dateMonth * 30) + bday.dateDay) < comparableDate) {
                    appendList.add(bday)
                } else {
                    displayList.add(bday)
                }
            }

            for (bday in appendList) { //adds the birthdays that have already passed in that current year
                displayList.add(bday)
            }

            adapter.submitList(displayList)
        }
    }
}
