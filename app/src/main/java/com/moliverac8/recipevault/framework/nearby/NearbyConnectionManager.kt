package com.moliverac8.recipevault.framework.nearby

import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.collection.SimpleArrayMap
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.moliverac8.domain.Recipe
import com.moliverac8.domain.RecipeWithIng
import com.moliverac8.recipevault.GENERAL
import com.moliverac8.recipevault.R
import com.moliverac8.recipevault.SERVICE_ID
import com.moliverac8.recipevault.Strings
import com.moliverac8.recipevault.ui.recipeDetail.RecipeDetailVM
import java.io.*


class NearbyConnectionManager(
    private val context: Context,
    private val emitter: Boolean,
    private val viewModel: RecipeDetailVM
) {

    private val client = Nearby.getConnectionsClient(context)

    private inner class DataReceivedCallback : PayloadCallback() {
        private val incomingFilePayloads = SimpleArrayMap<Long, Payload>()
        private val completedFilePayloads = SimpleArrayMap<Long, Payload>()
        private var filePayloadFilename: String = ""

        override fun onPayloadReceived(endPointId: String, payload: Payload) {
            when (payload.type) {
                Payload.Type.BYTES -> {
                    val gson = Gson()
                    val recipe =
                        gson.fromJson(String(payload.asBytes()!!), RecipeWithIng::class.java)
                    filePayloadFilename = recipe.domainRecipe.image
                    viewModel.setRecipeReceivedWithNearby(recipe)
                    Log.d(GENERAL, recipe.toString())
                }
                Payload.Type.FILE -> {
                    incomingFilePayloads.put(payload.id, payload)
                }
            }
        }

        override fun onPayloadTransferUpdate(endPointId: String, update: PayloadTransferUpdate) {
            if (update.status == PayloadTransferUpdate.Status.SUCCESS) {
                val payload = incomingFilePayloads.remove(update.payloadId)
                completedFilePayloads.put(update.payloadId, payload)
                if (payload?.type == Payload.Type.FILE) {
                    val filePayload = completedFilePayloads[update.payloadId]
                    if (filePayload != null && filePayloadFilename != "") {
                        completedFilePayloads.remove(update.payloadId)
                        // Get the received file (which will be in the Downloads folder)
                        // Because of https://developer.android.com/preview/privacy/scoped-storage, we are not
                        // allowed to access filepaths from another process directly. Instead, we must open the
                        // uri using our ContentResolver.
                        val uri = filePayload.asFile()!!.asUri()
                        try {
                            // Copy the file to a new location.
                            val input: InputStream? = context.contentResolver.openInputStream(uri!!)
                            if (input != null) {
                                val storageDir: File? =
                                    context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                                val file = File(storageDir, filePayloadFilename.split("/").last())
                                copyStream(input, FileOutputStream(file))
                                viewModel.updateWithRecipeReceivedWithNearby()
                            }
                        } catch (e: IOException) {
                            Log.d(GENERAL, "error al guardar el fichero", e)
                        } finally {
                            // Delete the original file.
                            context.contentResolver.delete(uri!!, null, null)
                        }
                    }
                }
            }
        }
    }

    private inner class ConnectingProcessCallback : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endPointId: String, info: ConnectionInfo) {
            MaterialAlertDialogBuilder(context)
                .setTitle(Strings.get(R.string.accept_connection, info.endpointName))
                .setMessage(Strings.get(R.string.confirm_code, info.authenticationDigits))
                .setPositiveButton(Strings.get(R.string.accept)) { _: DialogInterface, _: Int ->
                    Nearby.getConnectionsClient(context)
                        .acceptConnection(endPointId, DataReceivedCallback())
                }
                .setNegativeButton(Strings.get(R.string.cancel)) { _: DialogInterface, _: Int ->
                    Nearby.getConnectionsClient(context).rejectConnection(endPointId)
                }
                .setIcon(R.drawable.outline_warning_24)
                .show()
        }

        override fun onConnectionResult(endPointId: String, result: ConnectionResolution) {
            when (result.status.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    if (emitter) {
                        val gson = Gson()
                        val rec = viewModel.recipeWithIng.value
                        rec?.let {
                            // Changing the id so the receiver saves it as a new recipe and not an updated one
                            val jsonRecipe = gson.toJson(
                                RecipeWithIng(
                                    Recipe(
                                        -1,
                                        rec.domainRecipe.name,
                                        rec.domainRecipe.timeToCook,
                                        rec.domainRecipe.dishType,
                                        rec.domainRecipe.dietType,
                                        rec.domainRecipe.instructions,
                                        rec.domainRecipe.image,
                                        rec.domainRecipe.description
                                    ), rec.ings
                                )
                            )
                            sendText(jsonRecipe, endPointId)
                            viewModel.recipeWithIng.value?.domainRecipe?.image?.let { uri ->
                                Log.d(GENERAL, "enviando foto")
                                sendImage(uri, endPointId)
                            }
                        }
                    }
                }
                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                    Log.d(GENERAL, "conexion rechazada")
                }
                ConnectionsStatusCodes.STATUS_ERROR -> {
                    Log.d(GENERAL, "DESCONEXION")
                }
            }
        }

        override fun onDisconnected(endPointId: String) {
            Log.d(GENERAL, "DESCONEXION OK")
        }

    }

    private fun copyStream(input: InputStream, out: OutputStream) {
        try {
            val buffer = ByteArray(1024)
            var read: Int
            while (input.read(buffer).also { read = it } != -1) {
                out.write(buffer, 0, read)
            }
            out.flush()
        } finally {
            input.close()
            out.close()
        }
    }

    private fun sendText(text: String, endPointId: String) {
        val payload = Payload.fromBytes(text.toByteArray())
        client.sendPayload(endPointId, payload)
    }

    private fun sendImage(uri: String, endPointId: String) {
        try {
            val fileDesc = context.contentResolver.openFileDescriptor(
                Uri.parse(uri),
                "r"
            )
            fileDesc?.let {
                val payload = Payload.fromFile(it)
                client.sendPayload(endPointId, payload).addOnFailureListener { e ->
                    Log.d(GENERAL, "FALL CON FOTO", e)
                }
            }
        } catch (e: FileNotFoundException) {
            Log.d(GENERAL, "File not found", e)
        }
    }

    fun startAdvertising() {
        val advertisingOptions =
            AdvertisingOptions.Builder().setStrategy(Strategy.P2P_POINT_TO_POINT).build()
        client.startAdvertising(
            android.os.Build.MODEL, SERVICE_ID, ConnectingProcessCallback(), advertisingOptions
        )
            .addOnSuccessListener {
                Log.d(GENERAL, "advertising...")
            }
            .addOnFailureListener {
                Log.d(GENERAL, "failure advertising...")
            }
    }

    fun startDiscovering() {
        val discoveryOptions =
            DiscoveryOptions.Builder().setStrategy(Strategy.P2P_POINT_TO_POINT).build()
        client
            .startDiscovery(SERVICE_ID, object : EndpointDiscoveryCallback() {
                override fun onEndpointFound(endPointId: String, info: DiscoveredEndpointInfo) {
                    client.requestConnection(android.os.Build.MODEL, endPointId, ConnectingProcessCallback())
                        .addOnSuccessListener {

                        }
                        .addOnFailureListener {
                        }
                }

                override fun onEndpointLost(endPointId: String) {
                    Log.d(GENERAL, "endpoint lost")
                }
            }, discoveryOptions)
            .addOnSuccessListener { Log.d(GENERAL, "discovering...") }
            .addOnFailureListener {
                Log.d(GENERAL, "failure discovering...")
            }
    }

    fun stopAdvertising() = client.stopAdvertising()
    fun stopDiscovering() = client.stopDiscovery()
}