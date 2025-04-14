package com.pixelbrew.qredi.data.network.model

class RouteModel {
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