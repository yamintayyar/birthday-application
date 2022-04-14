package com.example.birthday.ui

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.core.view.get
import androidx.fragment.app.activityViewModels
import com.example.birthday.BirthdayApplication
import com.example.birthday.BirthdayViewModel
import com.example.birthday.BirthdayViewModelFactory
import com.example.birthday.R
import com.example.birthday.data.BirthdayDao
import com.example.birthday.databinding.FragmentAddBirthdayBinding
import com.example.birthday.databinding.FragmentCalendarBinding
import kotlinx.android.synthetic.main.fragment_add_birthday.*
import java.util.*
import kotlin.properties.Delegates

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

        binding.addNewBirthday.setOnClickListener {
            saveBirthday()

            //TODO: navigate back to CalendarFragment
        }
    }

    private fun saveBirthday() {
        val name = binding.name.text.toString()
        val message = binding.message.text.toString()

        viewModel.saveBirthday(name, day, month, year, message)
    }
}
