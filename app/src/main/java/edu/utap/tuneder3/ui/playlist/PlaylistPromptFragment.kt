package edu.utap.tuneder3.ui.playlist

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import edu.utap.tuneder3.R

class PlaylistPromptFragment(private val onPlaylistCreated: (String) -> Unit) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_playlist, null)

        val playlistNameField: EditText = view.findViewById(R.id.playlistName)

        val builder = AlertDialog.Builder(requireActivity())
            .setTitle("Create New Playlist")
            .setView(view)
            .setPositiveButton("Go") { dialog, id ->
                val playlistName = playlistNameField.text.toString()
                if (playlistName.isNotEmpty()) {
                    onPlaylistCreated(playlistName)
                }
            }
            .setNegativeButton("Cancel") { dialog, id ->
                dialog.cancel()
            }
            .create()
        return builder
    }

}

