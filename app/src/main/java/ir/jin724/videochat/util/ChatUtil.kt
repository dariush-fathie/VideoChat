package ir.jin724.videochat.util

import ir.jin724.videochat.data.userRepository.User
import kotlin.random.Random

object ChatUtil {

    fun generateTempChatItemId(me: User): Int {
        return Random.nextInt(9999, 999999999)
    }

}