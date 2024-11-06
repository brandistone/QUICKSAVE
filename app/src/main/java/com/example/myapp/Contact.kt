package com.example.myapp

import android.os.Parcel
import android.os.Parcelable

data class Contact(
    val name: String,
    val phoneNumber: String,
    var isSelected: Boolean = false // Add this property for checkbox selection
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte() // Correctly read isSelected from the parcel
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(phoneNumber)
        parcel.writeByte(if (isSelected) 1 else 0) // Correctly write isSelected to the parcel
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Contact> {
        override fun createFromParcel(parcel: Parcel): Contact {
            return Contact(parcel)
        }

        override fun newArray(size: Int): Array<Contact?> {
            return arrayOfNulls(size)
        }
    }
}
