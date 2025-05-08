package com.pixelbrew.qredi.data.network.model

class SpentTypeModel {
    var name: String = ""
}

class SpentTypeModelRes {
    var id: String = ""
    var name: String = ""
}

class SpentModel {
    var companyId: String = ""
    var typeId: String = ""
    var note: String = ""
    var cost = 0.0
}

class SpentModelRes {
    var id: String = ""
    var company: CompanyModel = CompanyModel()
    var type: SpentTypeModelRes = SpentTypeModelRes()
    var note: String = ""
    var cost = 0.0
}