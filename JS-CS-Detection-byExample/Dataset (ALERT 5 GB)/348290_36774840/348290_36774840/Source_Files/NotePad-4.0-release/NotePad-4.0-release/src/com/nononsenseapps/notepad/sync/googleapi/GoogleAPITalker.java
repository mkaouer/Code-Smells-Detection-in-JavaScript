/*
 * Copyright (C) 2012 Jonas Kalderstam
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nononsenseapps.notepad.sync.googleapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Random;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.nononsenseapps.build.Config;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import com.nononsenseapps.helpers.Log;
import android.net.http.AndroidHttpClient; // Supports GZIP, apache's doesn't

/**
 * Helper class that sorts out all XML, JSON, HTTP bullshit for other classes.
 * Also keeps track of APIKEY and AuthToken
 * 
 * @author Jonas
 * 
 */
public class GoogleAPITalker {

	public static class PreconditionException extends Exception {
		private static final long serialVersionUID = 7317567246857384353L;

		public PreconditionException() {
		}

		public PreconditionException(String detailMessage) {
			super(detailMessage);
		}

		public PreconditionException(Throwable throwable) {
			super(throwable);
		}

		public PreconditionException(String detailMessage, Throwable throwable) {
			super(detailMessage, throwable);
		}

	}

	public static class NotModifiedException extends Exception {
		private static final long serialVersionUID = -6736829980184373286L;

		public NotModifiedException() {
		}

		public NotModifiedException(String detailMessage) {
			super(detailMessage);
		}

		public NotModifiedException(Throwable throwable) {
			super(throwable);
		}

		public NotModifiedException(String detailMessage, Throwable throwable) {
			super(detailMessage, throwable);
		}

	}

	public static Random rand = new Random();

	public static String AuthUrlEnd() {
		return "key=" + Config.GTASKS_API_KEY;
	}

	// public static final String AUTH_URL_END = "key=" + APIKEY;

	public static final String BASE_URL = "https://www.googleapis.com/tasks/v1/users/@me/lists";

	public static String AllLists() {
		return BASE_URL + "?" + AuthUrlEnd();
	}

	// public static final String ALL_LISTS = BASE_URL + "?" + AUTH_URL_END;

	public static String AllListsJustEtag() {
		return BASE_URL + "?fields=etag&" + AuthUrlEnd();
	}

	// public static final String ALL_LISTS_JUST_ETAG = BASE_URL +
	// "?fields=etag&"+ AUTH_URL_END;

	public static String ListURL(String id) {
		return BASE_URL + "/" + id + "?" + AuthUrlEnd();
	}

	public static final String LISTS = "/lists";
	public static final String BASE_TASK_URL = "https://www.googleapis.com/tasks/v1/lists";
	public static final String TASKS = "/tasks"; // Must be preceeded by

	// only retrieve the fields we will save in the database or use
	// https://www.googleapis.com/tasks/v1/lists/MDIwMzMwNjA0MjM5MzQ4MzIzMjU6MDow/tasks?showDeleted=true&showHidden=true&pp=1&key={YOUR_API_KEY}
	// updatedMin=2012-02-07T14%3A59%3A05.000Z
	private static String AllTasksInsert(String listId) {
		return BASE_TASK_URL + "/" + listId + TASKS + "?" + AuthUrlEnd();
	}

	private static String TaskURL(String taskId, String listId) {
		return BASE_TASK_URL + "/" + listId + TASKS + "/" + taskId + "?"
				+ AuthUrlEnd();
	}

	private static String TaskURL_ETAG_ID_UPDATED(final String taskId,
			final String listId) {
		String url = BASE_TASK_URL + "/" + listId + TASKS + "/" + taskId
				+ "?fields=id,etag,updated";
		url += ",position,parent&" + AuthUrlEnd();
		return url;
	}

	private static String TaskMoveURL_ETAG_UPDATED(final String taskId,
			final String listId, final String remoteparent,
			final String remoteprevious) {
		String url = BASE_TASK_URL + "/" + listId + TASKS + "/" + taskId
				+ "/move?";
		if (remoteparent != null && !remoteparent.isEmpty())
			url += "parent=" + remoteparent + "&";
		if (remoteprevious != null && !remoteprevious.isEmpty())
			url += "previous=" + remoteprevious + "&";
		url += "fields=etag,updated,position,parent&" + AuthUrlEnd();
		return url;
	}

