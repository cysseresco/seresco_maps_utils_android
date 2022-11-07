package seresco.maps.utils.lib.utils

import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import seresco.maps.utils.lib.model.WMSItem
import java.io.IOException
import java.lang.NullPointerException


class Preferences (var context: Context): Constant {

    var pref: SharedPreferences? = null

    fun saveCoordinates(key: String, coords: MutableList<MutableList<Double>>?){
        val editor = pref!!.edit()
        val jsonString = Gson().toJson(coords)
        editor.putString(key, jsonString).apply()
    }

    fun getCoordinates(key: String): MutableList<MutableList<Double>>?{
        val jsonString = pref!!.getString(key, "")
        return Gson().fromJson(jsonString, object: TypeToken<MutableList<MutableList<Double>>>(){}.type)
    }

    fun saveLocation(key: String, location: Location){
        val editor = pref!!.edit()
        val jsonString = Gson().toJson(location)
        editor.putString(key, jsonString).apply()
    }

    fun getLocation(key: String): Location?{
        val jsonString = pref!!.getString(key, "")
        return Gson().fromJson(jsonString, object: TypeToken<Location>(){}.type)
    }

    fun saveInt(key: String, value: Int){
        val editor = pref!!.edit()
        editor.putInt(key, value).apply()
    }

    fun getInt(key: String): Int{
        return pref!!.getInt(key, 0)
    }

    fun saveLayers(key: String, value: WMSItem){
        val jsonStringLayers = pref!!.getString(CURRENT_WMS_LAYERS_SELECTED, "")

        try {
            val selectedLayers: MutableList<WMSItem> = Gson().fromJson(jsonStringLayers, object : TypeToken<List<WMSItem>>() {}.type)
            if (selectedLayers.contains(value)) {
                selectedLayers.remove(value)
            } else {
                selectedLayers.add(value)
            }
            val editor = pref!!.edit()
            val jsonString = Gson().toJson(selectedLayers)
            editor.putString(key, jsonString).apply()
        } catch (e: NullPointerException) {
            val arrayOfLayers = mutableListOf<WMSItem>()
            arrayOfLayers.add(value)
            val editor = pref!!.edit()
            val jsonString = Gson().toJson(arrayOfLayers)
            editor.putString(key, jsonString).apply()
        }

    }

    fun getLayers(key: String): List<WMSItem> {
        val jsonString = pref!!.getString(key, "")
        return try {
            Gson().fromJson(jsonString, object : TypeToken<List<WMSItem>>() {}.type)
        } catch (e: NullPointerException) {
            mutableListOf<WMSItem>()
        }
    }

    fun saveLayersIndex(key: String, value: Int){
        val jsonStringLayers = pref!!.getString(CURRENT_WMS_LAYERS_SELECTED_POSITION, "")

        try {
            val selectedItems: MutableList<Int> = Gson().fromJson(jsonStringLayers, object : TypeToken<List<Int>>() {}.type)

            if (selectedItems.contains(value)) {
                selectedItems.remove(value)
            } else {
                selectedItems.add(value)
            }

            val editor = pref!!.edit()
            val jsonString = Gson().toJson(selectedItems)
            editor.putString(key, jsonString).apply()
        } catch (e: NullPointerException) {
            val arrayOfLayers = mutableListOf<Int>()
            arrayOfLayers.add(value)
            val editor = pref!!.edit()
            val jsonString = Gson().toJson(arrayOfLayers)
            editor.putString(key, jsonString).apply()
        }


    }

    fun getLayersIndex(key: String): List<Int> {
        val jsonString = pref!!.getString(key, "")
        return try {
            Gson().fromJson(jsonString, object : TypeToken<List<Int>>() {}.type)
        } catch (e: NullPointerException) {
            mutableListOf<Int>()
        }
    }

    fun saveString(key: String, value: String) {
        val editor = pref!!.edit()
        editor.putString(key, value).apply()
    }

    fun getString(key: String): String? {
        return pref!!.getString(key, "")
    }

    fun saveDouble(key: String, double: Double){
        val editor = pref!!.edit()
        val jsonString = Gson().toJson(double)
        editor.putString(key, jsonString).apply()
    }

    fun getDouble(key: String): Double?{
        val jsonString = pref!!.getString(key, "")
        return Gson().fromJson(jsonString, object: TypeToken<Double>(){}.type)
    }


    fun clearPreferences(){
        pref!!.edit().clear().apply()
    }

    init {
        pref = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
    }
}

