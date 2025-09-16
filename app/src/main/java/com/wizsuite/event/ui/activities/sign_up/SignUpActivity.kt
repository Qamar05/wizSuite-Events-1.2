package com.wizsuite.event.ui.activities.sign_up

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.OpenableColumns
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.wizsuite.event.R
import com.wizsuite.event.api.ApiInterface
import com.wizsuite.event.api.ApiUtility
import com.wizsuite.event.databinding.ActivitySignUpBinding
import com.wizsuite.event.model.RegistrationResponse
import com.wizsuite.event.ui.activities.login.LoginActivity
import com.wizsuite.event.utils.AppUtils
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

class SignUpActivity : AppCompatActivity() , View.OnClickListener {
    lateinit var binding: ActivitySignUpBinding
    lateinit var filePart: MultipartBody.Part
    lateinit var myFile: File
    private var pickedPdfFile: Uri? = null
    lateinit var tShirtSize: String
    lateinit var govtIdType: String
    var absolutePath: String? = null
    private val IMAGE_DIRECTORY = "/wizsuite_events"
    val TAG: String = "LOGIN"

    /*val tShirtSize = resources.getStringArray(R.array.t_shirt_size)
    val govtIds = resources.getStringArray(R.array.govt_id)
*/
    private val GALLERY = 1
    private val CAMERA = 2

    private val BUFFER_SIZE = 1024 * 2

