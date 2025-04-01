package com.pixelbrew.qredi.network.api

import com.pixelbrew.qredi.network.model.DownloadModel
import com.pixelbrew.qredi.network.model.LoginRequest
import com.pixelbrew.qredi.network.model.RouteModel
import com.pixelbrew.qredi.network.model.TokenModel
import com.pixelbrew.qredi.network.model.UploadFee
import com.pixelbrew.qredi.network.model.UserModel
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("/user/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): TokenModel

    @GET("/user/loadUser")
    suspend fun loadUser(): UserModel

    @GET("/routes")
    suspend fun getRoutes(): List<RouteModel>

    @GET("/route/download/{id}")
    suspend fun downloadRoute(
        @Path("id") id: String
    ): List<DownloadModel>

    @PUT("/fee/uploadFees")
    suspend fun uploadFees(
        @Body fees: List<UploadFee>
    ): List<UploadFee>
}