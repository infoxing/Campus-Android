package de.tum.in.tumcampusapp.component.ui.chat;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import de.tum.in.tumcampusapp.R;
import de.tum.in.tumcampusapp.api.app.TUMCabeClient;
import de.tum.in.tumcampusapp.api.app.model.TUMCabeVerification;
import de.tum.in.tumcampusapp.component.other.generic.GenericNotification;
import de.tum.in.tumcampusapp.component.ui.chat.activity.ChatActivity;
import de.tum.in.tumcampusapp.component.ui.chat.activity.ChatRoomsActivity;
import de.tum.in.tumcampusapp.database.daos.ChatMessageDao;
import de.tum.in.tumcampusapp.model.chat.ChatMessage;
import de.tum.in.tumcampusapp.model.chat.ChatRoom;
import de.tum.in.tumcampusapp.component.ui.chat.repository.ChatMessageLocalRepository;
import de.tum.in.tumcampusapp.component.ui.chat.repository.ChatMessageRemoteRepository;
import de.tum.in.tumcampusapp.component.ui.overview.CardManager;
import de.tum.in.tumcampusapp.component.ui.overview.MainActivity;
import de.tum.in.tumcampusapp.database.TcaDb;
import de.tum.in.tumcampusapp.core.Const;
import de.tum.in.tumcampusapp.core.Utils;

/**
 * Creates/modifies the notification when there is a new chat message.
 */
public class ChatNotification extends GenericNotification {

    private static final int NOTIFICATION_ID = CardManager.CARD_CHAT;

    private final FcmChat extras;

    private ChatRoom chatRoom;
    private String notificationText;
    private TaskStackBuilder sBuilder;

    private final ChatMessageDao chatMessageDao;

    public ChatNotification(Bundle extras, Context context, int notification) {
        super(context, 1, notification, true);
        chatMessageDao = TcaDb.getInstance(context)
                              .chatMessageDao();

        //Initialize the object keeping important infos about the update
        this.extras = new FcmChat();

        //Get the update details
        this.extras.setRoom(Integer.parseInt(extras.getString("room")));
        this.extras.setMember(Integer.parseInt(extras.getString("member")));

        //Message part is only present if we have a updated message
         if (extras.containsKey("message")) {
            this.extras.setMessage(Integer.parseInt(extras.getString("message")));
        } else {
            this.extras.setMessage(-1);
        }

        try {
            this.prepare();
        } catch (IOException e) {
            Utils.log(e);
        }
    }

    public ChatNotification(String payload, Context context, int notification) {
        super(context, 1, notification, true);
        chatMessageDao = TcaDb.getInstance(context)
                              .chatMessageDao();

        //Check if a payload was passed
        if (payload == null) {
            throw new IllegalArgumentException();
        }

        // parse data
        this.extras = new Gson().fromJson(payload, FcmChat.class);

        try {
            this.prepare();
        } catch (IOException e) {
            Utils.log(e);
        }
    }

    private void prepare() throws IOException {
        Utils.logv("Received GCM notification: room=" + this.extras.getRoom()
                + " member=" + this.extras.getMember() + " message=" + this.extras.getMessage());

        // Get the data necessary for the ChatActivity
        chatRoom = TUMCabeClient.getInstance(context)
                                .getChatRoom(this.extras.getRoom());

        getNewMessages(chatRoom, extras.getMessage());
    }

    @SuppressLint("CheckResult")
    private void getNewMessages(ChatRoom chatRoom, int messageId) {
        ChatMessageLocalRepository localRepository = ChatMessageLocalRepository.INSTANCE;
        localRepository.setDb(TcaDb.getInstance(context));

        ChatMessageRemoteRepository remoteRepository = ChatMessageRemoteRepository.INSTANCE;
        remoteRepository.setTumCabeClient(TUMCabeClient.getInstance(context));

        ChatMessageViewModel chatMessageViewModel =
                new ChatMessageViewModel(localRepository, remoteRepository);

        TUMCabeVerification verification = TUMCabeVerification.create(context, null);
        if (verification == null) {
            return;
        }

        if (messageId == -1) {
            chatMessageViewModel
                    .getNewMessages(chatRoom, verification)
                    .subscribe(chatMessages -> onDataLoaded(), Utils::log);
        } else {
            chatMessageViewModel
                    .getOlderMessages(chatRoom, messageId, verification)
                    .subscribe(chatMessages -> {
                        // Free ad space
                    }, Utils::log);
        }
    }

    private void onDataLoaded() {
        List<ChatMessage> messages = chatMessageDao.getLastUnread(chatRoom.getId());
        Collections.reverse(messages);
        Intent intent = new Intent(Const.CHAT_BROADCAST_NAME);
        intent.putExtra("FcmChat", this.extras);
        LocalBroadcastManager.getInstance(context)
                             .sendBroadcast(intent);
        notificationText = null;
        if (messages != null) {
            for (ChatMessage msg : messages) {
                if (notificationText == null) {
                    notificationText = msg.getText();
                } else {
                    notificationText += "\n" + msg.getText();
                }
            }
        }
        // Put the data into the intent
        Intent notificationIntent = new Intent(context, ChatActivity.class);
        notificationIntent.putExtra(Const.CURRENT_CHAT_ROOM, new Gson().toJson(chatRoom));

        sBuilder = TaskStackBuilder.create(context);
        sBuilder.addNextIntent(new Intent(context, MainActivity.class));
        sBuilder.addNextIntent(new Intent(context, ChatRoomsActivity.class));
        sBuilder.addNextIntent(notificationIntent);

        showNotification();
    }

    private void showNotification() {
        //Check if chat is currently open then don't show a notification if it is
        if (ChatActivity.mCurrentOpenChatRoom != null && this.extras.getRoom() == ChatActivity.mCurrentOpenChatRoom.getId()) {
            return;
        }

        if (Utils.getSettingBool(context, "card_chat_phone", true) && this.extras.getMessage() == -1) {

            PendingIntent contentIntent = sBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

            // FcmNotification sound
            Uri sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.message);

            /* TODO(jacqueline8711): Create the reply action and add the remote input

            String replyLabel = context.getResources().getString(R.string.reply_label);

            RemoteInput remoteInput = new RemoteInput.Builder(ChatActivity.EXTRA_VOICE_REPLY)
                    .setLabel(replyLabel)
                    .build();

            NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.ic_reply,
                                                                                    context.getString(R.string.reply_label),
                                                                                    contentIntent)
                            .addRemoteInput(remoteInput)
                            .build();*/

            //Create a nice notification
            Notification n = new NotificationCompat.Builder(context, Const.NOTIFICATION_CHANNEL_CHAT)
                    .setSmallIcon(this.icon)
                    .setLargeIcon(Utils.getLargeIcon(context, R.drawable.ic_chat_with_lines))
                    .setContentTitle(chatRoom.getName().substring(4))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationText))
                    .setContentText(notificationText)
                    .setContentIntent(contentIntent)
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setLights(0xff0000ff, 500, 500)
                    .setSound(sound)
                    .setAutoCancel(true)
                    .setColor(ContextCompat.getColor(context, R.color.color_primary))
                    .build();

            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(getNotificationIdentification(), n);

        }
    }

    @Override
    public Notification getNotification() {
        return null; // we're showing the notification in the class itself because we have to load data first
    }

    @Override
    public int getNotificationIdentification() {
        return (this.extras.getRoom() << 4) + ChatNotification.NOTIFICATION_ID;
    }
}
