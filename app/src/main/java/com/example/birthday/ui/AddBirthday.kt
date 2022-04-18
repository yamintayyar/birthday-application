package com.example.birthday.ui

import android.icu.text.DateFormat.Field.DAY_OF_MONTH
import android.icu.text.DateFormat.Field.DAY_OF_WEEK
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.birthday.BirthdayApplication
import com.example.birthday.BirthdayViewModel
import com.example.birthday.BirthdayViewModelFactory
import com.example.birthday.data.Birthday
import com.example.birthday.databinding.FragmentAddBirthdayBinding
import kotlinx.android.synthetic.main.fragment_add_birthday.*
import java.time.LocalDateTime
import java.util.*

class AddBirthday : Fragment() {

    private val viewModel: BirthdayViewModel by activityViewModels {
        BirthdayViewModelFactory(
            (activity?.application as BirthdayApplication).database.birthdayDao()
        )
    }

    private val navigationArgs: AddBirthdayArgs by navArgs()

    private var _binding: FragmentAddBirthdayBinding? = null
    private val binding get() = _binding!!

    private var day = 0
    private var month = 0
    private var year = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddBirthdayBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O) //ensures that the android version is compatible, satisfies the setOnDateChangedListener() function
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.date.setOnDateChangedListener { datePicker, i, i2, i3 -> //saves date whenever user changes the selected date on the DatePicker
            day = i3
            month = i2
            year = i
        }

        val id =
            navigationArgs.id //gets id argument that was passed while navigating to AddBirthdayFragment (id = -1 by default)

        if (id > -1) { //if we passed in an id (navigated to AddBirthdayFragment to edit a birthday, not to make a new one)

            viewModel.getBirthday(id)
                .observe(viewLifecycleOwner) { bday -> //observing the object is necessary, because the return type of
                    //getBirthday() is a LiveData, whose value will be null unless observed, in which case it can be accessed

                    binding.apply {
                        name.setText(bday.name, TextView.BufferType.EDITABLE)
                        message.setText(bday.message, TextView.BufferType.EDITABLE)

                        date.updateDate(bday.dateYear, bday.dateMonth, bday.dateDay)

                        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Edit An Existing Birthday"

                        saveBirthday.setOnClickListener {

                            if (day == 0) { //if the user has not changed the date (the user cannot pick 0) (in case the birthday is the current date)

                                val calendar : Calendar = Calendar.getInstance()

                                val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
                                val currentMonth = calendar.get(Calendar.MONTH)
                                val currentYear = calendar.get(Calendar.YEAR)

                                day = currentDay
                                month = currentMonth
                                year = currentYear
                            }

                            val name = binding.name.text.toString()

                            if (name == "") { // if the name field is empty, show the user an error

                                binding.name.error = "The name can not be left empty. Please input a name to be associated with this birthday."
                            } else {

                                updateBirthday(
                                    id,
                                    binding.name.text.toString(),
                                    binding.message.text.toString()
                                )

                                val action =
                                    AddBirthdayDirections.actionAddBirthdayToCalendarFragment()
                                findNavController().navigate(action)
                            }
                        }

                        deleteBirthday.visibility = View.VISIBLE

                        deleteBirthday.setOnClickListener {
                            deleteBirthday(bday)
                            val action =
                                AddBirthdayDirections.actionAddBirthdayToCalendarFragment()
                            findNavController().navigate(action)
                        }
                    }
                }

        } else {
            binding.saveBirthday.setOnClickListener {

                if (day == 0) { //if the user has not changed the date (the user cannot pick 0) (in case the birthday is the current date)

                    val calendar : Calendar = Calendar.getInstance()

                    val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
                    val currentMonth = calendar.get(Calendar.MONTH)
                    val currentYear = calendar.get(Calendar.YEAR)

                    day = currentDay
                    month = currentMonth
                    year = currentYear
                }

                val name = binding.name.text.toString()

                if (name == "") { // if the name field is empty, show the user an error

                    binding.name.error = "The name can not be left empty. Please input a name to be associated with this birthday."
                } else {

                    viewModel.saveBirthday(
                        name = binding.name.text.toString(),
                        day = day,
                        month = month,
                        year = year,
                        message = binding.message.text.toString()
                    )

                    val action = AddBirthdayDirections.actionAddBirthdayToCalendarFragment()
                    findNavController().navigate(action)
                }
            }
        }
    }

    private fun updateBirthday(
        id: Int,
        name: String,
        message: String,
    ) {
        viewModel.updateBirthday(id, name, message, day, month, year)
    }

    private fun deleteBirthday(
        birthday: Birthday,
    ) {
        viewModel.deleteBirthday(birthday)
    }
}
