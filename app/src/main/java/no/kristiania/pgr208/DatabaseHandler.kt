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
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "ImageDatabase"
        private const val TABLE_CONTACTS = "SavedImagesTable"

        private const val KEY_ID = "_id"
        private const val KEY_THUMBNAIL = "thumbnail"
        private const val KEY_IMAGE = "image"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_CONTACTS_TABLE =
            ("CREATE TABLE " + TABLE_CONTACTS + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_THUMBNAIL + " TEXT," + KEY_IMAGE + " TEXT" + ")")
        db?.execSQL(CREATE_CONTACTS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS)
        onCreate(db)
    }

    fun addImage(img: DatabaseImage): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_THUMBNAIL, img.thumbnail)
        contentValues.put(KEY_IMAGE, img.image)

        // Insert row
        val success = db.insert(TABLE_CONTACTS, null, contentValues)

        db.close()
        return success
    }

    fun viewImage(): ArrayList<DatabaseImage> {

        val imgList: ArrayList<DatabaseImage> = ArrayList()

        val selectQuery = "SELECT * FROM $TABLE_CONTACTS"

        val db = this.readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var id: Int
        var thumbnail: String
        var image: String

        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex(KEY_ID))
                thumbnail = cursor.getString(cursor.getColumnIndex(KEY_THUMBNAIL))
                image = cursor.getString(cursor.getColumnIndex(KEY_IMAGE))

                val img = DatabaseImage(id = id, thumbnail = thumbnail, image = image)
                imgList.add(img)
            } while (cursor.moveToNext())
        }

        return imgList

    }

    fun deleteImage(img: DatabaseImage): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_ID, img.id)

        val success = db.delete(TABLE_CONTACTS, KEY_ID + "=" + img.id, null)
        db.close()
        return success
    }
}