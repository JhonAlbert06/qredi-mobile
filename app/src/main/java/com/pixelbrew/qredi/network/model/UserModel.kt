package com.pixelbrew.qredi.network.model

class UserModel {
    var id: String = ""
    var company: CompanyModel = CompanyModel()
    var role: RoleModel = RoleModel()
    var firstName: String = ""
    var lastName: String = ""
    var userName: String = ""
    var isNew: Boolean = false

    constructor() {}

    constructor(
        id: String,
        company: CompanyModel,
        role: RoleModel,
        firstName: String,
        lastName: String,
        userName: String,
        isNew: Boolean
    ) {
        this.id = id
        this.company = company
        this.role = role
        this.firstName = firstName
        this.lastName = lastName
        this.userName = userName
        this.isNew = isNew
    }
}