	private static String AllTasksCompletedMin(String listId, String timestamp) {
		// showDeleted=true&showHidden=true
		// don't want deleted and hidden as this is the first sync
		if (timestamp == null)
			return BASE_TASK_URL + "/" + listId + TASKS + "?fields=items&"
					+ AuthUrlEnd();
		else {
			// In this case, we want deleted and hidden items in order to update
			// our own database
			try {
				return BASE_TASK_URL
						+ "/"
						+ listId
						+ TASKS
						+ "?showDeleted=true&showHidden=true&fields=items&updatedMin="
						+ URLEncoder.encode(timestamp, "UTF-8") + "&"
						+ AuthUrlEnd();
			} catch (UnsupportedEncodingException e) {

				Log.d(TAG, "Malformed timestamp: " + e.getLocalizedMessage());
				return BASE_TASK_URL + "/" + listId + TASKS
						+ "?showDeleted=true&showHidden=true&fields=items&"
						+ AuthUrlEnd();
			}
		}
	}

	// Tasks URL which inludes deleted tasks: /tasks?showDeleted=true
	// Also do showHidden=true?
	// Tasks returnerd will have deleted = true or no deleted field at all. Same
	// case for hidden.
	private static final String TAG = "GoogleAPITalker";

	private static final String USERAGENT = "HoloNotes (gzip)";

	// A URL is alwasy constructed as: BASE_URL + ["/" + LISTID [+ TASKS [+ "/"
	// + TASKID]]] + "?" + [POSSIBLE FIELDS + "&"] + AUTH_URL_END
	// Where each enclosing parenthesis is optional

	public String authToken;

	public AndroidHttpClient client;

	public static String getAuthToken(AccountManager accountManager,
			Account account, String authTokenType, boolean notifyAuthFailure) {

		Log.d(TAG, "getAuthToken");
		String authToken = "";
		try {
			// Might be invalid in the cache
			authToken = accountManager.blockingGetAuthToken(account,
					authTokenType, notifyAuthFailure);
			accountManager.invalidateAuthToken("com.google", authToken);

			authToken = accountManager.blockingGetAuthToken(account,
					authTokenType, notifyAuthFailure);
		} catch (OperationCanceledException e) {
		} catch (AuthenticatorException e) {
		} catch (IOException e) {
		}
		return authToken;
	}

	public boolean initialize(AccountManager accountManager, Account account,
			String authTokenType, boolean notifyAuthFailure) {

		Log.d(TAG, "initialize");
		// HttpParams params = new BasicHttpParams();
		// params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
		// HttpVersion.HTTP_1_1);
		// client = new AndroidHttpClientHttpClient(params);
		client = AndroidHttpClient.newInstance(USERAGENT);

		authToken = getAuthToken(accountManager, account, authTokenType,
				notifyAuthFailure);

		Log.d(TAG, "authToken: " + authToken);
		if (authToken != null && !authToken.equals("")) {
			return true;
		} else {
			return false;
		}
	}

	public void closeClient() {
		if (client != null) {
			client.close();
		}
	}

	/*
	 * User methods
	 */

	// This is not necessary unless I really want the etags of each list
	// public ArrayList<GoogleTaskList> getAllLists()
	// throws ClientProtocolException, JSONException,
	// PreconditionException, NotModifiedException, IOException {
	// ArrayList<GoogleTaskList> list = new ArrayList<GoogleTaskList>();
	//
	// // Lists will not carry etags, must fetch them individually
	// for (GoogleTaskList gimpedList : getListOfLists()) {
	// list.add(getList(gimpedList));
	// }
	//
	// return list;
	// }

