package com.example.myapplication

import android.app.Activity
import android.app.ActivityOptions
import android.app.AlertDialog
import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.Fragment2Binding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.Date

data class ImageData(
    val path: String,
    val text: String
)

class Fragment2: Fragment() {
    private lateinit var binding: Fragment2Binding
    private var imageDatas = mutableListOf<ImageData>()
    //selectedImageUri에 디바이스에서 가져온 사진의 Uri가 담는 변수 기본적으로 null값이 할당되어 있어 null가 감지되면 toast 메시지를 출력한다.
    private var selectedImageUri: Uri? = null
    //이미지 선택을 실행
    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            selectedImageUri = it.data?.data  // 이미지 URI를 가져옵니다.
        }
    }
    private lateinit var currentPhotoPath: String

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            selectedImageUri?.let { uri ->
                // Photo captured, show dialog to enter text and add to grid view
                showAddImageDialog(binding.gridview.adapter as GridViewAdapter)
            }
        } else {
            Log.e("Fragment2", "Camera result not OK, resultCode: ${it.resultCode}")
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.CAMERA] == true &&
            permissions[Manifest.permission.READ_MEDIA_IMAGES] == true) {
            dispatchTakePictureIntent()
        } else {
            Toast.makeText(requireContext(), "Camera or storage permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = Fragment2Binding.inflate(inflater, container, false)

        ImageResources.clearImages()
        imageDatas = loadImagesFromJsonFile()
        imageDatas.forEach { imageData ->
            ImageResources.addImage(Uri.parse(imageData.path), imageData.text)
        }

        //사진들을 화면에 보여주기 위한 gridviewadapter이다
        val gridviewAdapter = GridViewAdapter(requireContext(), ImageResources.images, ImageResources.texts)

        //어뎁터 고정
        binding.gridview.adapter = gridviewAdapter

        binding.gridview.setOnItemLongClickListener { parent, view, position, id ->
            val imageUri = ImageResources.images[position]
            val imageName = ImageResources.texts[position]

            showDeleteConfirmationDialog(imageUri, imageName, gridviewAdapter)
            true
        }

        //고정한 +버튼 눌렷을 사
        binding.imageAddition.setOnClickListener {
            // 새로운 데이터 추가
            showAddImageDialog(gridviewAdapter)
        }
        binding.photoAddition.setOnClickListener(){
            requestPermissions()
        }
        //각 그리드뷰가 터치되면?
        binding.gridview.onItemClickListener = AdapterView.OnItemClickListener { _, view, position, _ ->
            //터치 이벤트 실행
            clickEvent(view, position)
        }
        return binding.root
    }
    //화면을 확대하는 클릭 이벤트
    private fun clickEvent(view: View, pos: Int){
        val intent = Intent(requireContext(), Fragment2ImageActivity::class.java)
        intent.putExtra("pos", pos)
        val opt = ActivityOptions.makeSceneTransitionAnimation(requireActivity(), view, "imgTrans")
        startActivity(intent, opt.toBundle())
    }
    //불러올 이미지를 선택하고, text를 지정해줘 앱에 보이게하는 dialog
    private fun showAddImageDialog(gridViewAdapter: GridViewAdapter) {
        //view라는 변수에 dialog_add_image.xml 파일을 담는 과정
        //layoutInflater를 통해 xml을 앱에서 사용할 수 있는 뷰 객체로 변환
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_add_image, null)

        //xml 파일에 있는 요소들을 각 요소의 id를 통해 접근한다
        val textInput = view.findViewById<EditText>(R.id.edit_text)
        val selectImageButton = view.findViewById<Button>(R.id.selectImage)
        //selectImageButton이 눌리면?
        selectImageButton.setOnClickListener {
            //사진가져오기
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            imagePickerLauncher.launch(photoPickerIntent)
        }
        //제목과 add cancel 버튼의 역할 지정
        AlertDialog.Builder(requireContext())
            .setTitle("Add New Image")
            .setView(view)
            .setPositiveButton("Add") { dialog, _ ->
                val text = textInput.text.toString()

                if (selectedImageUri != null && text.isNotEmpty()) {
                    val imageData = ImageData(selectedImageUri!!.toString(), text)
                    imageDatas.add(imageData)
                    saveImagesToJsonFile(imageDatas)
                    ImageResources.addImage(selectedImageUri!!, text)
                    gridViewAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(requireContext(), "Image or text is missing", Toast.LENGTH_LONG).show()
                }
                dialog.dismiss()
            }
            //cancel누르면 닫는 로직
            .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
            //위에 있는걸 creat하고 show해준다
            .create()
            .show()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestPermissions() {
        requestPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_MEDIA_IMAGES
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            val photoUri = createImageUri()
            if (photoUri != null) {
                // Pass URI to the camera intent
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                selectedImageUri = photoUri // Set selectedImageUri here
                cameraLauncher.launch(takePictureIntent)
            } else {
                Toast.makeText(requireContext(), "Unable to create image URI", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun createImageUri(): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "photo_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }
        return requireContext().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    }



    private val createWriteRequestLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Handle successful write request
        } else {
            Toast.makeText(requireContext(), "Write permission denied", Toast.LENGTH_SHORT).show()
        }
    }


    private fun showDeleteConfirmationDialog(imageUri: Uri, imageName: String, gridViewAdapter: GridViewAdapter) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Image")
            .setMessage("Are you sure you want to delete this image?")
            .setPositiveButton("Yes") { _, _ ->
                val imageData = imageDatas.find{it.path == imageUri.toString()}
                if(imageData != null){
                    imageDatas.remove(imageData)
                    saveImagesToJsonFile(imageDatas)
                    ImageResources.removeImage(imageUri, imageName)
                    gridViewAdapter.notifyDataSetChanged()
                }
                else{
                    Toast.makeText(requireContext(), "Image not found", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("No") { dialog, _ -> dialog.cancel() }
            .create()
            .show()
    }

    private fun getImagesJsonFile(): File {
        val file = File(requireContext().filesDir, "images.json")
        if (!file.exists()) {
            try {
                // 파일이 없으면 새로 생성합니다.
                file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return file
    }

    private fun saveImagesToJsonFile(images: List<ImageData>) {
        val gson = Gson()
        val json = gson.toJson(images)
        val file = getImagesJsonFile()
        FileWriter(file).use { writer ->
            writer.write(json)
        }
    }

    private fun loadImagesFromJsonFile(): MutableList<ImageData> {
        val file = getImagesJsonFile()
        if (!file.exists()) {
            return emptyList<ImageData>().toMutableList()
        }

        val gson = Gson()
        val type: Type = object : TypeToken<List<ImageData>>() {}.type
        return try {
            gson.fromJson<List<ImageData>?>(file.readText(), type).toMutableList()
        } catch (e: Exception) {
            emptyList<ImageData>().toMutableList()
        }
    }

}
