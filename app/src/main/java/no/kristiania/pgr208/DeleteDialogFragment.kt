package no.kristiania.pgr208

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.fragment_delete_dialog.view.*
import no.kristiania.pgr208.adapters.ImageAdapter
import no.kristiania.pgr208.adapters.SavedImagesAdapter

class DeleteDialogFragment() : DialogFragment() {
    private lateinit var db: DatabaseHandler
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        db = context?.let { DatabaseHandler(it) }!!
       val rootView: View = inflater.inflate(R.layout.fragment_delete_dialog, container, false)
        val id = this.arguments?.getInt("imageId")


        rootView.cancel_button.setOnClickListener {
            dismiss()
        }

        rootView.delete_button.setOnClickListener {
//        Delete image from saved images table. If the function returns false, we know that it is the original image
//        and can instead query to delete it from the other table
            if (!db.deleteImage(id!!)) {
                db.deleteUploadedImage(id)
            }
            dismiss()
//            Finish the fullscreen activity
            activity?.finish()
        }


        return rootView
    }
}