	/**
	 * The entries in this does only one net-call, and such the list items do
	 * not contain e-tags. useful to get an id-list.
	 * 
	 * @return
	 * @throws IOException
	 * @throws NotModifiedException
	 * @throws PreconditionException
	 * @throws ClientProtocolException
	 */
	private String getListOfLists(ArrayList<GoogleTaskList> list)
			throws ClientProtocolException, IOException,
			PreconditionException {
		String eTag = "";
		HttpGet httpget = new HttpGet(AllLists());
		httpget.setHeader("Authorization", "OAuth " + authToken);

		// Log.d(TAG, "request: " + AllLists());
		AndroidHttpClient.modifyRequestToAcceptGzipResponse(httpget);

		try {
		JSONObject jsonResponse = (JSONObject) new JSONTokener(
				parseResponse(client.execute(httpget))).nextValue();

		// Log.d(TAG, jsonResponse.toString());

		eTag = jsonResponse.getString("etag");
		JSONArray lists = jsonResponse.getJSONArray("items");

		int size = lists.length();
		int i;

		// Lists will not carry etags, must fetch them individually if that
		// is desired
		for (i = 0; i < size; i++) {
			JSONObject jsonList = lists.getJSONObject(i);
			list.add(new GoogleTaskList(jsonList));
		}
		// } catch (PreconditionException e) {
		// // Can not happen in this case since we don't have any etag!
		// } catch (NotModifiedException e) {
		// // Can not happen in this case since we don't have any etag!
		// }
		} catch (JSONException e) {
			Log.d(TAG, "getlistoflists: " + e.getLocalizedMessage());
		}

		return eTag;
	}

	/**
	 * If etag is present, will make a if-none-match request. Expects only id
	 * and possibly etag to be present in the object
	 * 
	 * @param gimpedTask
	 * @return
	 * @throws IOException
	 * @throws NotModifiedException
	 * @throws JSONException
	 * @throws ClientProtocolException
	 * @throws PreconditionException
	 */
	public GoogleTask getTask(GoogleTask gimpedTask, GoogleTaskList list)
			throws ClientProtocolException, JSONException,
			NotModifiedException, IOException, PreconditionException {
		GoogleTask result = null;
		HttpGet httpget = new HttpGet(TaskURL(gimpedTask.id, list.id));
		setAuthHeader(httpget);
		// setHeaderWeakEtag(httpget, gimpedTask.etag);
		AndroidHttpClient.modifyRequestToAcceptGzipResponse(httpget);

		// Log.d(TAG, "request: " + TaskURL(gimpedTask.id, list.id));

		JSONObject jsonResponse = (JSONObject) new JSONTokener(
				parseResponse(client.execute(httpget))).nextValue();

		// Log.d(TAG, jsonResponse.toString());
		result = new GoogleTask(jsonResponse);
		// } catch (PreconditionException e) {
		// // Can not happen since we are not doing a PUT/POST
		// }

		result.listdbid = list.dbId;
		return result;
	}

	/**
	 * Takes a list object because the etag is optional here. If one is present,
	 * will make a if-none-match request.
	 * 
	 * @param gimpedList
	 * @return
	 * @throws ClientProtocolException
	 * @throws JSONException
	 * @throws PreconditionException
	 * @throws NotModifiedException
	 * @throws IOException
	 */
	public GoogleTaskList getList(GoogleTaskList gimpedList)
			throws ClientProtocolException, JSONException,
			NotModifiedException, IOException, PreconditionException {
		GoogleTaskList result = null;
		HttpGet httpget = new HttpGet(ListURL(gimpedList.id));
		setAuthHeader(httpget);
		// setHeaderWeakEtag(httpget, gimpedList.etag);
		AndroidHttpClient.modifyRequestToAcceptGzipResponse(httpget);

		// Log.d(TAG, "request: " + ListURL(gimpedList.id));

		JSONObject jsonResponse = (JSONObject) new JSONTokener(
				parseResponse(client.execute(httpget))).nextValue();

		// Log.d(TAG, jsonResponse.toString());
		result = new GoogleTaskList(jsonResponse);
		// } catch (PreconditionException e) {
		// // Can not happen since we are not doing a PUT/POST
		// }

		return result;
	}

	public String getEtag() throws ClientProtocolException, JSONException,
			IOException, PreconditionException {
		String eTag = "";
		HttpGet httpget = new HttpGet(AllListsJustEtag());
		httpget.setHeader("Authorization", "OAuth " + authToken);

		// Log.d(TAG, "request: " + AllLists());
		AndroidHttpClient.modifyRequestToAcceptGzipResponse(httpget);

		JSONObject jsonResponse = (JSONObject) new JSONTokener(
				parseResponse(client.execute(httpget))).nextValue();

		// Log.d(TAG, jsonResponse.toString());

		eTag = jsonResponse.getString("etag");

		// } catch (PreconditionException e) {
		// // Can not happen in this case since we don't have any etag!
		// } catch (NotModifiedException e) {
		// // Can not happen in this case since we don't have any etag!
		// }

		return eTag;
	}

