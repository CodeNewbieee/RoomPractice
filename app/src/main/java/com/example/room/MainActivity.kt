package com.example.room

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.room.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    lateinit var myDao : MyDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        with(binding) {
            myDao = MyDatabase.getDatabase(this@MainActivity).getMyDao()

            val allStudents = myDao.getAllStudents()
            allStudents.observe(this@MainActivity) {
                val str = StringBuilder().apply {
                    for ((id, name) in it) {
                        append(id)
                        append(" ")
                        append("-")
                        append(" ")
                        append(name)
                        append("\n")
                    }
                }.toString()
                textStudentList.text = str
            }

            addStudent.setOnClickListener {
                val id = editStudentId.text.toString().toInt()
                val name = editStudentName.text.toString()
                if(id>0 && name.isNotEmpty()) {
                    CoroutineScope(Dispatchers.IO).launch {
                        myDao.insertStudent(Student(id, name))
                    }
                }
                editStudentId.text = null
                editStudentName.text = null
            }
            queryStudent.setOnClickListener {
                val name  = editStudentName.text.toString()
                CoroutineScope(Dispatchers.IO).launch {

                    val results = myDao.getStudentByName(name)

                    if(results.isNotEmpty()) {
                        val str = StringBuilder().apply {
                            results.forEach { student ->
                                append(student.id)
                                append(" ")
                                append("-")
                                append(" ")
                                append(student.name)
                                append("\n")
                            }
                        }
                        withContext(Dispatchers.Main) {
                            textQueryStudent.text = str
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            textQueryStudent.text = ""
                        }
                    }
                }
            }
        }
    }
}