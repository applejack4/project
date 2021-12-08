import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.medico.medko.Model.AppointConstructor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

class HistoryViewModel : ViewModel() {
    private val auth : String = FirebaseAuth.getInstance().currentUser?.uid.toString()

    private var firebaseDatabase : DatabaseReference = FirebaseDatabase.getInstance("https://trial-38785-default-rtdb.firebaseio.com/").getReference("AppUsers")

    private var  list : ArrayList<AppointConstructor> = arrayListOf()
    private var subitemlist : ArrayList<AppointConstructor> = arrayListOf()
    private var historyUpdateSize : ArrayList<AppointConstructor> = arrayListOf()
    private var num by Delegates.notNull<Int>()

    private val historyTitleList : MutableLiveData<ArrayList<AppointConstructor>> by lazy {
        MutableLiveData<ArrayList<AppointConstructor>>().also {
            val ref = firebaseDatabase.child("Doctor").child(auth).child("History")
            ref.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        list.clear()
                        for(obj in snapshot.children){
                            val dateModel = obj.getValue(AppointConstructor::class.java)
                            if(dateModel != null){
                                list.add(dateModel)
                            }else{
                                return
                            }
                        }
                        if(list.size > 0){
                            historyTitleList.value = list
                        }
                    }else{
                        list.clear()
                        historyTitleList.value = list
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.i("TAG","Error"+error.message)
                }
            })
        }
    }

    private val historySubItemList : MutableLiveData<ArrayList<AppointConstructor>> by lazy {
        MutableLiveData<ArrayList<AppointConstructor>>().also {
            val ref = firebaseDatabase.child("Doctor").child(auth).child("History")
            ref.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        subitemlist.clear()
                        for(obj in snapshot.children){
                            val subItem = obj.getValue(AppointConstructor::class.java)
                            if(subItem != null){
                                subitemlist.add(subItem)
                            }
                        }
                        if(subitemlist.size > 0){
                            historySubItemList.value = subitemlist
                        }
                    }else{
                        subitemlist.clear()
                        historySubItemList.value = subitemlist
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.i("TAG","Error"+error.message)
                }

            })
        }
    }

    private val listSize : MutableLiveData<ArrayList<AppointConstructor>> by lazy {
        MutableLiveData<ArrayList<AppointConstructor>>().also {
            val ref = firebaseDatabase.child("Doctor").child(auth).child("History")
            ref.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        historyUpdateSize.clear()
                        for(obj in snapshot.children){
                            val updateVal = obj.getValue(AppointConstructor::class.java)
                            if(updateVal != null){
                                historyUpdateSize.add(updateVal)
                            }
                        }
                        if(historyUpdateSize.size > 0){
                            listSize.value = historyUpdateSize
                        }
                    }else{
                        historyUpdateSize.clear()
                        listSize.value = historyUpdateSize
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.i("error", error.message)
                }

            })
        }
    }


    fun getTitleHistory() : LiveData<ArrayList<AppointConstructor>>{
        return historyTitleList
    }

    fun getSubItems() : LiveData<ArrayList<AppointConstructor>>{
        return historySubItemList
    }

    fun updateListSize() : LiveData<ArrayList<AppointConstructor>>{
        return listSize
    }
}

