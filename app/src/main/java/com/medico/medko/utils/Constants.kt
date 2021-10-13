package com.medico.medko.utils

object Constants {
    const val speciality : String = "Speciality"
    fun speciality():ArrayList<String> {
        val list = ArrayList<String>()
        list.add("Homeopathy")
        list.add("Cardiologist")
        list.add("Audiologist")
        list.add("Dentist")
        list.add("ENT")
        list.add("Gynaecologist")
        list.add("Orthopaedic surgeon")
        list.add("Paediatrician")
        list.add("Psychiatrists")
        list.add("Veterinarian")
        list.add("Radiologist")
        list.add("Pulmonologist")
        list.add("Endocrinologist")
        list.add("Oncologist")
        list.add("Neurologist")
        list.add("Cardiothoracic surgeon")
        list.add("Other")
        return list
    }
}