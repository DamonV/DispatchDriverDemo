package com.driver.reliantdispatch.data.glide

import android.os.Parcel
import android.os.Parcelable

data class SizeGlide(
    val width: Int,
    val height: Int
) : Parcelable {
    companion object {
        @JvmField val CREATOR: Parcelable.Creator<SizeGlide> = object : Parcelable.Creator<SizeGlide> {
            override fun createFromParcel(`in`: Parcel): SizeGlide {
                return SizeGlide(`in`)
            }

            override fun newArray(size: Int): Array<SizeGlide?> {
                return arrayOfNulls(size)
            }
        }
    }

    private constructor(parcelIn: Parcel) : this(parcelIn.readInt(), parcelIn.readInt())

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(width)
        dest.writeInt(height)
    }

    override fun describeContents() = 0

    override fun toString(): String = "$width x $height"
}