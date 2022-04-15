package com.example.firebasertcandroid.components

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.lang.Exception
import javax.inject.Inject



@InstallIn(SingletonComponent::class)
@Module
class SharedPref @Inject constructor(  appContext: Context) {

    var pref:SharedPreferences = appContext.getSharedPreferences("SharePref", Context.MODE_PRIVATE)

    fun storeToPref(key:String, data:Any?)
    {
        when (data)
        {

            is String->{

                pref.edit()
                    .putString(
                        key,
                        data
                    )
                    .apply()

            }

            else->{
                throw Exception()
            }

        }
    }

    fun storeNull(key:String)
    {

        pref.edit()
            .putString(key,null)
            .putStringSet(key,null)
            .apply()

    }
}