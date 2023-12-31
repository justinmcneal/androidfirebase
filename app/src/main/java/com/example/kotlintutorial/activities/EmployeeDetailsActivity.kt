package com.example.kotlintutorial.activities

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlintutorial.R
import com.example.kotlintutorial.models.EmployeeModel
import com.google.firebase.database.FirebaseDatabase

class EmployeeDetailsActivity : AppCompatActivity() {
    private lateinit var tvEmpId: TextView
    private lateinit var tvEmpName: TextView
    private lateinit var tvEmpAge: TextView
    private lateinit var tvEmpSalary: TextView
    private lateinit var btnUpdate: Button
    private lateinit var btnDelete: Button

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employee_details)

        initView()
        setValuesToViews()

        btnUpdate.setOnClickListener{
            openUpdateDialog(
            intent.getStringExtra("empId").toString(),
            intent.getStringExtra("empName").toString())
        }

        btnDelete.setOnClickListener{
            deleteRecord(
                intent.getStringExtra("empId").toString()
            )
        }
    }

    private fun deleteRecord(
        id: String){
        val dbRef = FirebaseDatabase.getInstance().getReference("Employees").child(id)
        val mTask = dbRef.removeValue()

        mTask.addOnSuccessListener {
            Toast.makeText( this, "Employee data deleted", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, FetchingActivity::class.java)
            finish()
            startActivity(intent)
        }.addOnFailureListener{ error ->
            Toast.makeText(this, "Delete Err ${error.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun initView() {}

    private fun setValuesToViews() {

        tvEmpId.text = intent.getStringExtra("empId")
        tvEmpName.text = intent.getStringExtra("empName")
        tvEmpAge.text = intent.getStringExtra("empAge")
        tvEmpSalary.text = intent.getStringExtra("empSalary")

    }

    private fun openUpdateDialog(
        empId: String,
        empName: String
    ){
        val mDialog = AlertDialog.Builder(this,)
        val inflater = layoutInflater
        val mdialogView = inflater.inflate(R.layout.update_dialog,null)

        mDialog.setView(mdialogView)

        val etEmpName = mdialogView.findViewById<EditText>(R.id.etEmpName)
        val etEmpAge = mdialogView.findViewById<EditText>(R.id.etEmpAge)
        val etEmpSalary = mdialogView.findViewById<EditText>(R.id.etEmpSalary)

        val btnUpdateData = mdialogView.findViewById<Button>(R.id.btnUpdateData)

        etEmpName.setText(intent.getStringExtra("empName").toString())
        etEmpAge.setText(intent.getStringExtra("empAge").toString())
        etEmpSalary.setText(intent.getStringExtra("empSalary").toString())

        mDialog.setTitle("Updating $empName Record")

        val alertDialog = mDialog.create()
        alertDialog.show()

        btnUpdateData.setOnClickListener{
            updateEmpData(
                empId,
                etEmpName.text.toString(),
                etEmpAge.text.toString(),
                etEmpSalary.text.toString()
            )

            Toast.makeText(applicationContext, "Employee Data Updated", Toast.LENGTH_LONG).show()

            //update setting data to textviews
            tvEmpName.text = etEmpName.text.toString()
            tvEmpAge.text = etEmpAge.text.toString()
            tvEmpSalary.text = etEmpSalary.text.toString()

            alertDialog.dismiss()
        }
    }

    private fun updateEmpData(
        id:String,
        name:String,
        age:String,
        salary:String
    ){
        val dbRef =FirebaseDatabase.getInstance().getReference("Employees").child(id)
        val empInfo = EmployeeModel(id, name, age, salary)
        dbRef.setValue(empInfo)
    }
}