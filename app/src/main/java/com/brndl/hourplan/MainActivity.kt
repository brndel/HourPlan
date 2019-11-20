package com.brndl.hourplan

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.brndl.hourplan.learning.LearningActivity
import com.brndl.hourplan.learning.LearningManager
import com.brndl.hourplan.subjects.SubjectManager
import com.brndl.hourplan.subjects.SubjectManagerActivity
import com.brndl.hourplan.timetable.TimetableFragment
import com.brndl.hourplan.timetable.TimetableManager
import com.google.firebase.FirebaseApp
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var timetableFragment: TimetableFragment

    private lateinit var optionsMenu: Menu

    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sharedPrefs = getSharedPreferences("SHARED_PREFS", Context.MODE_PRIVATE)
        SubjectManager.sharedPrefs = sharedPrefs
        SubjectManager.loadData()
        TimetableManager.sharedPrefs = sharedPrefs
        TimetableManager.loadData()
        LearningManager.sharedPrefs = sharedPrefs
        LearningManager.loadData()


        SubstitutionManger.init()

        FirebaseApp.initializeApp(this)

        setSupportActionBar(toolbar)

        toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        if (savedInstanceState  == null) {
            supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container,
                HomeFragment(this)
            ).commit()
            nav_view.setCheckedItem(R.id.nav_home)
        }

        nav_view.setNavigationItemSelectedListener{

            when(it.itemId) {
                R.id.nav_home -> {
                    goToHome()
                }
                R.id.nav_timetable -> {
                    goToTimetable()
                }
                R.id.nav_substitution -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.fragment_container,
                        SubstitutionFragment()
                    ).commit()
                    optionsMenu.findItem(R.id.item_edit)?.isVisible = false
                }
                R.id.nav_subject_manager -> {
                    val intent = Intent(this, SubjectManagerActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_learning -> {
                    val intent = Intent(this, LearningActivity::class.java)
                    startActivity(intent)
                }
            }
            drawer_layout.closeDrawer(GravityCompat.START)
            true
        }

    }

    fun goToHome() {
        supportFragmentManager.beginTransaction().replace(
            R.id.fragment_container,
            HomeFragment(this)
        ).commit()
        optionsMenu.findItem(R.id.item_edit)?.isVisible = false
        nav_view.menu.getItem(0).isChecked = true
    }


    fun goToTimetable(day: Int = TimetableManager.indexOfDay(Calendar.getInstance().get(Calendar.DAY_OF_WEEK))) {
        timetableFragment = TimetableFragment(day)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, timetableFragment).commit()
        optionsMenu.findItem(R.id.item_edit)?.isVisible = true
        nav_view.menu.getItem(1).isChecked = true
    }

    fun goToSubstitution() {
        supportFragmentManager.beginTransaction().replace(
            R.id.fragment_container,
            SubstitutionFragment()
        ).commit()
        optionsMenu.findItem(R.id.item_edit)?.isVisible = false
        nav_view.menu.getItem(0).isChecked = true
    }


    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else if (!nav_view.menu.getItem(0).isChecked) {
            goToHome()
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (menu != null){
            optionsMenu = menu
        }
        menuInflater.inflate(R.menu.edit_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        timetableFragment.onOptionMenuItemSelected(item)
        return super.onOptionsItemSelected(item)
    }
}
