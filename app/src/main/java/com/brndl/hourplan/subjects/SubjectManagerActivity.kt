package com.brndl.hourplan.subjects

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.brndl.hourplan.R
import kotlinx.android.synthetic.main.activity_subject_manager.*
import kotlinx.android.synthetic.main.activity_subject_manager.addButton
import kotlinx.android.synthetic.main.activity_subject_manager.toolbar

class SubjectManagerActivity : AppCompatActivity() {

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subject_manager)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setSupportActionBar(toolbar)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        setupAdapter()
    }

    override fun onResume() {
        super.onResume()
        setupAdapter()
    }

    fun setupAdapter() {

        val adapter =
            SubjectItemAdapter(SubjectManager.getSubjectsForAdapter())
        recyclerView.adapter = adapter

        adapter.setOnClickListener(object : SubjectItemAdapter.OnItemClickListener {
            override fun onItemClick(position: Int, viewHolder: SubjectItemAdapter.SubjectItemViewHolder) {
                val intent = createIntent()
                intent.putExtra("EDIT_SUBJECT", position)
                startActivity(intent)
            }
        })

        addButton.setOnClickListener {
            val intent = createIntent()
            startActivity(intent)
        }
    }

    fun createIntent(): Intent {
        return Intent(this, AddSubjectActivity::class.java)
    }
}
