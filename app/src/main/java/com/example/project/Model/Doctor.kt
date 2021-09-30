package com.example.project.Model

class Doctor (var id : String ?= null,
              var DoctorName : String ?= null,
              var ClinicName : String ?= null,
              var Email : String ?= null,
              var mobile : String ?= null,
              var Speciality : String ?= null,
              var password : String ?= null,
              var profilePicture : String ?= null,
              var appointmentStatus : String ?= null,
              var hospitalStatus : String?= null,
              var upiPay : String ?= null,
              val token : String ?= null)