package com.moliverac8.recipevault.ui.recipeDetail.edit

import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.moliverac8.domain.RecipeWithIng
import com.moliverac8.recipevault.APP
import com.moliverac8.recipevault.PERMISSION
import com.moliverac8.recipevault.R
import com.moliverac8.recipevault.databinding.FragmentRecipeDetailEditBinding
import com.moliverac8.recipevault.databinding.FragmentRecipeDetailEditBindingImpl
import com.moliverac8.recipevault.ui.common.Permissions
import com.moliverac8.recipevault.ui.common.toListOfInstructions
import com.moliverac8.recipevault.ui.recipeDetail.RecipeDetailVM
import com.moliverac8.recipevault.ui.recipeDetail.instructions.RecipeInstructionsAdapter
import com.moliverac8.recipevault.ui.recipeDetail.RecipePagerFragment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class RecipeDetailEditFragment : Fragment() {

    private val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var binding: FragmentRecipeDetailEditBinding
    private lateinit var recipe: RecipeWithIng
    private lateinit var instructions: MutableList<String>
    private val viewModel: RecipeDetailVM by viewModels(ownerProducer = { parentFragment as RecipePagerFragment })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipeDetailEditBinding.inflate(layoutInflater)
        val adapter = RecipeInstructionsEditAdapter()
        binding.instructions.adapter = adapter

        viewModel.recipeWithIng.observe(viewLifecycleOwner, { recipe ->
            this.recipe = recipe
            instructions = recipe.domainRecipe.instructions.toListOfInstructions().toMutableList()
            adapter.submitList(instructions)
        })

        binding.photoBtn.setOnClickListener {
            if (!Permissions.hasPermissions(requireContext())) {
                Permissions.requestPermissionsFragment(::requestPermissions)
            } else {
                launchCamera()
                Log.d(PERMISSION, "Permisos concedidos up")
            }
        }

        binding.addBtn.setOnClickListener {
            Log.d("TEST", "Click")
            instructions.add("")
            adapter.submitList(instructions)
            adapter.notifyItemInserted(instructions.size - 1)
        }

        return binding.root
    }

    private fun launchCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            Log.d(APP, "No se pudo lanzar la camara")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            binding.photoBtn.setImageBitmap(imageBitmap)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == Permissions.PERMISSION_CODE) {
            if (grantResults.isEmpty() || (grantResults.any { it == PackageManager.PERMISSION_DENIED })) {
                Log.d(PERMISSION, "Permisos denegados")
            } else {
                launchCamera()
                Log.d(PERMISSION, "Permisos concedidos")
            }
        }
    }

    companion object {
        fun newInstance(): RecipeDetailEditFragment = RecipeDetailEditFragment()
    }

    // MOVER AL VIEWMODEL CUANDO LO PONGA
    /*fun saveToDevice() {
        val bitmap =
            BitmapFactory.decodeResource(context?.resources, R.drawable.sample_recipe_image)
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            // Directorio dentro de la app /files/Pictures/
            val picturesDirectory = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            if (picturesDirectory != null && !picturesDirectory.exists()) {
                picturesDirectory.mkdirs()
            }
            try {
                val file = File(picturesDirectory, "receta1.png")
                val fileOutput = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutput)
                Log.d("IO", Uri.fromFile(file).toString())

                fileOutput.flush()
                fileOutput.close()
            } catch (e: IOException) {
                Log.d("IO", "error guardando foto en < Q ${e.message}")
            }
        }
    }*/
}