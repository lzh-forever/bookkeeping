package com.example.bookkeeping.data.network

import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {

    @POST(Api.LOGIN_URL)
    suspend fun login(@Body requestBody: RequestBody): DataResponse<String>


    @POST(Api.REGISTER_URL)
    suspend fun register(@Body requestBody: RequestBody): Response


    @POST(Api.SEND_AUTH_URL)
    suspend fun sendCaptcha(@Body requestBody: RequestBody): Response

}