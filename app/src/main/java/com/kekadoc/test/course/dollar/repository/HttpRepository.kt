package com.kekadoc.test.course.dollar.repository

import com.kekadoc.test.course.dollar.model.ValCurs
import com.kekadoc.test.course.dollar.model.ValCursRange
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.await
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.*


interface CourseService {

    //http://www.cbr.ru/scripts/XML_daily.asp?date_req=02/03/2002
    @GET("XML_daily.asp")
    fun getDailyCourse(): Call<ValCurs>

    //http://www.cbr.ru/scripts/XML_dynamic.asp?date_req1=02/03/2001&date_req2=14/03/2001&VAL_NM_RQ=R01235
    @GET("XML_dynamic.asp")
    fun getMonthlyCourse(@Query("date_req1") from: String,
                         @Query("date_req2") to: String,
                         @Query("VAL_NM_RQ") valute: String): Call<ValCursRange>
}

object HttpRepository {

    const val DOLLAR_ID = "R01235"

    private var retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://www.cbr.ru/scripts/")
        .client(OkHttpClient())
        .addConverterFactory(SimpleXmlConverterFactory.create())
        .build()

    private val service = retrofit.create(CourseService::class.java)

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("ru"))

    suspend fun loadDailyCourse(): ValCurs {
        return service.getDailyCourse().await()
    }
    suspend fun loadMonthlyCourse(): ValCursRange {
        val fromDate = Calendar.getInstance().apply {
            roll(Calendar.MONTH, -1)
        }.time
        val toDate = Calendar.getInstance().time

        val from = dateFormat.format(fromDate)
        val to = dateFormat.format(toDate)

        val value = HttpRepository.DOLLAR_ID

        return service.getMonthlyCourse(from, to, value).await()

    }

}