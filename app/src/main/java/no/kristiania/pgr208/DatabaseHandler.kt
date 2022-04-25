package no.kristiania.pgr208

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {


    //    Constants for tablenames and columns. Increment DATABASE_VERSION to apply database changes
    companion object {
        private const val DATABASE_VERSION = 7
        private const val DATABASE_NAME = "ImageDatabase"
        private const val TABLE_UPLOADEDIMAGES = "UploadedImagesTable"
        private const val TABLE_SAVEDIMAGES = "SavedImagesTable"

        private const val KEY_UPLOADID = "upload_id"
        private const val KEY_RESULTID = "result_id"
        private const val KEY_UPLOADIMAGE = "uploaded_image"
        private const val KEY_SAVEDIMAGE = "saved_image"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_UPLOADEDIMAGES_TABLE =
            ("CREATE TABLE $TABLE_UPLOADEDIMAGES($KEY_UPLOADID INTEGER PRIMARY KEY,$KEY_UPLOADIMAGE BLOB)")
        val CREATE_SAVEDIMAGES_TABLE =
            ("CREATE TABLE $TABLE_SAVEDIMAGES($KEY_RESULTID INTEGER PRIMARY KEY, $KEY_UPLOADID references $TABLE_SAVEDIMAGES($KEY_UPLOADID),  $KEY_SAVEDIMAGE BLOB)")

        db?.execSQL(CREATE_UPLOADEDIMAGES_TABLE)
        db?.execSQL(CREATE_SAVEDIMAGES_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_UPLOADEDIMAGES")
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_SAVEDIMAGES")
        onCreate(db)
    }

    fun addUploadedImage(img: DatabaseImage): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_UPLOADIMAGE, img.image)
        // Insert row
        val success = db.insert(TABLE_UPLOADEDIMAGES, null, contentValues)
        db.close()
        return success
    }

    fun addSavedImage(img: DatabaseImage): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()

//        Get the ID of the latest uploaded image and insert it into db to create relation between saved images and uploaded images
        var id: Int? = null
        val cursor: Cursor = db.rawQuery("SELECT  * FROM $TABLE_UPLOADEDIMAGES", null)
        if (cursor.moveToLast()) {
//            Set the cursor to the first column (id column) and to the last entry
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

    //    Select all uploaded images and return them in a list
    fun viewImage(): ArrayList<DatabaseImage> {
        val imgList: ArrayList<DatabaseImage> = ArrayList()
        val selectQuery = "SELECT * FROM $TABLE_UPLOADEDIMAGES"
        val db = this.readableDatabase
        val cursor: Cursor?
        var id: Int
        var image: ByteArray

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex(KEY_UPLOADID))
                image = cursor.getBlob(cursor.getColumnIndex(KEY_UPLOADIMAGE))

                val img = DatabaseImage(id = id, image = image)
                imgList.add(img)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return imgList
    }

    //    Select all the images related to the ID of the original image. Add them and the original image to a list and return it
    fun getRelatedImages(id: Int): ArrayList<DatabaseImage> {
        val imgList: ArrayList<DatabaseImage> = ArrayList()
        val selectQuery =
            "SELECT $TABLE_SAVEDIMAGES.$KEY_RESULTID, $TABLE_SAVEDIMAGES.$KEY_SAVEDIMAGE, $TABLE_UPLOADEDIMAGES.$KEY_UPLOADIMAGE " +
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
            imageId = cursor.getInt(cursor.getColumnIndex(KEY_RESULTID))
            image = cursor.getBlob(cursor.getColumnIndex(KEY_UPLOADIMAGE))
            val originalImage = DatabaseImage(id = imageId, image = image)
            imgList.add(originalImage)
//            Add the rest of the images
            do {
                imageId = cursor.getInt(cursor.getColumnIndex(KEY_RESULTID))
                image = cursor.getBlob(cursor.getColumnIndex(KEY_SAVEDIMAGE))

                val img = DatabaseImage(id = imageId, image = image)
                imgList.add(img)
            } while (cursor.moveToNext())
        }

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
                val img = DatabaseImage(id = imageId, image = image)
                imgList.add(img)
            }
        }

        cursor.close()
        db.close()
        return imgList
    }

}

