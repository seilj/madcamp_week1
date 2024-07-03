package com.example.myapplication

import android.net.Uri

object ImageResources {
    val images = mutableListOf<Uri>()
    val texts = mutableListOf<String>()

    fun addImage(imageUri: Uri, text: String) {
        images.add(imageUri)
        texts.add(text)
    }

    fun removeImage(imageuri: Uri, text: String){
        val index = images.indexOf(imageuri)
        if (index != -1) {
            images.removeAt(index)
            texts.remove(text)
        }
    }

    fun clearImages() {
        images.clear()
        texts.clear()
    }
}
