package io.github.sainiharry.fume.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import io.github.sainiharry.fume.R
import io.github.sainiharry.fume.filter.FilterViewModel
import io.github.sainiharry.fume.filter.FiltersFragment
import io.github.sainiharry.fume.info.InfoActivity
import io.github.sainiharry.fume.repository.AqiRepositoryImpl
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers

class MainActivity : AppCompatActivity() {

    private val model by viewModels<MainViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(
                    AqiRepositoryImpl(
                        this@MainActivity,
                        lifecycleScope,
                        Dispatchers.IO
                    ), Dispatchers.Main.immediate
                ) as T
            }
        }
    }

    private val filterModel by viewModels<FilterViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.item_filter -> {
                    FiltersFragment().show(
                        supportFragmentManager,
                        FiltersFragment::class.simpleName
                    )
                    true
                }
                R.id.item_info -> {
                    startActivity(Intent(this, InfoActivity::class.java))
                    true
                }
                else -> false
            }
        }

        val adapter = AqiAdapter()
        recycler_view.adapter = adapter

        model.aqiData.observe(this) {
            adapter.submitList(it)
        }

        filterModel.filterLiveData.observe(this) {
            model.handleFilters(it)
        }

        model.emptyTextVisible.observe(this) {
            empty_results_textView.isVisible = it
        }

        model.emptyFilterResultsVisible.observe(this) {
            empty_filter_results_textView.isVisible = it
        }

        model.batteryLevel.observe(this) {
            battery_level.text = getString(R.string.battery_level, it)
        }

        model.batteryLevelVisible.observe(this) {
            battery_level.isVisible = it
        }
    }
}