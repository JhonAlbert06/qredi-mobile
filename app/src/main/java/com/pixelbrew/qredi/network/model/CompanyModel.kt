package com.pixelbrew.qredi.network.model

class CompanyModel {
    var id: String = ""
    var name: String = ""

    constructor() {}

    constructor(id: String, name: String) {
        this.id = id
        this.name = name
    }
}