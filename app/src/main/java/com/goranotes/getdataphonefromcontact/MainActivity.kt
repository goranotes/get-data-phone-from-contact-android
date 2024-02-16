package com.goranotes.getdataphonefromcontact

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.goranotes.getdataphonefromcontact.databinding.ActivityMainBinding
import com.goranotes.gorapermission.Permission

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val resultPermission = Permission.handlePermissionsResult(permissions, grantResults, this)
        if(resultPermission.permissionGranted) {
            when (resultPermission.permissionType) {
                Permission.PermissionType.READ_CONTACTS -> {
                    try {
                        val intent  = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
                        activityResultLauncher.launch(intent)
                    }catch (e: Exception){
                        Toast.makeText(this, e.printStackTrace().toString(), Toast.LENGTH_LONG).show()
                    }
                }

                else -> {}
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                getDataContactPhone(result.data)
            }
        }

        binding.ivContactPhone.setOnClickListener {

            if(Permission.PermissionGrant(this, Permission.PermissionType.READ_CONTACTS)){
                val intent  = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
                activityResultLauncher.launch(intent)
            }else{
                Permission.ReqPermission(this, Permission.PermissionType.READ_CONTACTS)
            }
        }

    }

    private fun getDataContactPhone(data: Intent?){

        val contactData = data?.data
        contactData?.let {uri->

            val queryFields = arrayOf(ContactsContract.Data.CONTACT_ID, ContactsContract.Contacts.DISPLAY_NAME)
            val cr = this.contentResolver

            val c = cr?.query(
                uri,
                queryFields,
                null,
                null,
                null
            )

            c?.let { cursor ->
                if (cursor.moveToFirst()) {

                    val contactId       = cursor.getString(0)
                    val contactName     = cursor.getString(1)
                    val contactNumber   = getNumberFromId(contactId, cr)

                    binding.tvNameValue.setText(contactName)
                    binding.tvPhoneValue.setText(contactNumber)
                }
                c.close()
            }
        }
    }

    private fun getNumberFromId(id: String, cr: ContentResolver): String? {

        val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
        val cursor = cr.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
            arrayOf(id.toString()),
            null
        )

        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                if (columnIndex != -1) {
                    return it.getString(columnIndex)
                } else {
                    return null
                }
            }
        }
        return null
    }
}