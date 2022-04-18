package com.example.birthday.adapter

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.compose.ui.res.stringResource
import androidx.core.content.res.TypedArrayUtils.getString
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
//import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.birthday.R
import com.example.birthday.data.Birthday
import com.example.birthday.data.getFormattedDate
import com.example.birthday.databinding.CalendarItemBinding
import com.example.birthday.ui.CalendarFragmentDirections


class CalendarListAdapter(val onClicked: (Int) -> (Unit)) :
    ListAdapter<Birthday, CalendarListAdapter.CalendarViewHolder>(DiffCallback) {

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Birthday>() {
            override fun areItemsTheSame(oldItem: Birthday, newItem: Birthday): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Birthday, newItem: Birthday): Boolean {
                return oldItem.name == newItem.name
            }
        }
    }

    class CalendarViewHolder(private val binding: CalendarItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(birthday: Birthday) { //binds text fields according to the item at the position
            binding.apply {
                name.text = birthday.name
                date.text = birthday.getFormattedDate(
                    birthday.dateDay,
                    birthday.dateMonth,
                )

                message.text = birthday.message
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val viewHolder = CalendarViewHolder(
            CalendarItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

        return viewHolder
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        val current = getItem(position)

        holder.bind(current)

        holder.itemView.setOnClickListener { onClicked(current.id) }
    }
}
