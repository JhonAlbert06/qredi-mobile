package com.pixelbrew.qredi.data.network.api

import com.pixelbrew.qredi.data.network.model.CustomerModel
import com.pixelbrew.qredi.data.network.model.CustomerModelRes
import com.pixelbrew.qredi.data.network.model.DownloadModel
import com.pixelbrew.qredi.data.network.model.LoginRequest
import com.pixelbrew.qredi.data.network.model.RouteModel
import com.pixelbrew.qredi.data.network.model.TokenModel
import com.pixelbrew.qredi.data.network.model.UploadFee
import com.pixelbrew.qredi.data.network.model.UserModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Url

interface ApiService {

    @POST
    suspend fun login(
        @Url url: String,
        @Body loginRequest: LoginRequest
    ): TokenModel

    @GET
    suspend fun loadUser(
        @Url url: String
    ): UserModel

    @GET
    suspend fun getRoutes(
        @Url url: String
    ): List<RouteModel>

    @GET
    suspend fun downloadRoute(
        @Url url: String
    ): List<DownloadModel>

    @PUT
    suspend fun uploadFees(
        @Url url: String,
        @Body fees: List<UploadFee>
    ): Response<Unit>

    @POST
    suspend fun createCustomer(
        @Url url: String,
        @Body customer: CustomerModel
    ): CustomerModelRes

    @GET
    suspend fun getCustomers(
        @Url url: String
    ): List<CustomerModelRes>
}