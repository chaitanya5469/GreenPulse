package chaitu.android.greenpulse

data class DeviceData(
    val deviceId: String,
    val nickname: String,
    val recentTemp: Float,
    val lastSeen:Long,
    val recentHumidity: Float,
    val recentSoil: Float,
    val tempHistory: List<Pair<Long, Float>>,
    val humidityHistory: List<Pair<Long, Float>>,
    val soilHistory: List<Pair<Long, Float>>,
    val wateredTimestamps: List<Long>
)