    var imageDataList: List<ByteArray> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setOnClickListener()
        setTShirtSpinner()
        setGovtIdSpinner()
        binding.uploadFile.setOnClickListener {
            val galleryIntent = Intent()
            galleryIntent.action = Intent.ACTION_GET_CONTENT

            // We will be redirected to choose pdf
            galleryIntent.type = "application/pdf"
            // startActivityForResult(galleryIntent, 1)
            resultLauncher.launch(galleryIntent)
        }
    }

    private fun setTShirtSpinner() {
        if (binding.edtTShirtSize != null) {
            ArrayAdapter.createFromResource(
                this,
                R.array.t_shirt_size,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // Apply the adapter to the spinner
                binding.edtTShirtSize.adapter = adapter
            }

            binding.edtTShirtSize.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long
                ) {
                    tShirtSize = parent.getItemAtPosition(position) as String
                    Toast.makeText(
                        this@SignUpActivity,
                        " Selected Item : " +
                                " " + parent.getItemAtPosition(position), Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }

    }

    private fun setGovtIdSpinner() {
        if (binding.edtGovtId != null) {
            ArrayAdapter.createFromResource(
                this,
                R.array.govt_id,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                // Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // Apply the adapter to the spinner
                binding.edtGovtId.adapter = adapter
            }


            binding.edtGovtId.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long
                ) {

                    govtIdType = parent.getItemAtPosition(position) as String
                    Toast.makeText(
                        this@SignUpActivity,
                        " Selected Item : " +
                                " " + parent.getItemAtPosition(position), Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }

        }
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                pickedPdfFile = data?.data
                myFile = File(pickedPdfFile.toString())
                Log.d(TAG, "Pdf : $pickedPdfFile")
                /*   val count: Int? = data?.clipData?.itemCount
                   val mimeType: String? = data?.clipData?.description?.getMimeType(0)


                   Log.d(TAG, "File Length : $count and File MimeType : $mimeType")
   */
                if (pickedPdfFile.toString().startsWith("content://")) {
                    var cursor: Cursor? = null
                    try {
                        cursor =
                            applicationContext.contentResolver
                                .query(pickedPdfFile!!, null, null, null, null)
                        if (cursor != null && cursor.moveToFirst()) {
                            var displayName =
                                cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                            Log.d(TAG, "Display Name of File : $displayName")
                            binding.uploadFile.text = displayName

                            absolutePath = getFilePathFromURI(this, pickedPdfFile, displayName)
                            Log.d(TAG, "Absolute Path : $absolutePath")
                        }
                    } finally {
                        cursor?.close()
                    }
                }
            }

        }

    private fun setOnClickListener() {
        binding.txtSignUp.setOnClickListener(this)
        binding.txtSignIn.setOnClickListener(this)
        binding.txtTermsAndConditions.setOnClickListener(this)
        binding.txtPrivacyPolicy.setOnClickListener(this)
        Log.d(TAG, "Set OnClickListener")
    }

    var dialog: ProgressDialog? = null


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.txtSignUp -> {
                Log.d(TAG, "In R.id.SignUp")
                if (validateForm()) {

                    callSignUpAPI(tShirtSize, govtIdType)
                }
            }
            R.id.txtSignIn -> {
                Log.d(TAG, "In R.id.SignIn")
                startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
                finish()
            }
            R.id.txtTermsAndConditions -> {
                Log.d(TAG, "In R.id.SignIn")
                AppUtils.Companion.openBrowser(this, AppUtils.Companion.TERMS_AND_CONDITIONS)
                /*startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
                finish()*/
            }
            R.id.txtPrivacyPolicy -> {
                Log.d(TAG, "In R.id.SignIn")
                AppUtils.Companion.openBrowser(this, AppUtils.Companion.PRIVACY_POLICY)
                /*startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
                finish()*/
            }

            else -> Log.d(TAG, "In Else Part")
        }
    }

    private fun callSignUpAPI(tShirtSize: String, govtIdType: String) {
        binding.loading.visibility = View.VISIBLE
        val name: RequestBody =
            RequestBody.Companion.create(
                "multipart/form-data".toMediaTypeOrNull(),
                binding.edtName.text.toString().trim()
            )
        val email: RequestBody =
            RequestBody.Companion.create(
                "multipart/form-data".toMediaTypeOrNull(),
                binding.edtEmail.text.toString().trim()
            )
        val phone: RequestBody =
            RequestBody.Companion.create(
                "multipart/form-data".toMediaTypeOrNull(),
                binding.edtPhone.text.toString().trim()
            )
        val password: RequestBody =
            RequestBody.Companion.create(
                "multipart/form-data".toMediaTypeOrNull(),
                binding.edtConfirmPassword.text.toString().trim()
            )
        val city: RequestBody = RequestBody.Companion.create(
            "multipart/form-data".toMediaTypeOrNull(),
            binding.edtCity.text.toString().trim()
        )
        val tShirtSize: RequestBody =
            RequestBody.Companion.create("multipart/form-data".toMediaTypeOrNull(), tShirtSize)
        val govtIdType: RequestBody =
            RequestBody.Companion.create("multipart/form-data".toMediaTypeOrNull(), govtIdType)

        val signUpMap: java.util.HashMap<String, RequestBody> = HashMap()
        signUpMap["name"] = name
        signUpMap["phone"] = phone
        signUpMap["email"] = email
        signUpMap["password"] = password
        signUpMap["city"] = city
        signUpMap["t_shirt"] = tShirtSize
        signUpMap["govermt_id_type"] = govtIdType

        Log.d(TAG, "File Name : ${myFile.name}")
        val filePart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "govermt", myFile.name, RequestBody.Companion.create(
                "*/*".toMediaTypeOrNull(), absolutePath.toString()
            ) //URLConnection.guessContentTypeFromName(myFile.name)
        )


        val apiService = ApiUtility.getInstance().create(ApiInterface::class.java)
        apiService.registerUserRequest(filePart, signUpMap)
            .enqueue(object : Callback<RegistrationResponse> {
                override fun onResponse(
                    call: Call<RegistrationResponse>,
                    response: Response<RegistrationResponse>
                ) {

                    if (response.code() == 200) {
                        binding.loading.visibility = View.INVISIBLE
                        Log.i(TAG, "post submitted to API : " + response.body()!!)
                        Toast.makeText(
                            this@SignUpActivity,
                            response.body()!!.message,
                            Toast.LENGTH_LONG
                        ).show()
                        startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
                        finish()
                    } else {
                        binding.loading.visibility = View.INVISIBLE
                        Log.d(TAG, "error Response : ${response.errorBody()!!.string()}")
                        val jObjError = JSONObject(response.errorBody()!!.string())
                        val errorMessage = jObjError.getString("message")
                        Toast.makeText(
                            this@SignUpActivity,
                            errorMessage,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<RegistrationResponse>, t: Throwable) {
                    Log.i(TAG, "post submitted to API Failure : " + t.message)
                    binding.loading.visibility = View.INVISIBLE
                }
            })
    }

    private fun validateForm(): Boolean {
        if (binding.edtName.text.isEmpty()) {
            Toast.makeText(this@SignUpActivity, R.string.empty_name, Toast.LENGTH_LONG).show()
            return false
        } else if (binding.edtPhone.text.isEmpty()) {
            Toast.makeText(this@SignUpActivity, R.string.empty_phone, Toast.LENGTH_LONG).show()
            return false
        } else if (binding.edtPhone.text.toString().trim().length < 10) {
            Toast.makeText(this@SignUpActivity, R.string.empty_phone, Toast.LENGTH_LONG).show()
            return false
        } else if (binding.edtEmail.text.isEmpty()) {
            Toast.makeText(this@SignUpActivity, R.string.empty_email, Toast.LENGTH_LONG).show()
            return false
        } else if (binding.edtPassword.text.isEmpty()) {
            Toast.makeText(this@SignUpActivity, R.string.empty_password, Toast.LENGTH_LONG).show()
            return false
        } else if (binding.edtPassword.text.toString().trim().length < 6) {
            Toast.makeText(this@SignUpActivity, R.string.password_length, Toast.LENGTH_LONG).show()
            return false
        } else if (binding.edtPassword.text.toString()
                .trim() != binding.edtConfirmPassword.text.toString().trim()
        ) {
            Toast.makeText(
                this@SignUpActivity,
                R.string.confirm_password_not_match,
                Toast.LENGTH_LONG
            ).show()
            return false
        } else if (binding.edtCity.text.isEmpty()) {
            Toast.makeText(this@SignUpActivity, R.string.empty_city, Toast.LENGTH_LONG).show()
            return false
        }else if (!binding.chkBxkTermAndConditions.isChecked) {
            Toast.makeText(this@SignUpActivity, R.string.agree_to_t_and_c, Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }


    fun getFilePathFromURI(context: Context?, contentUri: Uri?, displayName: String): String? {
        //copy file and send new file path
        val fileName: String? = getFileName(contentUri)
        val wallpaperDirectory: File = File(
            Environment.getExternalStorageDirectory(), IMAGE_DIRECTORY
        )
        /* val wallpaperDirectory: File = File(
             context?.filesDir,  "MyFile.pdf"
         )*/
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs()
        }
        if (!TextUtils.isEmpty(fileName)) {
            // val copyFile = File(wallpaperDirectory.toString() + File.separator + fileName)
            val copyFile = File(wallpaperDirectory.toString() + File.separator + displayName)
            // create folder if not exists
            copy(context!!, contentUri, copyFile)
            return copyFile.absolutePath
        }
        return null
    }

    fun getFileName(uri: Uri?): String? {
        if (uri == null) return null
        var fileName: String? = null
        val path = uri.path
        val cut = path!!.lastIndexOf('/')
        if (cut != -1) {
            fileName = path.substring(cut + 1)
        }
        return fileName
    }

    fun copy(context: Context, srcUri: Uri?, dstFile: File?) {
        try {
            val inputStream = context.contentResolver.openInputStream(srcUri!!)
                ?: return
            val outputStream: OutputStream = FileOutputStream(dstFile)
            copystream(inputStream, outputStream)
            inputStream.close()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Throws(java.lang.Exception::class, IOException::class)
    fun copystream(input: InputStream?, output: OutputStream?): Int {
        val buffer = ByteArray(BUFFER_SIZE)
        val `in` = BufferedInputStream(input, BUFFER_SIZE)
        val out = BufferedOutputStream(output, BUFFER_SIZE)
        var count = 0
        var n = 0
        try {
            while (`in`.read(buffer, 0, BUFFER_SIZE).also { n = it } != -1) {
                out.write(buffer, 0, n)
                count += n
            }
            out.flush()
        } finally {
            try {
                out.close()
            } catch (e: IOException) {
                Log.e(e.message, e.toString())
            }
            try {
                `in`.close()
            } catch (e: IOException) {
                Log.e(e.message, e.toString())
            }
        }
        return count
    }

    fun getBytes(uri: Uri?): ByteArray? {
        try {
            val inputStream: InputStream? =
                applicationContext.contentResolver.openInputStream(uri!!)
            return readBytes(inputStream!!)
        } catch (exception: Exception) {
            Log.d(TAG, exception.stackTraceToString())

        }
        return null
    }

    @Throws(IOException::class)
    private fun readBytes(inputStream: InputStream): ByteArray? {
        // this dynamically extends to take the bytes you read
        val byteBuffer = ByteArrayOutputStream()

        // this is storage overwritten on each iteration with bytes
        val bufferSize = 1024
        val buffer = ByteArray(bufferSize)

        // we need to know how may bytes were read to write them to the byteBuffer
        var len = 0
        while (inputStream.read(buffer).also { len = it } != -1) {
            byteBuffer.write(buffer, 0, len)
        }

        // and then we can return your byte array.
        return byteBuffer.toByteArray()
    }

    private fun handleAttachment(imageDataList: List<ByteArray>, mimeType: String) {
        val parts: MutableList<MultipartBody.Part?> = ArrayList()
        for (i in imageDataList.indices) {
            var body: MultipartBody.Part? = null
            when (mimeType) {
                /* MimeTypeConst.imageMimeTypeInRequest -> {
                     val requestFile = RequestBody.create(
                         MediaType.parse(MimeTypeConst.imageMimeType),
                         imageDataList[i]
                     )
                     val fileName = "attachment$i.jpg"
                     body = MultipartBody.Part.createFormData("image", fileName, requestFile)
                 }*/
                "application/pdf" -> {
                    val requestFile = RequestBody.Companion.create(
                        "application/pdf"
                            .toMediaTypeOrNull(),
                        imageDataList[i]
                    )
                    val fileName = "attachment$i.pdf"
                    body = MultipartBody.Part.createFormData("image", fileName, requestFile)
                }
            }
            parts.add(body)
        }
        // uploadAttachment(parts)
    }
}