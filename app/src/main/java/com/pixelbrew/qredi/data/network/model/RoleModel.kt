package com.pixelbrew.qredi.data.network.model

class RoleModel {
    var id: String = ""
    var name: String = ""

    constructor() {}

    constructor(id: String, name: String) {
        this.id = id
        this.name = name
    }
}