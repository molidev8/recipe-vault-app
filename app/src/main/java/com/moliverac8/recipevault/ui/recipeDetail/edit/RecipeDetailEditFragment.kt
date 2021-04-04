package com.moliverac8.recipevault.ui.recipeDetail.edit

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.FileProvider
import androidx.core.view.children
import androidx.core.view.forEach
import androidx.core.view.forEachIndexed
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.moliverac8.domain.DietType
import com.moliverac8.domain.DishType
import com.moliverac8.domain.Recipe
import com.moliverac8.domain.RecipeWithIng
import com.moliverac8.recipevault.GENERAL
import com.moliverac8.recipevault.IO
import com.moliverac8.recipevault.PERMISSION
import com.moliverac8.recipevault.databinding.FragmentRecipeDetailEditBinding
import com.moliverac8.recipevault.ui.common.Permissions
import com.moliverac8.recipevault.ui.common.setImage
import com.moliverac8.recipevault.ui.common.toJsonInstructions
import com.moliverac8.recipevault.ui.common.toListOfInstructions
import com.moliverac8.recipevault.ui.recipeDetail.RecipeDetailVM
import com.moliverac8.recipevault.ui.recipeDetail.RecipePagerFragment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class RecipeDetailEditFragment : Fragment() {

    private val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var binding: FragmentRecipeDetailEditBinding
    private lateinit var recipe: RecipeWithIng
    private lateinit var instructions: MutableList<String>
    private val viewModel: RecipeDetailVM by viewModels(ownerProducer = { parentFragment as RecipePagerFragment })
    private lateinit var photoUri: Uri
    private val mapOfInstructions = mutableMapOf<Int, String>()
    private var nInstructions = 0
    private lateinit var adapter: RecipeInstructionsEditAdapter
    private lateinit var recipePhotoPath: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipeDetailEditBinding.inflate(layoutInflater)
        adapter = RecipeInstructionsEditAdapter()
        binding.instructions.adapter = adapter

        viewModel.recipeWithIng.observe(viewLifecycleOwner, { recipe ->
            this.recipe = recipe
            binding.setTitleEdit.setText(recipe.domainRecipe.name)
            binding.setDescriptionEdit.setText(recipe.domainRecipe.description)
            if (recipe.domainRecipe.timeToCook == 0) binding.setTimeToCookEdit.setText("")
            else binding.setTimeToCookEdit.setText(recipe.domainRecipe.timeToCook.toString())
            instructions =
                if (recipe.domainRecipe.instructions.isNotEmpty())
                    recipe.domainRecipe.instructions.toListOfInstructions().toMutableList()
                else mutableListOf()
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
            instructions.add("")
            mapOfInstructions[nInstructions] = ""
            nInstructions += 1
            adapter.submitList(instructions)
            adapter.notifyItemInserted(instructions.size - 1)
        }

        binding.saveBtn.setOnClickListener {
            saveRecipeInfo()
            findNavController().popBackStack()
        }

        return binding.root
    }

    private fun saveRecipeInfo() {
        // Compruebo si es una receta nueva o una edicion
        val type = mutableListOf<DishType>()
        val id = if (recipe.domainRecipe.id != -1) -1
        else recipe.domainRecipe.id

        // Recupero las instrucciones
        recoverInstructions()

        // Obtengo el cuando se quiere comer la receta
        binding.timeToEatChips.checkedChipIds.forEach {
            when (it) {
                binding.breakfastChip.id -> type.add(DishType.BREAKFAST)
                binding.mealChip.id -> type.add(DishType.MEAL)
                else -> type.add(DishType.DINNER)
            }
        }

        // Obtengo el tipo de dieta
        val diet = when (binding.dietChips.checkedChipId) {
            binding.regularChip.id -> DietType.REGULAR
            binding.veganChip.id -> DietType.VEGETARIAN
            else -> DietType.VEGAN
        }

        // Guardo en la base de datos
        viewModel.saveRecipe(
            Recipe(
                id,
                binding.setTitleEdit.text.toString(),
                binding.setTimeToCookEdit.text.toString().toInt(),
                type,
                diet,
                mapOfInstructions.values.toList().toJsonInstructions(),
                photoUri.toString(),
                binding.setDescriptionEdit.text.toString()
            )
        )
        viewModel.saveEverything()
    }

    private fun recoverInstructions() {
        binding.instructions.forEachIndexed { idx, view ->
            if (view is TextInputLayout) {
                Log.d(GENERAL, view.editText?.text.toString())
                mapOfInstructions[idx] = view.editText?.text.toString()
            }
        }
    }

    private fun launchCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: IOException) {
            Log.d(IO, "Error al lanzar la camara")
            null
        }
        photoUri = FileProvider.getUriForFile(
            requireContext(),
            "com.moliverac8.recipevault",
            photoFile!!
        )

        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    @Throws(IOException::class)
    @SuppressLint("SimpleDateFormat")
    private fun createImageFile(): File {
        val timestamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timestamp}_",
            ".jpg",
            storageDir
        ).apply {
            recipePhotoPath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Glide.with(this).load(photoUri).into(binding.photoBtn)
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
}