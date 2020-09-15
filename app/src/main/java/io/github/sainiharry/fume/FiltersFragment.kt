package io.github.sainiharry.fume

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_filters.*
import java.text.SimpleDateFormat
import java.util.*

class FiltersFragment : BottomSheetDialogFragment(), DatePickerDialog.OnDateSetListener {

    private val model by activityViewModels<FilterViewModel>()

    private val sdf = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_filters, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentFilter = model.filterLiveData.value
        currentFilter?.let { filter ->
            val id = when (filter.pollutionLevel) {
                PollutionLevel.LOW -> R.id.low_pollution_button
                PollutionLevel.MODERATE -> R.id.moderate_pollution_button
                PollutionLevel.HIGH -> R.id.high_pollution_button
                PollutionLevel.VERY_HIGH -> R.id.very_high_pollutoin_button
                else -> null
            }

            if (id != null) {
                pollution_toggle.check(id)
            }

            filter.date?.let {
                date_input.setText(sdf.format(Date(it)))
            }

        }

        clear_button.setOnClickListener {
            model.handleClearButtonClick()
            dismiss()
        }

        done_button.setOnClickListener {
            model.handleDoneButtonClick()
            dismiss()
        }

        pollution_toggle.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                val pollutionLevel = when (checkedId) {
                    R.id.low_pollution_button -> PollutionLevel.LOW
                    R.id.moderate_pollution_button -> PollutionLevel.MODERATE
                    R.id.high_pollution_button -> PollutionLevel.HIGH
                    R.id.very_high_pollutoin_button -> PollutionLevel.VERY_HIGH
                    else -> null
                }

                model.handlePollutionLevel(pollutionLevel)
            }
        }

        val calendar = Calendar.getInstance()
        date_input.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.datePicker.maxDate = calendar.timeInMillis
            datePickerDialog.show()
        }
        date_layout.setEndIconOnClickListener {
            model.handleDate(null)
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        date_input.setText(sdf.format(calendar.time))
        model.handleDate(calendar.timeInMillis)
    }
}