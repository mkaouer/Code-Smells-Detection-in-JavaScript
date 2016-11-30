package com.nononsenseapps.helpers;

import com.nononsenseapps.notepad.NotePad;
import com.nononsenseapps.notepad.NotesEditorFragment;
import com.nononsenseapps.notepad.R;
import com.nononsenseapps.notepad.widget.ListWidgetProvider;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.net.Uri;

/**
 * The purpose here is to make it easy for other classes to notify that
 * something has changed in the database. Will also call update on the widgets
 * appropriately.
 * 
 */
public class UpdateNotifier {

	/**
	 * Will update all notes and specific uri if present
	 */
	public static void notifyChangeNote(Context context) {
		notifyChange(context, NotePad.Notes.CONTENT_VISIBLE_URI);
		updateWidgets(context);
	}

	/**
	 * Will update all notes and specific uri if present
	 * 
	 * @param uri
	 *            optional uri
	 */
	public static void notifyChangeNote(Context context, Uri uri) {
		notifyChange(context, uri);
		notifyChangeNote(context);
	}

	/**
	 * Will update all notes and specific uri if present
	 * 
	 * @param uri
	 *            optional uri
	 */
	public static void notifyChangeList(Context context) {
		notifyChange(context, NotePad.Lists.CONTENT_VISIBLE_URI);
		updateWidgets(context);
	}

	/**
	 * Will update all lists and specific uri if present
	 * 
	 * @param uri
	 *            optional uri
	 */
	public static void notifyChangeList(Context context, Uri uri) {
		notifyChange(context, uri);
		notifyChangeList(context);
	}

	/**
	 * Will update all lists and specific uri if present
	 * 
	 * @param uri
	 *            optional uri
	 */
	private static void notifyChange(Context context, Uri uri) {
		if (uri != null)
			context.getContentResolver().notifyChange(uri, null, false);
	}

	/**
	 * Instead of doing this in a service which might be killed, simply call
	 * this whenever something is changed in here
	 * 
	 * Update all widgets's views as this database has changed somehow
	 */
	private static void updateWidgets(Context context) {
		AppWidgetManager appWidgetManager = AppWidgetManager
				.getInstance(context);
		int[] appWidgetIds = appWidgetManager
				.getAppWidgetIds(new ComponentName(context,
						ListWidgetProvider.class));
		if (appWidgetIds.length > 0) {
			/*
			 * Tell the widgets that the list items should be invalidated and
			 * refreshed! Will call onDatasetChanged in ListWidgetService, doing
			 * a new requery
			 */
			appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds,
					R.id.notes_list);
		}
	}
}