	/**
	 * If the etag matches the one from Google, nothing has changed and an empty
	 * list is returned. Else, all lists are returned, but with the title value
	 * set from the appropriate source (db or remote). dbId is of course also
	 * set.
	 * 
	 * Also, because the API does not support deleted flags on lists, we have to
	 * compare with the local list to find missing (deleted) lists.
	 * 
	 * @throws IOException
	 * @throws JSONException
	 * @throws ClientProtocolException
	 * @throws PreconditionException
	 * 
	 */
	public String getModifiedLists(String lastEtag,
			ArrayList<GoogleTaskList> allLocalLists,
			ArrayList<GoogleTaskList> modifiedLists)
			throws ClientProtocolException, IOException {
		ArrayList<GoogleTaskList> allRemoteLists = new ArrayList<GoogleTaskList>();
		String newestEtag;
		try {
			newestEtag = getListOfLists(allRemoteLists);
		} catch (PreconditionException e) {
			// Can't happen
			Log.e(TAG, "Grossball error: " + e.getLocalizedMessage());
			newestEtag = null;
		} 
		if (newestEtag.equals(lastEtag)) {
			// Nothing has changed, don't do anything
			return newestEtag;
		} else {

			// Get list of lists
			@SuppressWarnings("unchecked")
			ArrayList<GoogleTaskList> deletedLists = (ArrayList<GoogleTaskList>) allLocalLists
					.clone();
			for (GoogleTaskList gimpedList : allRemoteLists) {
				boolean retrieved = false;
				for (GoogleTaskList localList : allLocalLists) {
					if (gimpedList.equals(localList)) {
						// Remove from list as well. any that remains do not
						// exist
						// on server, and hence should be deleted
						deletedLists.remove(localList);
						// Compare. If the list was modified locally, it wins
						// Otherwise, use remote info
						if (localList.modified != 1) {
							localList.title = gimpedList.title;
						}
						modifiedLists.add(localList);

						retrieved = true;
						break; // Break first loop, since we found it
					}
				}
				if (!retrieved) {
					// for new items, just get, and save
					modifiedLists.add(gimpedList);
				}
			}

			// Any lists that remain in the deletedLists, could not be
			// found on server. Hence
			// they must have been deleted. Set them as deleted and add to
			// modified
			// list.
			// This is though only true if it existed on the server in the first
			// place
			for (GoogleTaskList possiblyDeletedList : deletedLists) {
				if (possiblyDeletedList.id != null
						&& !possiblyDeletedList.id.isEmpty()) {
					possiblyDeletedList.deleted = 1;
					if (!modifiedLists.contains(possiblyDeletedList))
						modifiedLists.add(possiblyDeletedList);
				}
			}

			return newestEtag;
		}
	}

	/**
	 * Given a time, will fetch all tasks which were modified afterwards
	 * 
	 * @param googleTaskList
	 */
	public ArrayList<GoogleTask> getModifiedTasks(String lastUpdated,
			GoogleTaskList list) {
		ArrayList<GoogleTask> moddedList = new ArrayList<GoogleTask>();

		HttpGet httpget = new HttpGet(
				AllTasksCompletedMin(list.id, lastUpdated));
		setAuthHeader(httpget);
		AndroidHttpClient.modifyRequestToAcceptGzipResponse(httpget);

		// Log.d(TAG, httpget.getRequestLine().toString());
		for (Header header : httpget.getAllHeaders()) {

			Log.d(TAG, header.getName() + ": " + header.getValue());
		}

		String stringResponse;
		try {
			stringResponse = parseResponse(client.execute(httpget));

			JSONObject jsonResponse = new JSONObject(stringResponse);

			// Log.d(TAG, jsonResponse.toString());
			// Will be an array of items
			JSONArray items = jsonResponse.getJSONArray("items");

			int i;
			int length = items.length();
			for (i = 0; i < length; i++) {
				JSONObject jsonTask = items.getJSONObject(i);
				Log.d(TAG, "moddedJSONTask: " + jsonTask.toString());
				moddedList.add(new GoogleTask(jsonTask));
			}
		} catch (ClientProtocolException e) {

			Log.d(TAG, e.getLocalizedMessage());
		} catch (PreconditionException e) {
			// // Can't happen
			return null;
			// } catch (NotModifiedException e) {
			//
			// Log.d(TAG, e.getLocalizedMessage());
		} catch (IOException e) {

			Log.d(TAG, e.getLocalizedMessage());
		} catch (JSONException e) {
			// Item list must have been empty

			Log.d(TAG, e.getLocalizedMessage());
		}

		return moddedList;
	}

