package com.example.myapp



import com.example.myapp.Contact

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class ContactsAdapter(private val contactList: List<Contact>, private val activity: ContactsActivity) : RecyclerView.Adapter<ContactsAdapter.ContactViewHolder>() {

    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contactName: TextView = itemView.findViewById(R.id.contact_name)
        val contactPhone: TextView = itemView.findViewById(R.id.contact_phone)
        val callButton: Button = itemView.findViewById(R.id.call_button)
       // val messageButton: Button = itemView.findViewById(R.id.message_button)
        //val deleteButton: Button = itemView.findViewById(R.id.delete_button)
        val selectCheckBox: CheckBox = itemView.findViewById(R.id.contact_checkbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.contacts_item, parent, false)
        return ContactViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contactList[position]
        holder.contactName.text = contact.name
        holder.contactPhone.text = contact.phoneNumber


        holder.selectCheckBox.isChecked = contact.isSelected
        holder.selectCheckBox.setOnCheckedChangeListener { _, isChecked ->
            contact.isSelected = isChecked
        }




        holder.callButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:${contact.phoneNumber}"))
            if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED) {
                activity.startActivity(intent)
            } else {
                ActivityCompat.requestPermissions(activity,
                    arrayOf(android.Manifest.permission.CALL_PHONE),
                    ContactsActivity.REQUEST_CODE_CALL_PHONE)
            }
        }


    }

    override fun getItemCount() = contactList.size
}
