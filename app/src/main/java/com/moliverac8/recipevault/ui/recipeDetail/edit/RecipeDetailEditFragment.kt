package com.moliverac8.recipevault.ui.recipeDetail.edit

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.moliverac8.recipevault.PERMISSION
import com.moliverac8.recipevault.R
import com.moliverac8.recipevault.databinding.FragmentRecipeDetailEditBinding
import com.moliverac8.recipevault.databinding.FragmentRecipeDetailEditBindingImpl
import com.moliverac8.recipevault.ui.common.Permissions

class RecipeDetailEditFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentRecipeDetailEditBinding.inflate(layoutInflater)

        val adapter = RecipeInstructionsEditAdapter()
        binding.instructions.adapter = adapter

        val list = mutableListOf<String>()
        list.add("Cocinar mucho")
        list.add("Seguir")

        adapter.submitList(list)

        binding.photoBtn.setOnClickListener {
            if (!Permissions.hasPermissions(requireContext())) {
                Permissions.requestPermissionsFragment(::requestPermissions)
            } else {
                //launch camera
                Log.d(PERMISSION, "Permisos concedidos up")
            }
        }

        binding.addBtn.setOnClickListener {
            Log.d("TEST", "Click")
            list.add("")
            adapter.submitList(list)
            adapter.notifyItemInserted(list.size - 1)
        }

        return binding.root
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
                //launch camera
                Log.d(PERMISSION, "Permisos concedidos")
            }
        }
    }
}