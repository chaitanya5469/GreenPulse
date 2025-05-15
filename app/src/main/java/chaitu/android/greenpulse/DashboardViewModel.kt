package chaitu.android.greenpulse

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch



class DashboardViewModel : ViewModel() {


    private val _devices = MutableStateFlow<List<DeviceData>>(emptyList())
    val devices: StateFlow<List<DeviceData>> = _devices

    private val dbRef = FirebaseDatabase.getInstance().getReference("devices")

    fun loadDevices(context: Context) {
        val prefs = context.getSharedPreferences("registered_devices", Context.MODE_PRIVATE)
        val savedIds = prefs.getStringSet("device_ids", emptySet()) ?: emptySet()

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val result = mutableListOf<DeviceData>()

                for (deviceId in savedIds) {
                    val dataSnap = snapshot.child(deviceId).child("data")
                    val wateredSnap = dataSnap.child("lastWatered")


                    val tempMap = dataSnap.child("temperature").children.mapNotNull {
                        it.key?.toLongOrNull()?.let { ts ->
                            it.getValue(Float::class.java)?.let { value -> ts to value }
                        }
                    }.sortedBy { it.first }

                    val humidityMap = dataSnap.child("humidity").children.mapNotNull {
                        it.key?.toLongOrNull()?.let { ts ->
                            it.getValue(Float::class.java)?.let { value -> ts to value }
                        }
                    }.sortedBy { it.first }

                    val soilMap = dataSnap.child("soil").children.mapNotNull {
                        it.key?.toLongOrNull()?.let { ts ->
                            it.getValue(Float::class.java)?.let { value -> ts to value }
                        }
                    }.sortedBy { it.first }

                    val watered = wateredSnap.children.mapNotNull {
                        val key = it.key
                        val ts = key?.toLongOrNull()
                        if (ts == null) {
                            Log.w("WateredCheck", "Could not parse key $key as Long")
                        }
                        ts
                    }.sortedDescending()

                    val nickname = prefs.getString("nickname_$deviceId", deviceId) ?: deviceId
                    val recentTemp = tempMap.lastOrNull()?.second ?: 0f
                    val recentHumidity = humidityMap.lastOrNull()?.second ?: 0f
                    val recentSoil = soilMap.lastOrNull()?.second ?: 0f
                    val lastSeen= dataSnap.child("lastSeen").value?.toString()?.toLongOrNull()?:0

                    result.add(
                        DeviceData(
                            deviceId = deviceId,
                            nickname = nickname,
                            recentTemp = recentTemp,
                            lastSeen = lastSeen,
                            recentHumidity = recentHumidity,
                            recentSoil = recentSoil,
                            tempHistory = tempMap,
                            humidityHistory = humidityMap,
                            soilHistory = soilMap,
                            wateredTimestamps = watered
                        )
                    )
                }

                viewModelScope.launch {
                    _devices.emit(result)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun renameDevice(context: Context, deviceId: String, newName: String) {
        val prefs = context.getSharedPreferences("registered_devices", Context.MODE_PRIVATE)
        prefs.edit().putString("nickname_$deviceId", newName).apply()
        loadDevices(context)
    }

    fun deleteDevice(context: Context, deviceId: String) {
        val prefs = context.getSharedPreferences("registered_devices", Context.MODE_PRIVATE)
        val currentSet = prefs.getStringSet("device_ids", emptySet())?.toMutableSet() ?: return
        currentSet.remove(deviceId)
        prefs.edit()
            .putStringSet("device_ids", currentSet)
            .remove("nickname_$deviceId")
            .apply()
        loadDevices(context)
    }
}
