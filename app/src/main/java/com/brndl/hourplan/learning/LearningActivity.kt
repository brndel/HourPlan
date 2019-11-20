package com.brndl.hourplan.learning

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.brndl.hourplan.R
import com.brndl.hourplan.TextItemAdapter
import com.brndl.hourplan.subjects.SubjectItemAdapter
import com.brndl.hourplan.subjects.SubjectManager
import kotlinx.android.synthetic.main.activity_learning.*
import kotlinx.android.synthetic.main.activity_learning_test.view.*

class LearningActivity : AppCompatActivity() {

    enum class States {
        SUBJECT, TOPIC, LEARNABLE
    }

    private var currentState = States.SUBJECT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learning)


        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.item_delete) {
                when (currentState) {
                    States.LEARNABLE -> {
                        val builder = AlertDialog.Builder(this)
                        builder.setMessage(
                            getString(R.string.confirm_delete_format).format(
                                currentTopic
                            )
                        )
                            .setCancelable(true)
                            .setPositiveButton(R.string.delete) { _, _ ->
                                LearningManager.removeTopic(currentSubject, currentTopic)
                                setupTopicAdapter(currentSubject)
                                previousRecyclerView()
                            }
                            .setNegativeButton(R.string.cancel) { p0, _ -> p0?.cancel() }
                        builder.create().show()
                    }
                    States.TOPIC -> {
                        val builder = AlertDialog.Builder(this)
                        builder.setMessage(
                            getString(R.string.confirm_delete_format).format(
                                SubjectManager.getSubject(currentSubject)?.name
                            )
                        )
                            .setCancelable(true)
                            .setPositiveButton(
                                R.string.delete
                            ) { _, _ ->
                                LearningManager.removeSubject(currentSubject)
                                setupSubjectAdapter()
                                previousRecyclerView()
                            }
                            .setNegativeButton(R.string.cancel) { p0, _ -> p0?.cancel() }
                        builder.create().show()
                    }
                    else -> return@setOnMenuItemClickListener false
                }
            }
            true

        }


        recyclerViewSubject.setHasFixedSize(true)
        recyclerViewTopic.setHasFixedSize(true)
        recyclerViewLearnable.setHasFixedSize(true)

        recyclerViewSubject.layoutManager = LinearLayoutManager(this)
        recyclerViewTopic.layoutManager = LinearLayoutManager(this)
        recyclerViewTopic.itemAnimator?.apply {
            moveDuration = 50
            changeDuration = 50
        }
        recyclerViewLearnable.layoutManager = LinearLayoutManager(this)
        recyclerViewLearnable.itemAnimator?.apply {
            moveDuration = 50
            changeDuration = 50
        }

        recyclerViewTopic.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        recyclerViewLearnable.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )

        setupSubjectAdapter()

        addButton.setOnClickListener {
            when (currentState) {
                States.SUBJECT -> {
                    val intent = Intent(this, LearningAddSubjectActivity::class.java)
                    startActivity(intent)
                }
                States.TOPIC -> {
                    val intent = Intent(this, LearningAddTopicActivity::class.java)
                    intent.putExtra("SUBJECT", currentSubject)
                    startActivity(intent)
                }
                States.LEARNABLE -> {
                    val intent = Intent(this, LearningAddLearnableActivity::class.java)
                    intent.putExtra("SUBJECT", currentSubject)
                    intent.putExtra("TOPIC", currentTopic)
                    startActivity(intent)
                }
            }

        }

        itemTouchHelperTopic = ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {

            override fun onMove(
                recyclerView: RecyclerView,
                dragged: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {

                val positionDragged = dragged.adapterPosition
                val positionTarget = target.adapterPosition

                LearningManager.switchTopics(currentSubject, positionDragged, positionTarget)
                recyclerViewTopic.adapter?.notifyItemMoved(positionDragged, positionTarget)
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
        })
        itemTouchHelperTopic.attachToRecyclerView(recyclerViewTopic)


        itemTouchHelperLearnable = ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {

            override fun onMove(
                recyclerView: RecyclerView,
                dragged: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {

                val positionDragged = dragged.adapterPosition
                val positionTarget = target.adapterPosition

                LearningManager.switchLearnables(
                    currentSubject,
                    currentTopic,
                    positionDragged,
                    positionTarget
                )
                recyclerViewLearnable.adapter?.notifyItemMoved(positionDragged, positionTarget)
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
        })
        itemTouchHelperLearnable.attachToRecyclerView(recyclerViewLearnable)

        learnButton.setOnClickListener {
            if (currentState == States.LEARNABLE) {
                val intent = Intent(this, LearningTestActivity::class.java)
                intent.putExtra("SUBJECT", currentSubject)
                intent.putExtra("TOPIC_ARRAY", arrayListOf(currentTopic))
                startActivity(intent)
                return@setOnClickListener

            } else if (currentState == States.TOPIC) {
                if (topicAdapter.selectedViewHolders.size > 0) {
                    val intent = Intent(this, LearningTestActivity::class.java)
                    intent.putExtra("SUBJECT", currentSubject)
                    intent.putExtra("TOPIC_ARRAY", topicAdapter.getSelectionStrings())
                    startActivity(intent)
                }
                selectingTopics = !selectingTopics

            }
        }

        learnButton.setOnLongClickListener {
            when (currentState) {
                States.LEARNABLE -> {
                    val intent = Intent(this, LearnDuelActivity::class.java)
                    intent.putExtra("SUBJECT", currentSubject)
                    intent.putExtra("TOPIC_ARRAY", arrayListOf(currentTopic))
                    startActivity(intent)
                    true
                }
                States.TOPIC -> {
                    if (topicAdapter.getSelectionStrings().size > 0) {
                        val intent = Intent(this, LearnDuelActivity::class.java)
                        intent.putExtra("SUBJECT", currentSubject)
                        intent.putExtra("TOPIC_ARRAY", topicAdapter.getSelectionStrings())
                        startActivity(intent)
                        selectingTopics = false
                        return@setOnLongClickListener true
                    }
                    false
                }
                else -> false
            }
        }
    }

    private lateinit var itemTouchHelperLearnable: ItemTouchHelper
    private lateinit var itemTouchHelperTopic: ItemTouchHelper

    private var selectingTopics = false
        set(value) {
            if (value) {
                addButton.hide()
                Toast.makeText(this, R.string.select_topics_and_click_again, Toast.LENGTH_LONG)
                    .show()
                itemTouchHelperLearnable.attachToRecyclerView(null)
                itemTouchHelperTopic.attachToRecyclerView(null)

                setLearnButtonDrawable(R.drawable.ic_learn_go_anim)

            } else {
                topicAdapter.clearSelection()
                addButton.show()
                itemTouchHelperLearnable.attachToRecyclerView(recyclerViewLearnable)
                itemTouchHelperTopic.attachToRecyclerView(recyclerViewTopic)

                setLearnButtonDrawable(R.drawable.ic_go_learn_anim)
            }
            topicAdapter.selecting = value
            field = value
        }

    private fun setLearnButtonDrawable(drawableId: Int?) {
        if (drawableId == null) {
            //learnButton.setImageDrawable(null)
            println("drawableId is nul")
        } else {
            learnButton.setImageDrawable(getDrawable(drawableId).apply { setVisible(true) })
            //learnButton.hide()
            //learnButton.show()
            println("visible: ${getDrawable(drawableId)?.isVisible}")
            getDrawable(drawableId)?.setVisible(true, true)
            playAnimOnLearnButton()
        }
    }

    private fun playAnimOnLearnButton() {
        val drawable: Drawable = learnButton.drawable

        if (drawable is AnimatedVectorDrawableCompat) {
            drawable.start()
        } else if (drawable is AnimatedVectorDrawable) {
            drawable.start()
        }
    }

    override fun onResume() {
        super.onResume()
        when (currentState) {
            States.SUBJECT -> setupSubjectAdapter()
            States.TOPIC -> setupTopicAdapter(currentSubject)
            States.LEARNABLE -> setupLearnableAdapter(currentTopic, currentSubject)
        }

    }

    private fun setupSubjectAdapter() {
        learnButton.hide()
        toolbar.menu.findItem(R.id.item_delete)?.isVisible = false
        currentState = States.SUBJECT

        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
        val color: Int = ContextCompat.getColor(this, R.color.colorPrimary)
        toolbar.background = ColorDrawable(color)
        val subjectArray = LearningManager.getLearnableSubjectArray()
        val adapter = SubjectItemAdapter(subjectArray)
        recyclerViewSubject.adapter = adapter

        toolbar.title = getString(R.string.drawer_learning)


        adapter.setOnClickListener(object : SubjectItemAdapter.OnItemClickListener {
            override fun onItemClick(
                position: Int,
                viewHolder: SubjectItemAdapter.SubjectItemViewHolder
            ) {
                setupTopicAdapter(subjectArray[position].subject)
                nextRecyclerView()
            }

        })
    }

    var currentSubject = -1

    private lateinit var topicAdapter: TextItemAdapter

    fun setupTopicAdapter(subject: Int) {
        learnButton.show()
        if (currentState == States.LEARNABLE)
            setLearnButtonDrawable(R.drawable.ic_go_learn_anim)

        toolbar.menu.findItem(R.id.item_delete)?.isVisible = true
        currentSubject = subject
        currentState = States.TOPIC

        val color: Int? = SubjectManager.getSubject(subject)?.color
        if (color != null) {
            val darkerColor: Int = Color.rgb(
                (Color.red(color) * 0.8).toInt(),
                (Color.green(color) * 0.8).toInt(),
                (Color.blue(color) * 0.8).toInt()
            )

            window.statusBarColor = darkerColor
            toolbar.background = ColorDrawable(color)
        }

        val topicArray = LearningManager.getTopicsOfSubjectAsTextItems(subject)
        val adapter = TextItemAdapter(topicArray)
        recyclerViewTopic.adapter = adapter
        topicAdapter = adapter

        toolbar.title = SubjectManager.getSubject(currentSubject)?.name

        adapter.setOnClickListener(object : TextItemAdapter.OnItemClickListener {
            override fun onItemClick(
                position: Int,
                viewHolder: TextItemAdapter.TextItemViewHolder
            ) {
                if (!adapter.selecting) {
                    setupLearnableAdapter(topicArray[position].text)
                    nextRecyclerView()
                }
            }
        })
    }

    var currentTopic = ""

    fun setupLearnableAdapter(topic: String, subject: Int? = null) {

        if (currentState == States.TOPIC)
            setLearnButtonDrawable(R.drawable.ic_learn_go_anim)

        toolbar.menu.findItem(R.id.item_delete)?.isVisible = true
        if (subject != null) {
            currentSubject = subject
        }

        currentTopic = topic
        currentState = States.LEARNABLE
        val learnablesArray = LearningManager.getLearnablesAsTextItems(currentSubject, currentTopic)
        val adapter = TextItemAdapter(learnablesArray)
        recyclerViewLearnable.adapter = adapter

        toolbar.title = SubjectManager.getSubject(currentSubject)?.name + " | " + currentTopic

        adapter.setOnClickListener(object : TextItemAdapter.OnItemClickListener {

            override fun onItemClick(
                position: Int,
                viewHolder: TextItemAdapter.TextItemViewHolder
            ) {
                val intent = Intent(this@LearningActivity, LearningAddLearnableActivity::class.java)
                intent.putExtra("SUBJECT", currentSubject)
                intent.putExtra("TOPIC", currentTopic)
                intent.putExtra("CHANGE_INDEX", position)
                startActivity(intent)
            }

        })
    }

    override fun onBackPressed() {
        when (currentState) {
            States.LEARNABLE -> {
                setupTopicAdapter(currentSubject)
                previousRecyclerView()
            }
            States.TOPIC -> {
                if (selectingTopics) {
                    selectingTopics = false
                } else {
                    setupSubjectAdapter()
                    previousRecyclerView()
                }
            }
            States.SUBJECT -> super.onBackPressed()
        }
    }

    fun nextRecyclerView() {
        view_flipper.setInAnimation(this, R.anim.slide_in_right)
        view_flipper.setOutAnimation(this, R.anim.slide_out_left)
        view_flipper.showNext()
    }

    private fun previousRecyclerView() {
        view_flipper.setInAnimation(this, R.anim.slide_in_left)
        view_flipper.setOutAnimation(this, R.anim.slide_out_right)
        view_flipper.showPrevious()
    }
}
