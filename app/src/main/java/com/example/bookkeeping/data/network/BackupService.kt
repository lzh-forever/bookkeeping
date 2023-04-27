package com.example.bookkeeping.data.network

import okhttp3.RequestBody
import retrofit2.http.*

interface BackupService {


    @POST(Api.BACKUP_UPLOAD_URL)
    suspend fun backup(@Body requestBody: RequestBody): Response


    @GET(Api.BACKUP_DOWNLOAD_URL)
    suspend fun getBackup(): DataResponse<DatabaseBean>

}