package com.example.myapplication

import android.net.Uri

object ImageResources {
    val images = mutableListOf<Uri>()
    val texts = mutableListOf<String>()

    fun addImage(imageUri: Uri, text: String) {
        images.add(imageUri)
        texts.add(text)
    }
}
