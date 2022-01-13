package com.example.fafa_widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RemoteViews
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList


class WidgetConfigureActivity : Activity() {

    private val widgetId:Int by lazy { intent?.extras?.getInt(
        AppWidgetManager.EXTRA_APPWIDGET_ID,
        AppWidgetManager.INVALID_APPWIDGET_ID
    ) ?: AppWidgetManager.INVALID_APPWIDGET_ID }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(Activity.RESULT_CANCELED)
        setContentView(R.layout.configure)

        title = "选择物品"
        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID){
            showToast("id异常")
            return
        }

        fetchData()

    }


    private fun fetchData(){
        val data = DataSource.queryAllItem(this)
        val list = findViewById<RecyclerView>(R.id.list)
        list.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        list.adapter = Adapter(data).apply {
            callback = View.OnClickListener { view->
                val id = view.tag as String
                bindingWithItem(id)
            }
        }

    }

    private fun bindingWithItem(itemId:String){
        //绑定数据
        DataSource.bindingWidgetWithItem(this,widgetId,itemId)
        //查询一次，先更新UI
        val item = DataSource.queryItemByWidgetId(this,widgetId)
        if (item == null){
            showToast("绑定数据异常")
            return
        }
        submit(item)
    }


    private fun submit(item: ItemBean){
        val appWidgetManager: AppWidgetManager = AppWidgetManager.getInstance(this)
        val view = FaFaWidgetProvider.getView(this,item)
        appWidgetManager.updateAppWidget(widgetId, view)
        //通知配置完成
        val resultValue = Intent().apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        }
        setResult(Activity.RESULT_OK, resultValue)
        finish()

    }

    private fun Activity.showToast(text:String){
        Toast.makeText(this,text,Toast.LENGTH_LONG).show()
    }

    class Adapter(var data:ArrayList<ItemBean>) : RecyclerView.Adapter<VH>(){

        var callback:View.OnClickListener? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            return VH(LayoutInflater.from(parent.context).inflate(R.layout.item,parent,false))
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.tvName.text = data[position].name
            holder.tvDays.text = data[position].outDate
            holder.itemView.tag = data[position].id
            holder.itemView.setOnClickListener(callback)
        }

        override fun getItemCount() = data.size

    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvName:TextView = itemView.findViewById(R.id.name)
        var tvDays:TextView = itemView.findViewById(R.id.days)
    }

}