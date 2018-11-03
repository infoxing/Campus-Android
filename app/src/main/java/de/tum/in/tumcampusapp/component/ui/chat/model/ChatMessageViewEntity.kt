package de.tum.`in`.tumcampusapp.component.ui.chat.model

import android.content.Context
import de.tum.`in`.tumcampusapp.model.chat.ChatMember
import de.tum.`in`.tumcampusapp.model.chat.ChatMessage
import de.tum.`in`.tumcampusapp.core.DateTimeUtils
import org.joda.time.DateTime

data class ChatMessageViewEntity(
        val id: Int,
        val previous: Int,
        val room: Int,
        val text: String,
        val timestamp: DateTime,
        val formattedTimestamp: String,
        val signature: String,
        val member: ChatMember,
        val sendingStatus: Int,
        val raw: ChatMessage
) {

    companion object {

        @JvmStatic
        fun create(context: Context, message: ChatMessage): ChatMessageViewEntity {
            val timestamp = DateTimeUtils.getDateTime(message.timestamp)
            val formattedTimestamp = DateTimeUtils.formatTimeOrDay(timestamp, context)

            return ChatMessageViewEntity(
                    message.id, message.previous, message.room, message.text, timestamp,
                    formattedTimestamp, message.signature, message.member, message.sendingStatus, message
            )
        }

    }

}
