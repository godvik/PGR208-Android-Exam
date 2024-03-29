package no.kristiania.pgr208.utils

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import no.kristiania.pgr208.data.DatabaseImage

class DatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {


    //    Constants for tablenames and columns. Increment DATABASE_VERSION to apply database changes
    companion object {
        private const val DATABASE_VERSION = 11
        private const val DATABASE_NAME = "ImageDatabase"
        private const val TABLE_UPLOADEDIMAGES = "UploadedImagesTable"
        private const val TABLE_SAVEDIMAGES = "SavedImagesTable"

        private const val KEY_UPLOADID = "upload_id"
        private const val KEY_RESULTID = "result_id"
        private const val KEY_UPLOADIMAGE = "uploaded_image"
        private const val KEY_SAVEDIMAGE = "saved_image"
    }

    override fun onConfigure(db: SQLiteDatabase) {
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_UPLOADEDIMAGES_TABLE =
            ("CREATE TABLE $TABLE_UPLOADEDIMAGES($KEY_UPLOADID INTEGER PRIMARY KEY,$KEY_UPLOADIMAGE BLOB)")
        val CREATE_SAVEDIMAGES_TABLE =
            ("CREATE TABLE $TABLE_SAVEDIMAGES($KEY_RESULTID INTEGER PRIMARY KEY, $KEY_UPLOADID INTEGER, $KEY_SAVEDIMAGE BLOB, FOREIGN KEY ($KEY_UPLOADID) references $TABLE_UPLOADEDIMAGES($KEY_UPLOADID) ON DELETE CASCADE)")

        db?.execSQL(CREATE_UPLOADEDIMAGES_TABLE)
        db?.execSQL(CREATE_SAVEDIMAGES_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_UPLOADEDIMAGES")
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_SAVEDIMAGES")
        onCreate(db)
    }


    //    Insert the image that the user uploaded to the server
    fun addUploadedImage(img: DatabaseImage): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_UPLOADIMAGE, img.image)
        // Insert row
        val success = db.insert(TABLE_UPLOADEDIMAGES, null, contentValues)
        db.close()
        return success
    }


    //    Insert the images the user wants to save and create a connection to the uploaded image
    fun addSavedImage(img: DatabaseImage): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()

//        Get the upload_id of the latest uploaded image and insert it into db to create relation between saved images and uploaded images
        var id: Int? = null
        val cursor: Cursor = db.rawQuery("SELECT  * FROM $TABLE_UPLOADEDIMAGES", null)
        if (cursor.moveToLast()) {
//            Set the cursor to the first column (upload_id) and to the last entry
            id = cursor.getInt(0)
        }
        cursor.close()

        contentValues.put(KEY_UPLOADID, id)
        contentValues.put(KEY_SAVEDIMAGE, img.image)
        // Insert row
        val success = db.insert(TABLE_SAVEDIMAGES, null, contentValues)
        db.close()
        return success
    }


    //    Get the upload_id of all the uploaded images
    @SuppressLint("Range")
    fun getIds(): ArrayList<Int> {
        val idList: ArrayList<Int> = ArrayList()
        val selectQuery = "SELECT * FROM $TABLE_UPLOADEDIMAGES"
        val db = this.readableDatabase
        val cursor: Cursor?
        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        if (cursor.moveToFirst()) {
            do {
                idList.add(cursor.getInt(cursor.getColumnIndex(KEY_UPLOADID)))

            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return idList
    }

    //    Select all the images related to the upload_id of the original image. Add them and the original image to a list and return it
    @SuppressLint("Range")
    fun getRelatedImages(id: Int): ArrayList<DatabaseImage> {

        val imgList: ArrayList<DatabaseImage> = ArrayList()
        val selectQuery =
            "SELECT $TABLE_SAVEDIMAGES.$KEY_RESULTID, $TABLE_SAVEDIMAGES.$KEY_SAVEDIMAGE, $TABLE_UPLOADEDIMAGES.$KEY_UPLOADIMAGE, $TABLE_UPLOADEDIMAGES.$KEY_UPLOADID " +
                    "FROM $TABLE_SAVEDIMAGES " +
                    "JOIN $TABLE_UPLOADEDIMAGES ON $TABLE_SAVEDIMAGES.$KEY_UPLOADID = $TABLE_UPLOADEDIMAGES.$KEY_UPLOADID " +
                    "WHERE $TABLE_UPLOADEDIMAGES.$KEY_UPLOADID = $id"

        val db = this.readableDatabase
        var cursor: Cursor?
        var imageId: Int
        var image: ByteArray


        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        if (cursor.moveToFirst()) {
//            Add the original image to the list first
            imageId = cursor.getInt(cursor.getColumnIndex(KEY_UPLOADID))
            image = cursor.getBlob(cursor.getColumnIndex(KEY_UPLOADIMAGE))


            val originalImage = DatabaseImage(id = imageId, image = image, column = KEY_UPLOADID)
            imgList.add(originalImage)
//            Add the rest of the images
            do {
                imageId = cursor.getInt(cursor.getColumnIndex(KEY_RESULTID))
                image = cursor.getBlob(cursor.getColumnIndex(KEY_SAVEDIMAGE))

                val img = DatabaseImage(id = imageId, image = image, column = KEY_RESULTID)
                imgList.add(img)
            } while (cursor.moveToNext())
        }


//        If no images are saved to the original image, the above JOIN query will fail. Therefore we use this to return only the uploaded image
//        This is because the user can still go back and search and add new images
        if (imgList.isEmpty()) {
            val backUpQuery = "SELECT * FROM $TABLE_UPLOADEDIMAGES WHERE $KEY_UPLOADID = $id"
            try {
                cursor = db.rawQuery(backUpQuery, null)
            } catch (e: SQLiteException) {
                db.execSQL(selectQuery)
                return ArrayList()
            }
            if (cursor.moveToLast()) {

                imageId = cursor.getInt(0)
                image = cursor.getBlob(cursor.getColumnIndex(KEY_UPLOADIMAGE))
                val img = DatabaseImage(id = imageId, image = image, column = KEY_UPLOADID)
                imgList.add(img)
            }
        }

        cursor.close()
        db.close()
        return imgList
    }


    //    Delete one of the downloaded images. Uses a callback to ensure the operation is completed before the recyclerview updates so that it has the latest data
    fun deleteImage(id: Int, myCallback: () -> Unit): Boolean {
        val db = this.writableDatabase
        val response = db.delete(TABLE_SAVEDIMAGES, "$KEY_RESULTID = $id", null) != 0
        db.close()
        myCallback.invoke()
        return response
    }

    //    Delete one of the uploaded images. Uses a callback to ensure the operation is completed before the recyclerview updates so that it has the latest data
//    Deleting one of these images will also cascade delete all related images.
    fun deleteUploadedImage(id: Int, myCallback: () -> Unit): Boolean {
        val db = this.writableDatabase
        val response = db.delete(TABLE_UPLOADEDIMAGES, "$KEY_UPLOADID = $id", null) != 0
        db.close()
        myCallback.invoke()
        return response
    }

}

