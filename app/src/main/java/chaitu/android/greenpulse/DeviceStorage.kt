package chaitu.android.greenpulse



import android.content.Context

object DeviceStorage {
    private const val PREF_NAME = "registered_devices"
    private const val KEY_DEVICES = "device_ids"
    private const val KEY_NICKNAME_PREFIX = "nickname_"

    fun saveDeviceId(context: Context, deviceId: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val currentSet = prefs.getStringSet(KEY_DEVICES, emptySet())?.toMutableSet() ?: mutableSetOf()
        currentSet.add(deviceId)
        prefs.edit().putStringSet(KEY_DEVICES, currentSet).apply()
    }

    fun getDeviceIds(context: Context): Set<String> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getStringSet(KEY_DEVICES, emptySet()) ?: emptySet()
    }



    fun hasRegisteredDevices(context: Context): Boolean {
        return getDeviceIds(context).isNotEmpty()

    }
}