	/**
	 * Returns an object if all went well. Returns null if no upload was done.
	 * Will set only remote id, etag, position and parent fields.
	 * 
	 * Updates the task in place and also returns it.
	 * 
	 * @throws PreconditionException
	 */
	public GoogleTask uploadTask(final GoogleTask task,
			final GoogleTaskList pList) throws ClientProtocolException,
			IOException, PreconditionException {

		if (pList.id == null || pList.id.isEmpty()) {
			Log.d(TAG, "Invalid list ID found for uploadTask");
			return null; // Invalid list id
		}

		// If we are trying to upload a deleted task which does not exist on
		// server, we can ignore it. might happen with conflicts
		if (task.deleted == 1 && (task.id == null || task.id.isEmpty())) {
			Log.d(TAG, "Trying to upload a deleted non-synced note, ignoring: "
					+ task.title);
			return null;
		}

		HttpUriRequest httppost;
		if (task.id != null && !task.id.isEmpty()) {
			if (task.deleted == 1) {
				httppost = new HttpPost(TaskURL(task.id, pList.id));
				httppost.setHeader("X-HTTP-Method-Override", "DELETE");
			} else {
				httppost = new HttpPost(TaskURL_ETAG_ID_UPDATED(task.id,
						pList.id));
				// apache does not include PATCH requests, but we can force a
				// post to be a PATCH request
				httppost.setHeader("X-HTTP-Method-Override", "PATCH");
			}
			// Always set ETAGS for tasks
			setHeaderStrongEtag(httppost, task.etag);
		} else {
			if (task.deleted == 1) {
				return task; // Don't sync deleted items which do not exist on
								// the server
			}

			Log.d(TAG, "ID IS NULL: " + task.title);
			httppost = new HttpPost(AllTasksInsert(pList.id));
			task.didRemoteInsert = true; // Need this later
		}
		setAuthHeader(httppost);
		AndroidHttpClient.modifyRequestToAcceptGzipResponse(httppost);

		// Log.d(TAG, httppost.getRequestLine().toString());
		// for (Header header : httppost.getAllHeaders()) {
		// Log.d(TAG, header.getName() + ": " + header.getValue());
		// }

		if (task.deleted != 1) {
			setPostBody(httppost, task);
		}

		String stringResponse = parseResponse(client.execute(httppost));

		// If we deleted the note, we will get an empty response. Return the
		// same element back.
		if (task.deleted == 1) {

			Log.d(TAG, "deleted and Stringresponse: " + stringResponse);
		} else {
			JSONObject jsonResponse = null;

			try {
				jsonResponse = new JSONObject(stringResponse);

				// Log.d(TAG, jsonResponse.toString());

				// Will return a task, containing id and etag. always update
				// fields
				task.id = jsonResponse.getString(GoogleTask.ID);
				task.etag = jsonResponse.getString("etag");
				if (jsonResponse.has(GoogleTask.UPDATED))
					task.updated = jsonResponse.getString(GoogleTask.UPDATED);

			} catch (JSONException e) {
				Log.d(TAG, "" + jsonResponse + " " + e.getLocalizedMessage());
			}
		}

		return task;
	}

