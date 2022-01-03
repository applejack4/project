package com.medico.medko.View.Fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import com.medico.medko.Model.SliderItem
import com.medico.medko.Model.Token
import com.medico.medko.R
import com.medico.medko.View.Activities.SpecificDoctorList
import com.medico.medko.View.Adapters.SliderAdapter
import com.medico.medko.viewModel.HomeViewModel
import com.medico.medko.databinding.FragmentHomeBinding
import java.lang.Math.abs
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {

  private lateinit var homeViewModel: HomeViewModel
  private var _binding: FragmentHomeBinding? = null
    private lateinit var viewPager2: ViewPager2
    private val sliderHandler = Handler()
    private lateinit var token1 : String
    private lateinit var auth : String

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if(it.isComplete){
                token1 = it.result.toString()
            }
        }.addOnFailureListener {
            Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
        }

        val sdt = SimpleDateFormat("dd:MM:yyyy hh:mm:ss")
        val currentDate = sdt.format(Date())

        auth = FirebaseAuth.getInstance().currentUser?.uid.toString()

        val myRef = FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com/")
            .getReference("AppUsers").child("Users").child(auth)

        val friends: MutableList<String?> = ArrayList()

        myRef.child("Token").orderByChild("token").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                when (snapshot) {
                    snapshot -> {
                        if (snapshot.exists()) {
                            myRef.child("Token").orderByChild("token").equalTo(token1)
                                .addListenerForSingleValueEvent(object : ValueEventListener{
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if(snapshot.exists()){
                                            for (obj in snapshot.children){
                                                val friend : String = obj.child("token").value.toString()
                                                friends.add(friend)
                                            }
                                        }
                                        if(friends.contains(token1)){
                                            return
                                        }else{
                                            val token : Token = Token(token1)
                                            myRef.child("Token").child(currentDate).setValue(token)
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
                                    }

                                })
                        }
                        if (!snapshot.exists()) {
                            val token : Token = Token(token1)
                            myRef.child("Token").child(currentDate).setValue(token)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
            }

        })
    }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentHomeBinding.inflate(inflater, container, false)
    return _binding!!.root
  }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sliderItems : MutableList<SliderItem> = ArrayList()
        sliderItems.add(SliderItem(R.drawable.appointmentconfirmations))
        sliderItems.add(SliderItem(R.drawable.digitalrecord))
        sliderItems.add(SliderItem(R.drawable.doctorupdate))

        _binding?.Viewpager?.adapter = SliderAdapter(sliderItems, _binding!!.Viewpager)
        _binding?.Viewpager?.clipToPadding = false
        _binding?.Viewpager?.clipChildren = false
        _binding?.Viewpager?.offscreenPageLimit = 3
        _binding?.Viewpager?.getChildAt(0)?.overScrollMode = RecyclerView.OVER_SCROLL_ALWAYS

        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(30))
        compositePageTransformer.addTransformer{ page, position ->
            val r = 1 - abs(position)
            page.scaleY = (0.85 + r * 0.25f).toFloat()
        }

        _binding?.Viewpager?.setPageTransformer(compositePageTransformer)
        _binding?.Viewpager?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                sliderHandler.removeCallbacks(sliderRunnable)
                sliderHandler.postDelayed(sliderRunnable, 3000)
            }
        })
    }

    override fun onResume() {
        super.onResume()

        sliderHandler.postDelayed(sliderRunnable, 3000)

        _binding!!.historylll1.setOnClickListener {
            val intent = Intent(context, SpecificDoctorList::class.java)
            intent.putExtra("item", "My Doctors/Records")
            startActivity(intent)
        }

        _binding!!.Homeopathy.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, SpecificDoctorList::class.java)
            intent.putExtra("item", "Homeopathy")
            startActivity(intent)
        })

        _binding!!.CardiologistC.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, SpecificDoctorList::class.java)
            intent.putExtra("item", "Cardiologist")
            startActivity(intent)
        })

        _binding!!.Audiologist.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, SpecificDoctorList::class.java)
            intent.putExtra("item", "Audiologist")
            startActivity(intent)
        })

        _binding!!.Dentist.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, SpecificDoctorList::class.java)
            intent.putExtra("item", "Dentist")
            startActivity(intent)
        })

        _binding!!.ENTSpecialist.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, SpecificDoctorList::class.java)
            intent.putExtra("item", "ENT")
            startActivity(intent)
        })

        _binding!!.Gynaecologist.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, SpecificDoctorList::class.java)
            intent.putExtra("item", "Gynaecologist")
            startActivity(intent)
        })

        _binding!!.OrthopaedicSurgeon.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, SpecificDoctorList::class.java)
            intent.putExtra("item", "Orthopaedic Surgeon")
            startActivity(intent)
        })

        _binding!!.Paediatrician.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, SpecificDoctorList::class.java)
            intent.putExtra("item", "Paediatrician")
            startActivity(intent)
        })

        _binding!!.Psychiatrists.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, SpecificDoctorList::class.java)
            intent.putExtra("item", "Psychiatrist")
            startActivity(intent)
        })

        _binding!!.Veterinarian.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, SpecificDoctorList::class.java)
            intent.putExtra("item", "Veterinarian")
            startActivity(intent)
        })

        _binding!!.Radiologist.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, SpecificDoctorList::class.java)
            intent.putExtra("item", "Radiologist")
            startActivity(intent)
        })

        _binding!!.Pulmonologist.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, SpecificDoctorList::class.java)
            intent.putExtra("item", "Pulmonologist")
            startActivity(intent)
        })

        _binding!!.Endocrinologist.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, SpecificDoctorList::class.java)
            intent.putExtra("item", "Endocrinologist")
            startActivity(intent)
        })

        _binding!!.Oncologist.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, SpecificDoctorList::class.java)
            intent.putExtra("item", "Oncologist")
            startActivity(intent)
        })

        _binding!!.Neurologist.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, SpecificDoctorList::class.java)
            intent.putExtra("item", "Neurologist")
            startActivity(intent)
        })

        _binding!!.CardiothoracicSurgeon.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, SpecificDoctorList::class.java)
            intent.putExtra("item", "Cardiothoraric Surgeon")
            startActivity(intent)
        })

        _binding!!.Other.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, SpecificDoctorList::class.java)
            intent.putExtra("item", "Other")
            startActivity(intent)
        })
    }

    override fun onPause() {
        super.onPause()
        sliderHandler.postDelayed(sliderRunnable, 3000)
    }


    val sliderRunnable  = Runnable {
        _binding?.Viewpager?.currentItem = _binding?.Viewpager?.currentItem?.plus(1)!!
    }

override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}