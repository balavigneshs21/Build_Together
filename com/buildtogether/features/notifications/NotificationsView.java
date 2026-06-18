package com.buildtogether.features.notifications;

import com.buildtogether.dto.Notification;
import com.buildtogether.dto.User;
import com.buildtogether.util.ConsoleInput;

import java.util.List;
import java.util.Scanner;

public class NotificationsView {

    private final NotificationsModel notificationsModel;
    private final User user;
    private final Scanner scanner;

    public NotificationsView(User user) {
        this.user = user;
        this.notificationsModel = new NotificationsModel(this);
        this.scanner = ConsoleInput.getScanner();
    }

    // ── called from HomeView — both Developer and Investor ─────────────────
    public void init() {
        System.out.println();
        System.out.println("=== My Notifications ===");
        notificationsModel.loadNotifications(user);
    }

    // =========================================================================
    // Callbacks — NotificationsModel calls these back
    // =========================================================================

    void showNotifications(List<Notification> notifications) {
        if (notifications.isEmpty()) {
            System.out.println("No notifications yet.");
            return;
        }

        // count unread
        long unread = 0;
        for (Notification n : notifications) {
            if (!n.isRead()) unread++;
        }

        System.out.println("Total: " + notifications.size()
                + "  |  Unread: " + unread);
        System.out.println("──────────────────────────────────────────");

        for (Notification n : notifications) {
            String status = n.isRead() ? "[READ]" : "[NEW] ";
            System.out.println(status + " " + n.getMessage());
            System.out.println("──────────────────────────────────────────");
        }

        // mark all as read after viewing
        notificationsModel.markAllRead(user);
        System.out.println();
        System.out.println("All notifications marked as read.");
    }

    void showError(String message) {
        System.out.println("Error: " + message);
    }
}