	/**
	 * Calls the GTasks API and tries to move a task to its target position.
	 * 
	 * @param task
	 * @throws ClientProtocolException
	 * @throws JSONException
	 * @throws IOException
	 * @throws PreconditionException
	 */
	// private void moveTask(final GoogleTask task, final GoogleTaskList pList)
	// throws ClientProtocolException, JSONException, IOException,
	// PreconditionException {
	//
	// if (pList.id == null || pList.id.isEmpty() || task.id == null
	// || task.id.isEmpty()) {
	// Log.d(TAG + ".move", "Invalid list ID found for uploadTask");
	// return;
	// }
	//
	// HttpUriRequest httppost = new HttpPost(TaskMoveURL_ETAG_UPDATED(
	// task.id, pList.id, task.parent, task.remoteprevious));
	//
	// setAuthHeader(httppost);
	// AndroidHttpClient.modifyRequestToAcceptGzipResponse(httppost);
	//
	// // No need since this is only done on sucessful updates
	// // setHeaderStrongEtag(httppost, task.etag);
	//
	// Log.d(TAG + ".move", httppost.getRequestLine().toString());
	// for (Header header : httppost.getAllHeaders()) {
	// Log.d(TAG + ".move", header.getName() + ": " + header.getValue());
	// }
	//
	// String stringResponse = parseResponse(client.execute(httppost));
	//
	// JSONObject jsonResponse = new JSONObject(stringResponse);
	//
	// Log.d(TAG + ".move", jsonResponse.toString());
	//
	// // Will return a task, containing id and etag. always update
	// // fields
	// task.etag = jsonResponse.getString("etag");
	// if (jsonResponse.has(GoogleTask.UPDATED))
	// task.updated = jsonResponse.getString(GoogleTask.UPDATED);
	// task.moveUploaded = true;
	// if (jsonResponse.has(GoogleTask.PARENT)) {
	// task.remoteparent = jsonResponse.getString(GoogleTask.PARENT);
	// Log.d(TAG + ".move", jsonResponse.getString(GoogleTask.PARENT));
	// } else
	// task.remoteparent = null;
	// if (jsonResponse.has(GoogleTask.POSITION)) {
	// task.position = jsonResponse.getString(GoogleTask.POSITION);
	// Log.d(TAG + ".move", jsonResponse.getString(GoogleTask.POSITION));
	// } else
	// task.position = null;
	// }

	/**
	 * Returns an object if all went well. Returns null if a conflict was
	 * detected. If the list has deleted set to 1, will call the server and
	 * delete the list instead of updating it.
	 * 
	 * @throws IOException
	 * @throws NotModifiedException
	 * @throws PreconditionException
	 * @throws JSONException
	 * @throws ClientProtocolException
	 * @throws DefaultListDeleted
	 */
	public GoogleTaskList uploadList(final GoogleTaskList list)
			throws ClientProtocolException, IOException, PreconditionException {
		HttpUriRequest httppost;
		if (list.id != null) {

			Log.d(TAG, "ID is not NULL!! " + ListURL(list.id));
			if (list.deleted == 1) {
				httppost = new HttpDelete(ListURL(list.id));
				// Delete is the only case for lists where we really
				// care about conflicts
				setHeaderStrongEtag(httppost, list.etag);
			} else {
				httppost = new HttpPost(ListURL(list.id));
				// apache does not include PATCH requests, but we can force a
				// post to be a PATCH request
				httppost.setHeader("X-HTTP-Method-Override", "PATCH");
			}
		} else {
			if (list.deleted == 1) {
				return list; // Don't sync deleted items which do not exist on
								// the server
			}

			Log.d(TAG, "Fetching lists with: " + AllLists());
			httppost = new HttpPost(AllLists());
			list.didRemoteInsert = true; // Need this later
		}
		setAuthHeader(httppost);
		AndroidHttpClient.modifyRequestToAcceptGzipResponse(httppost);

		// Log.d(TAG, httppost.getRequestLine().toString());
		// for (Header header : httppost.getAllHeaders()) {
		// Log.d(TAG, header.getName() + ": " + header.getValue());
		// }

		if (list.deleted != 1) {
			setPostBody(httppost, list);
		}

		String stringResponse;
		stringResponse = parseResponse(client.execute(httppost));

		// If we deleted the note, we will get an empty response. Return the
		// same element back.
		if (list.deleted == 1) {

			Log.d(TAG, "deleted and Stringresponse: " + stringResponse);
		} else {
			JSONObject jsonResponse = null;
			try {
				jsonResponse = new JSONObject(stringResponse);

				// Log.d(TAG, jsonResponse.toString());

				// Will return a list, containing id and etag. always update
				// fields
				list.etag = jsonResponse.getString("etag");
				list.id = jsonResponse.getString("id");
				list.title = jsonResponse.getString("title");
			} catch (JSONException e) {
				Log.d(TAG, "" + jsonResponse + " " + e.getLocalizedMessage());
			}
		}

		return list;
	}

	/*
	 * Communication methods
	 */

	/**
	 * Sets the authorization header
	 * 
	 * @param url
	 * @return
	 */
	private void setAuthHeader(HttpUriRequest request) {
		if (request != null)
			request.setHeader("Authorization", "OAuth " + authToken);
	}

