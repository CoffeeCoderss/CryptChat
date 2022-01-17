package com.coffeecoders.cryptchat;

import java.util.ArrayList;

public interface DataChangedListener {
    void newMessage(ArrayList<MessageModel> messagesList);
}
