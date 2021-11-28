package com.medico.medko.utils

object Payment {

    const val pay : String = "Payment"

    fun pay() : ArrayList<String>{
        val list = ArrayList<String>()
        list.add("phone-pay")
        list.add("Google-pay")
        list.add("Paytm")
        list.add("Other")
        return list
    }
}