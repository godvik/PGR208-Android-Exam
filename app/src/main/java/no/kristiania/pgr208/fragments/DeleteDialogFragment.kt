package no.kristiania.pgr208.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.fragment_delete_dialog.view.*
import no.kristiania.pgr208.R
import no.kristiania.pgr208.utils.DatabaseHandler

class DeleteDialogFragment() : DialogFragment() {
    private lateinit var db: DatabaseHandler
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        db = context?.let { DatabaseHandler(it) }!!
        val rootView: View = inflater.inflate(R.layout.fragment_delete_dialog, container, false)
//        Get parcelables
        val id = this.arguments?.getInt("imageId")
        val column = this.arguments?.getString("columnName")


        rootView.cancel_button.setOnClickListener {
            dismiss()
        }

        rootView.delete_button.setOnClickListener {
// Delete image. If the user attempts to delete "the original image" aka the uploaded image, the database will cascade delete all the related images
//            Check to see which table the requested image should be deleted from
            if (column.equals("result_id")) {
                db.deleteImage(id!!) { activity?.finish() }
            } else {
                db.deleteUploadedImage(id!!) { activity?.finish() }
            }
            dismiss()
        }


        return rootView
    }
}