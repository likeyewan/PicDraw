package com.shoulashou.piantdemo.doublemoveview3.test

import android.content.Context
import android.util.Log
import java.io.*
import java.lang.StringBuilder

/**
 *Created by likeye on 2020/9/24 9:21.
 **/
class FileUtil {
     fun saveFile(context: Context,inputText:String,fileName:String){
        try{
            val output=context.openFileOutput(fileName, Context.MODE_PRIVATE)
            val writer= BufferedWriter(OutputStreamWriter(output))
            writer.use { it.write(inputText) }
        }catch (e: IOException){
            e.printStackTrace()
        }
    }
    fun load(context: Context,fileName:String):String{
        val content=StringBuilder()
        try{
            val input=context.openFileInput(fileName)
            val reader=BufferedReader(InputStreamReader(input))
            reader.use { reader.forEachLine { content.append(it) } }
        }catch (e:IOException){
            e.printStackTrace()
        }
        return content.toString()
    }

}