package com.medico.medko.Constants

import com.medico.medko.Model.Order
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RetrofitInterface {

    @POST("/getOrderId")
    fun getOrderId(@Body map : HashMap<String, String>) : Call<Order>

    @POST("/UpdateTransactionStatus")
    fun updateStatus(@Body map : HashMap<String, String>) : Call<String>

}