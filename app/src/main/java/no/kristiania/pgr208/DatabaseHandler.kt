package no.kristiania.pgr208

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 5
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

        var id: Int? = null
        val cursor: Cursor = db.rawQuery("SELECT  * FROM $TABLE_UPLOADEDIMAGES", null)
        if (cursor.moveToLast()) {
            id = cursor.getInt(0) //to get id, 0 is the column index
        }
        cursor.close()
        contentValues.put(KEY_UPLOADID, id)
        contentValues.put(KEY_SAVEDIMAGE, img.image)
        // Insert row
        val success = db.insert(TABLE_SAVEDIMAGES, null, contentValues)
        db.close()
        return success
    }



    fun viewImage(): ArrayList<DatabaseImage> {

        val imgList: ArrayList<DatabaseImage> = ArrayList()
        val selectQuery = "SELECT * FROM $TABLE_UPLOADEDIMAGES"

        val db = this.readableDatabase
        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var id: Int
        var image: ByteArray

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

    fun getRelatedImages(): ArrayList<DatabaseImage> {
        val imgList: ArrayList<DatabaseImage> = ArrayList()
        val selectQuery = "SELECT SavedImagesTable.result_id, SavedImagesTable.saved_image, UploadedImagesTable.uploaded_image FROM SavedImagesTable JOIN UploadedImagesTable ON SavedImagesTable.upload_id = UploadedImagesTable.upload_id"

        val db = this.readableDatabase
        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var id: Int
        var image: ByteArray

        if (cursor.moveToFirst()) {
            id = cursor.getInt(cursor.getColumnIndex(KEY_RESULTID))
            image = cursor.getBlob(cursor.getColumnIndex(KEY_UPLOADIMAGE))
            val originalImage = DatabaseImage(id = id, image = image)
            imgList.add(originalImage)

            do {
                id = cursor.getInt(cursor.getColumnIndex(KEY_RESULTID))
                image = cursor.getBlob(cursor.getColumnIndex(KEY_SAVEDIMAGE))

                val img = DatabaseImage(id = id, image = image)
                imgList.add(img)
            } while (cursor.moveToNext())
        }
        cursor.close()

        return imgList
    }



    fun deleteImage(img: DatabaseImage): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_UPLOADID, img.id)

        val success = db.delete(TABLE_UPLOADEDIMAGES, KEY_UPLOADID + "=" + img.id, null)
        db.close()
        return success
    }
}