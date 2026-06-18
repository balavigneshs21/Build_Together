package com.buildtogether.features.notifications;

import com.buildtogether.dto.Notification;
import com.buildtogether.dto.User;
import com.buildtogether.repository.BuildTogetherDB;

import java.util.List;

class NotificationsModel {

    private final NotificationsView notificationsView;

    NotificationsModel(NotificationsView notificationsView) {
        this.notificationsView = notificationsView;
    }

    // =========================================================================
    // Load all notifications for this user
    // =========================================================================

    void loadNotifications(User user) {
        if (user == null) {
            notificationsView.showError("User not found");
            return;
        }

        List<Notification> notifications = BuildTogetherDB.getInstance()
                .getNotificationsByUserId(user.getId());

        notificationsView.showNotifications(notifications);
    }

    // =========================================================================
    // Mark all notifications as read
    // =========================================================================

    void markAllRead(User user) {
        if (user == null) return;
        BuildTogetherDB.getInstance().markAllNotificationsRead(user.getId());
    }
}