	/**
	 * Does nothing if etag is null or "" Sets an if-match header for strong
	 * etag comparisons.
	 * 
	 * @param etag
	 */
	private void setHeaderStrongEtag(final HttpUriRequest httppost,
			final String etag) {
		if (etag != null && !etag.isEmpty()) {
			httppost.setHeader("If-Match", etag);

			Log.d(TAG, "If-Match: " + etag);
		} else {
			Log.d(TAG, "No ETAG could be found!");
		}
	}

	/**
	 * Does nothing if etag is null or "" Sets an if-none-match header for weak
	 * etag comparisons.
	 * 
	 * If-None-Match: W/"D08FQn8-eil7ImA9WxZbFEw."
	 * 
	 * @param etag
	 */
	private void setHeaderWeakEtag(HttpUriRequest httpget, String etag) {
		if (etag != null && !etag.equals("")) {
			httpget.setHeader("If-None-Match", etag);

			Log.d(TAG, "If-None-Match: " + etag);
		}
	}

	/**
	 * SUpports Post and Put. Anything else will not have any effect
	 * 
	 * @param httppost
	 * @param list
	 */
	private void setPostBody(HttpUriRequest httppost, GoogleTaskList list) {
		StringEntity se = null;
		try {
			se = new StringEntity(list.toJSON(), HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		se.setContentType("application/json");
		if (httppost instanceof HttpPost)
			((HttpPost) httppost).setEntity(se);
		else if (httppost instanceof HttpPut)
			((HttpPut) httppost).setEntity(se);
	}

	/**
	 * SUpports Post and Put. Anything else will not have any effect
	 * 
	 * @param httppost
	 * @param list
	 */
	private void setPostBody(HttpUriRequest httppost, GoogleTask task) {
		StringEntity se = null;
		try {
			se = new StringEntity(task.toJSON(), HTTP.UTF_8);
			// Log.d(TAG + ".move", "Sending: " + task.toJSON());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		se.setContentType("application/json");
		if (httppost instanceof HttpPost)
			((HttpPost) httppost).setEntity(se);
		else if (httppost instanceof HttpPut)
			((HttpPut) httppost).setEntity(se);
	}

	/**
	 * Parses a httpresponse and returns the string body of it. Throws
	 * exceptions for select status codes.
	 * 
	 * @throws PreconditionException
	 * @throws DefaultListDeleted
	 */
	public static String parseResponse(HttpResponse response)
			throws ClientProtocolException, PreconditionException {
		String page = "";
		BufferedReader in = null;

		Log.d(TAG, "HTTP Response Code: "
				+ response.getStatusLine().getStatusCode());

		if (response.getStatusLine().getStatusCode() == 403) {
			// Invalid authtoken
			throw new ClientProtocolException("Status: 403, Invalid authcode");
		}

		else if (response.getStatusLine().getStatusCode() == 412) { //
			/*
			 * Precondition failed. Object has been modified on server, can't do
			 * update
			 */
			throw new PreconditionException(
					"Etags don't match, can not perform update. Resolve the conflict then update without etag");
		}

		/*
		 * else if (response.getStatusLine().getStatusCode() == 304) { throw new
		 * NotModifiedException(); }
		 */
		else if (response.getStatusLine().getStatusCode() == 400) {
			// Warning: can happen for a legitimate case
			// This happens if you try to delete the default list.
			// Resolv it by considering the delete successful. List will still
			// exist on server, but all tasks will be deleted from it.
			// A successful delete returns an empty response.
			// Make a log entry about it anyway though
			Log.d(TAG,
					"Response was 400. Either we deleted the default list in app or did something really bad");
			throw new PreconditionException(
					"Tried to delete default list, undelete it");
		} else if (response.getStatusLine().getStatusCode() == 204) {
			// Successful delete of a tasklist. return empty string as that is
			// expected from delete

			Log.d(TAG, "Response was 204: Successful delete");
			return "";
		} else {

			try {
				if (response.getEntity() != null) {
					// Only call getContent ONCE
					InputStream content = AndroidHttpClient
							.getUngzippedContent(response.getEntity());
					if (content != null) {
						in = new BufferedReader(new InputStreamReader(content));
						StringBuffer sb = new StringBuffer("");
						String line = "";
						String NL = System.getProperty("line.separator");
						while ((line = in.readLine()) != null) {
							sb.append(line + NL);
						}
						in.close();
						page = sb.toString();
						//
						// System.out.println(page);
					}
				}
			} catch (IOException e) {
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		return page;
	}
}
