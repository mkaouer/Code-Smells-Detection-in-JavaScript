/*
 * Created on 19.03.2005
 */

package net.filebot.util.ui.notification;


import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import net.filebot.util.ui.SwingUI;


public class NotificationManager {

	private final NotificationLayout layout;


	public NotificationManager() {
		this(new QueueNotificationLayout());
	}


	public NotificationManager(NotificationLayout layout) {
		this.layout = layout;
	}


	public void show(NotificationWindow notification) {
		SwingUI.checkEventDispatchThread();

		notification.addWindowListener(new RemoveListener());
		layout.add(notification);

		notification.setVisible(true);
	}


	private class RemoveListener extends WindowAdapter {

		@Override
		public void windowClosing(WindowEvent e) {
			layout.remove((NotificationWindow) e.getWindow());
		}
	}

}
