package com.pixelbrew.qredi.data.network.api

import com.pixelbrew.qredi.data.network.model.CloseCashModel
import com.pixelbrew.qredi.data.network.model.CustomerModel
import com.pixelbrew.qredi.data.network.model.CustomerModelRes
import com.pixelbrew.qredi.data.network.model.CustomerModelResWithDetail
import com.pixelbrew.qredi.data.network.model.LoanDownloadModel
import com.pixelbrew.qredi.data.network.model.LoanModel
import com.pixelbrew.qredi.data.network.model.LoanModelRes
import com.pixelbrew.qredi.data.network.model.LoginRequest
import com.pixelbrew.qredi.data.network.model.RouteModel
import com.pixelbrew.qredi.data.network.model.RouteModel1
import com.pixelbrew.qredi.data.network.model.RouteModelRes
import com.pixelbrew.qredi.data.network.model.SpentModel
import com.pixelbrew.qredi.data.network.model.SpentModelRes
import com.pixelbrew.qredi.data.network.model.SpentTypeModel
import com.pixelbrew.qredi.data.network.model.SpentTypeModelRes
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
    ): Response<TokenModel>

    @GET
    suspend fun loadUser(
        @Url url: String
    ): Response<UserModel>

    @GET
    suspend fun getRoutes(
        @Url url: String
    ): Response<List<RouteModelRes>>

    @POST
    suspend fun createRoute(
        @Url url: String,
        @Body route: RouteModel
    ): Response<Unit>

    @PUT
    suspend fun updateRoute(
        @Url url: String,
        @Body route: RouteModel1
    ): Response<Unit>

    @GET
    suspend fun downloadRoute(
        @Url url: String
    ): Response<List<LoanDownloadModel>>

    @PUT
    suspend fun uploadFees(
        @Url url: String,
        @Body fees: List<UploadFee>
    ): Response<Unit>

    @POST
    suspend fun createCustomer(
        @Url url: String,
        @Body customer: CustomerModel
    ): Response<CustomerModelRes>

    @GET
    suspend fun getCustomers(
        @Url url: String
    ): Response<List<CustomerModelRes>>

    @POST
    suspend fun createLoan(
        @Url url: String,
        @Body loan: LoanModel
    ): Response<LoanModelRes>

    @GET
    suspend fun getLoans(
        @Url url: String
    ): Response<List<LoanModelRes>>

    @POST
    suspend fun closeCashRegister(
        @Url url: String,
        @Body closeCashModel: CloseCashModel
    ): Response<Unit>

    @GET
    suspend fun getCustomerWithDetail(
        @Url url: String
    ): Response<CustomerModelResWithDetail>

    @POST
    suspend fun createSpentType(
        @Url url: String,
        @Body spent: SpentTypeModel
    ): Response<Unit>

    @GET
    suspend fun getSpentTypes(
        @Url url: String
    ): Response<List<SpentTypeModelRes>>

    @POST
    suspend fun createSpent(
        @Url url: String,
        @Body spent: SpentModel
    ): Response<Unit>

    @GET
    suspend fun getSpents(
        @Url url: String
    ): Response<List<SpentModelRes>>

}