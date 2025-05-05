package com.pixelbrew.qredi.data.network.model

class CustomerModel {
    var companyId: String = ""
    var cedula: String = ""
    var names: String = ""
    var lastNames: String = ""
    var address: String = ""
    var phone: String = ""
    var reference: String = ""

    constructor(
        companyId: String,
        cedula: String,
        names: String,
        lastNames: String,
        address: String,
        phone: String,
        reference: String
    ) {
        this.companyId = companyId
        this.cedula = cedula
        this.names = names
        this.lastNames = lastNames
        this.address = address
        this.phone = phone
        this.reference = reference
    }
}

class CustomerModelRes {
    var id: String = ""
    var company: CompanyModel = CompanyModel()
    var cedula: String = ""
    var firstName: String = ""
    var lastName: String = ""
    var address: String = ""
    var phone: String = ""
    var reference: String = ""

    constructor() {}
}

class CustomerModelResWithDetail {
    var id: String = ""
    var company: CompanyModel = CompanyModel()
    var cedula: String = ""
    var firstName: String = ""
    var lastName: String = ""
    var address: String = ""
    var phone: String = ""
    var reference: String = ""
    var loans: List<LoanModelRes> = emptyList()

    constructor() {}
}