package com.pixelbrew.qredi.network.model


class DownloadModel {
    var id: String = ""
    var amount: Double = 0.0
    var interest: Double = 0.0
    var feesQuantity: Int = 0
    var loanIsPaid: Boolean = false
    var isRenewed: Boolean = false
    var isCurrentLoan: Boolean = false
    var date: DateClass = DateClass()
    var customer: Customer = Customer()
    var route: RouteModel = RouteModel()
    var user: UserModel = UserModel()
    var fees: List<Fee> = listOf()

    constructor()

    constructor(
        id: String,
        amount: Double,
        interest: Double,
        feesQuantity: Int,
        loanIsPaid: Boolean,
        isRenewed: Boolean,
        isCurrentLoan: Boolean,
        date: DateClass,
        customer: Customer,
        route: RouteModel,
        user: UserModel,
        fees: List<Fee>
    ) {
        this.id = id
        this.amount = amount
        this.interest = interest
        this.feesQuantity = feesQuantity
        this.loanIsPaid = loanIsPaid
        this.isRenewed = isRenewed
        this.isCurrentLoan = isCurrentLoan
        this.date = date
        this.customer = customer
        this.route = route
        this.user = user
        this.fees = fees
    }
}

class Customer {
    var id: String = ""
    var company: CompanyModel = CompanyModel()
    var cedula: String = ""
    var firstName: String = ""
    var lastName: String = ""
    var address: String = ""
    var phone: String = ""
    var civilStatus: String = ""
    var reference: String = ""

    constructor()

    constructor(
        id: String,
        company: CompanyModel,
        cedula: String,
        firstName: String,
        lastName: String,
        address: String,
        phone: String,
        civilStatus: String,
        reference: String
    ) {
        this.id = id
        this.company = company
        this.cedula = cedula
        this.firstName = firstName
        this.lastName = lastName
        this.address = address
        this.phone = phone
        this.civilStatus = civilStatus
        this.reference = reference
    }

}

class DateClass {
    var day: Int = 0
    var month: Int = 0
    var year: Int = 0
    var hour: Int = 0
    var minute: Int = 0
    var second: Int = 0
    var timezone: String = ""

    constructor() {}

    constructor(
        day: Int,
        month: Int,
        year: Int,
        hour: Int,
        minute: Int,
        second: Int,
        timezone: String
    ) {
        this.day = day
        this.month = month
        this.year = year
        this.hour = hour
        this.minute = minute
        this.second = second
        this.timezone = timezone
    }
}

class Fee {
    var id: String = ""
    var number: Int = 0
    var expectedDate: DateClass = DateClass()
    var payments: List<Payment> = listOf()

    constructor() {}

    constructor(
        id: String,
        number: Int,
        expectedDate: DateClass,
        payments: List<Payment>
    ) {
        this.id = id
        this.number = number
        this.expectedDate = expectedDate
        this.payments = payments
    }
}

class Payment {
    var id: String = ""
    var paidAmount: Double = 0.0
    var paidDate: DateClass = DateClass()
    var user: UserModel = UserModel()

    constructor() {}

    constructor(
        id: String,
        paidAmount: Double,
        paidDate: DateClass,
        user: UserModel
    ) {
        this.id = id
        this.paidAmount = paidAmount
        this.paidDate = paidDate
        this.user = user
    }
}
