package com.pixelbrew.qredi.data.network.model

class UserModel {
    var id: String = ""
    var company: CompanyModel = CompanyModel()
    var role: RoleModel = RoleModel()
    var firstName: String = ""
    var lastName: String = ""
    var userName: String = ""
    var isNew: Boolean = false
}