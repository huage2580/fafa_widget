package com.example.fafa_widget

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * ⣿⣿⣿⣿⣿⣿⢟⣡⣴⣶⣶⣦⣌⡛⠟⣋⣩⣬⣭⣭⡛⢿⣿⣿⣿⣿
 * ⣿⣿⣿⣿⠋⢰⣿⣿⠿⣛⣛⣙⣛⠻⢆⢻⣿⠿⠿⠿⣿⡄⠻⣿⣿⣿
 * ⣿⣿⣿⠃⢠⣿⣿⣶⣿⣿⡿⠿⢟⣛⣒⠐⠲⣶⡶⠿⠶⠶⠦⠄⠙⢿
 * ⣿⠋⣠⠄⣿⣿⣿⠟⡛⢅⣠⡵⡐⠲⣶⣶⣥⡠⣤⣵⠆⠄⠰⣦⣤⡀
 * ⠇⣰⣿⣼⣿⣿⣧⣤⡸ ⣿⡀⠂⠁⣸⣿⣿⣿⣿⣇⠄⠈⢀⣿⣿⠿
 * ⣰⣿⣿⣿⣿⣿⣿⣿⣷⣤⣈⣙⠶⢾⠭⢉⣁⣴⢯⣭⣵⣶⠾⠓⢀⣴
 * ⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣉⣤⣴⣾⣿⣿⣦⣄⣤⣤⣄⠄⢿⣿
 * ⣿⣿⣿⣿⣿⣿⣿⣿⠿⠿⠿⠿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣿⣇⠈⢿
 * ⣿⣿⣿⣿⣿⣿⡟⣰⣞⣛⡒⢒⠤⠦⢬⣉⣉⣉⣉⣉⣉⣉⡥⠴⠂⢸
 * ⠻⣿⣿⣿⣿⣏⠻⢌⣉⣉⣩⣉⡛⣛⠒⠶⠶⠶⠶⠶⠶⠶⠶⠂⣸⣿
 * ⣥⣈⠙⡻⠿⠿⣷⣿⣿⣿⣿⣿⣿⣿⣿⣿⣾⣿⠿⠛⢉⣠⣶⣶⣿⣿
 * ⣿⣿⣿⣶⣬⣅⣒⣒⡂⠈⠭⠭⠭⠭⠭⢉⣁⣄⡀⢾⣿⣿⣿⣿⣿⣿
 *
 * 查询数据库,保存widgetId对应的物品id配置项
 * Created on 2022/1/12 16:18.
 * @author hua
 * @file DataSource
 */
object DataSource {
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)


    fun queryAllItem(context:Context):ArrayList<ItemBean>{
        //通过sqlite查询 /app_flutter/fafa.db
        val dbFile = File(context.getDir("flutter",Context.MODE_PRIVATE),"fafa.db")
        if (!dbFile.exists()){
            return ArrayList()
        }
        //查询数据
        val result = ArrayList<ItemBean>()
        val helper = SQLHelper(context,dbFile.absolutePath,null,6)
        val db = helper.readableDatabase
        val c = db.rawQuery("SELECT id,name,out_date FROM item WHERE out_date is NOT NULL ORDER BY id DESC", emptyArray())
        while (c.moveToNext()){
          val id = c.getInt(0)
          val name = c.getString(1)
          val outDate = c.getString(2)
            result.add(ItemBean("$id",name, outDate))
        }
        c.close()
        db.close()
        helper.close()
        return result
    }

    fun queryItemByWidgetId(context: Context,widgetId:Int): ItemBean?{
        //先查出物品id
        val sp = context.getSharedPreferences("widget.xml",Context.MODE_PRIVATE)
        val itemId = sp.getString("widget_$widgetId",null) ?: return null

        //SELECT id,name,out_date FROM item WHERE out_date is NOT NULL AND id = 1
        val dbFile = File(context.getDir("flutter",Context.MODE_PRIVATE),"fafa.db")
        if (!dbFile.exists()){
            return null
        }
        //查询数据
        var result: ItemBean? = null
        val helper = SQLHelper(context,dbFile.absolutePath,null,5)
        val db = helper.readableDatabase
        val c = db.rawQuery("SELECT id,name,out_date FROM item WHERE out_date is NOT NULL AND id = $itemId", emptyArray())
        while (c.moveToNext()){
            val id = c.getInt(0)
            val name = c.getString(1)
            val outDate = c.getString(2)
            result = ItemBean("$id",name, outDate)
        }
        c.close()
        db.close()
        helper.close()
        return result
    }

    fun bindingWidgetWithItem(context: Context,widgetId:Int,itemId:String){
        val sp = context.getSharedPreferences("widget.xml",Context.MODE_PRIVATE)
        sp.edit().putString("widget_$widgetId",itemId).apply()
    }


    fun outDateFormat(date:String):Int{
        if (date.isEmpty()){
            return 0
        }
        val d = format.parse(date)
        val now = Calendar.getInstance(Locale.CHINA)
        val today = Calendar.getInstance(Locale.CHINA)
        today.clear()
        today.set(now.get(Calendar.YEAR),now.get(Calendar.MONTH),now.get(Calendar.DATE))
        val between_days: Long = (d!!.time - today.time.time) / (1000 * 3600 * 24).toLong()
        return between_days.toInt()
    }


    class SQLHelper(
        context: Context?,
        name: String?,
        factory: SQLiteDatabase.CursorFactory?,
        version: Int
    ) : SQLiteOpenHelper(context, name, factory, version) {
        override fun onCreate(db: SQLiteDatabase?) {
            // ?
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            // ?
        }

    }

}

data class ItemBean(var id:String,var name:String,var outDate:String)