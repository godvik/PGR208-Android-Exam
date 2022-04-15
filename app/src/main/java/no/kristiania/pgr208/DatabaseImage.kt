package no.kristiania.pgr208

import java.util.*

data class DatabaseImage(val id: Int = getAutoId(), val image: ByteArray) {
    companion object {
        fun getAutoId(): Int {
            val random = Random()
            return random.nextInt(100)
        }
    }
}
