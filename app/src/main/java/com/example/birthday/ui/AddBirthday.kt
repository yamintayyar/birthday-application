package com.example.birthday.ui

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.birthday.BirthdayApplication
import com.example.birthday.BirthdayViewModel
import com.example.birthday.BirthdayViewModelFactory
import com.example.birthday.databinding.FragmentAddBirthdayBinding
import kotlinx.android.synthetic.main.fragment_add_birthday.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [AddBirthday.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddBirthday : Fragment() {

    private val viewModel: BirthdayViewModel by activityViewModels {
        BirthdayViewModelFactory(
            (activity?.application as BirthdayApplication).database.birthdayDao()
        )
    }

    private val navigationArgs : AddBirthdayArgs by navArgs()

    private var _binding: FragmentAddBirthdayBinding? = null
    private val binding get() = _binding!!

    private var day = 0
    private var month = 0
    private var year = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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

        val id = navigationArgs.id //gets id argument that was passed while navigating to AddBirthdayFragment (id = -1 by default)

        if (id > -1) { //if we passed in an id (navigated to AddBirthdayFragment to edit a birthday, not to make a new one)

            viewModel.getBirthday(id).observe(viewLifecycleOwner) { bday -> //observing the object is necessary, because the return type of
                //getBirthday() is a LiveData, whose value will be null unless observed, in which case it can be accessed
                binding.apply {
                    name.setText(bday.name, TextView.BufferType.EDITABLE)
                    message.setText(bday.message, TextView.BufferType.EDITABLE)

                    date.updateDate(bday.dateYear, bday.dateMonth, bday.dateDay)

                    addNewBirthday.setOnClickListener {

                        //TODO: add update functionality (make update call in DAO, make update function in viewmodel, and make an update function here)

                        val action = AddBirthdayDirections.actionAddBirthdayToCalendarFragment()
                        findNavController().navigate(action)
                    }
                }
            }
        } else {
            binding.addNewBirthday.setOnClickListener {
                saveBirthday()

                val action = AddBirthdayDirections.actionAddBirthdayToCalendarFragment()
                findNavController().navigate(action)
            }
        }
    }

    private fun saveBirthday() {
        val name = binding.name.text.toString()
        val message = binding.message.text.toString()

        viewModel.saveBirthday(name, day, month, year, message)
    }

    //TODO: add delete functionality for birthday entries
}
