package com.example.bookkeeping.data.network

object Api {
    const val BASE_URL:String = "http://192.168.0.103:5000/"
    const val LOGIN_URL:String = "auth/login"
    const val REGISTER_URL:String = "auth/register"
    const val SEND_AUTH_URL:String = "auth/sendAuthCode"
    const val BACKUP_UPLOAD_URL:String = "backup/upload"
    const val BACKUP_DOWNLOAD_URL:String = "backup/download"
}