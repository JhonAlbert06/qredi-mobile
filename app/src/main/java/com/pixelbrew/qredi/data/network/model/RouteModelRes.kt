package com.pixelbrew.qredi.data.network.model

class RouteModel {
    var companyId: String = ""
    var name: String = ""

    constructor() {}

    constructor(companyId: String, name: String) {
        this.companyId = companyId
        this.name = name
    }
}

class RouteModel1 {
    var id: String = ""
    var name: String = ""

    constructor() {}

    constructor(companyId: String, name: String) {
        this.id = companyId
        this.name = name
    }
}

class RouteModelRes {
    var id: String = ""
    var name: String = ""
    var company: CompanyModel = CompanyModel()

    constructor() {}

    constructor(id: String, name: String, company: CompanyModel) {
        this.id = id
        this.name = name
        this.company = company
    }
}