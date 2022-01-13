package com.example.fafa_widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews

class FaFaWidgetProvider : AppWidgetProvider() {

    companion object{
        private const val TAG = "FaFaWidgetProvider"

        fun getView(context: Context,item:ItemBean):RemoteViews{
            return RemoteViews(context.packageName, R.layout.widget).also { views->
                val d = DataSource.outDateFormat(item.outDate)
                views.setTextViewText(R.id.name,item.name)
                views.setTextViewText(R.id.days,when{
                    d ==0->"今天        "
                    d ==1->"明天        "
                    d < 0 -> "已过期        "
                    else->"$d"
                })
            }
        }

        fun updateWidget(context: Context){
            val appWidgetManager: AppWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(Intent(context,FaFaWidgetProvider::class.java).component )
            appWidgetIds.forEach { id->
                //查询一次，先更新UI
                Log.i("fafa","update widget->$id")
                var item = DataSource.queryItemByWidgetId(context,id)
                if (item == null){
                    //nothing
                    item = ItemBean("--","error","")
                }
                val view = getView(context,item)
                appWidgetManager.updateAppWidget(id, view)
                Log.i(TAG, "updateWidget: $id,${item.name}")
            }
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        Log.d(
            TAG,
            "onUpdate() called with: context = $context, appWidgetManager = $appWidgetManager, appWidgetIds = $appWidgetIds"
        )

        appWidgetIds.forEach { id->
            //查询一次，先更新UI
            var item = DataSource.queryItemByWidgetId(context,id)
            if (item == null){
                //nothing
                item = ItemBean("--","error","")
            }
            val view = getView(context,item)
            appWidgetManager.updateAppWidget(id, view)
        }

    }

    override fun onAppWidgetOptionsChanged(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
    }
}