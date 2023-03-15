package com.example.firstsql

import android.content.ContentValues
import android.content.DialogInterface
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.SimpleCursorAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.firstsql.databinding.ActivityMainBinding

private lateinit var binding: ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var db : SQLiteDatabase
    lateinit var rs : Cursor
    lateinit var adapter : SimpleCursorAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var helper = MyHelper(applicationContext)
        db = helper.readableDatabase
        rs = db.rawQuery("SELECT * FROM ACTABLE ORDER BY NAME",null)

        registerForContextMenu(binding.listviewi)

        binding.btnShowfirst.setOnClickListener {
            if(rs.moveToFirst()){
                binding.etName.setText(rs.getString(1))
                binding.etAge.setText(rs.getString(2))
            }else
                Toast.makeText(applicationContext,"No data further",Toast.LENGTH_SHORT).show()
        }

        binding.btnNext.setOnClickListener {
            if(rs.moveToNext()){
                binding.etName.setText(rs.getString(1))
                binding.etAge.setText(rs.getString(2))
            }else
                Toast.makeText(applicationContext,"No data futher",Toast.LENGTH_SHORT).show()
        }

        binding.btnInsert.setOnClickListener {
            var cv = ContentValues()
            cv.put("NAME", binding.etName.text.toString())
            cv.put("MEANING", binding.etAge.text.toString())
            db.insert("ACTABLE",null,cv)
            rs.requery()

            var ad = AlertDialog.Builder(this)
            ad.setTitle("Record Added sucessfully!")
            ad.setMessage("Record inserted in table sucessfully")
            ad.setPositiveButton("OK",DialogInterface.OnClickListener{ dialogInterface, i ->
                binding.etName.setText("")
                binding.etAge.setText("")
                binding.etName.requestFocus()
            })
            ad.show()
        }

        binding.btnUpdate.setOnClickListener {
            var cv = ContentValues()
            cv.put("NAME", binding.etName.text.toString())
            cv.put("MEANING", binding.etAge.text.toString())
            db.update("ACTABLE",cv,"_id = ?",arrayOf(rs.getString(0)))
            rs.requery()

            var aj = AlertDialog.Builder(this)
            aj.setTitle("Update status")
            aj.setMessage("Entry updated sucessfully")
            aj.setPositiveButton("OK",DialogInterface.OnClickListener { dialogInterface, i ->
                if(rs.moveToFirst()){
                    binding.etName.setText(rs.getString(1))
                    binding.etAge.setText(rs.getString(2))
                }
            })
            aj.show()
        }

        binding.btnDelete.setOnClickListener {
            db.delete("ACTABLE","_id = ?", arrayOf(rs.getString(0)))
            rs.requery()
            var ad = AlertDialog.Builder(this)
            ad.setTitle("Record deleted")
            ad.setMessage("Record is deleted successfully!")
            ad.setPositiveButton("OK",DialogInterface.OnClickListener { dialogInterface, i ->
                if(rs.moveToFirst()){
                    binding.etName.setText(rs.getString(1))
                    binding.etAge.setText(rs.getString(2))
                }else{
                    binding.etName.setText("No data found")
                    binding.etAge.setText("No data found")
                    Toast.makeText(applicationContext,"No data entered and found",Toast.LENGTH_SHORT).show()
                }
            })
            ad.show()
        }

        adapter = SimpleCursorAdapter(applicationContext,android.R.layout.simple_expandable_list_item_2,rs,
                        arrayOf("NAME","MEANING"),
                        intArrayOf(android.R.id.text1, android.R.id.text2),0)
        binding.listviewi.adapter = adapter

        binding.btnViewall.setOnClickListener {
            adapter.notifyDataSetChanged()
            binding.searchView3.isIconified = false
            binding.searchView3.queryHint = "Search Among ${rs.count} records"
            binding.searchView3.visibility = View.VISIBLE
            binding.listviewi.visibility = View.VISIBLE
        }
        binding.searchView3.setOnQueryTextListener(object:SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                rs = db.rawQuery("SELECT * FROM ACTABLE WHERE NAME LIKE '%${p0}%' OR MEANING LIKE'%${p0}%'",null)
                adapter.changeCursor(rs)
                return false
            }
        })
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu?.add(101,11,1,"DELETE")
        menu?.setHeaderTitle("Removing Data")
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (item.itemId == 11) {
            db.delete("ACTABLE","_id = ?", arrayOf(rs.getString(0)))
            rs.requery()
            adapter.notifyDataSetChanged()
            binding.searchView3.queryHint = "Search Among ${rs.count} records"

        }
        return super.onContextItemSelected(item)
    }
}