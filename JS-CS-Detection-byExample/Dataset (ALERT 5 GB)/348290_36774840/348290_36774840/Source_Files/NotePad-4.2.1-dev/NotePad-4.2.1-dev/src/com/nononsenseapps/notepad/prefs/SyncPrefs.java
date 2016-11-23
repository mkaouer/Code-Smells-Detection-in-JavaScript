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

package com.nononsenseapps.notepad.prefs;

import java.io.IOException;

import com.nononsenseapps.notepad.MainActivity;
import com.nononsenseapps.notepad.NotePad;
import com.nononsenseapps.notepad.R;
import com.nononsenseapps.notepad.sync.SyncAdapter;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import com.nononsenseapps.helpers.Log;

public class SyncPrefs extends PreferenceFragment implements
		OnSharedPreferenceChangeListener {

	public static final String KEY_SYNC_ENABLE = "syncEnablePref";
	public static final String KEY_ACCOUNT = "accountPref";
	public static final String KEY_SYNC_FREQ = "syncFreq";

	private Activity activity;

	private Preference prefAccount;
	private Preference prefSyncFreq;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.app_pref_sync);

		prefAccount = findPreference(KEY_ACCOUNT);
		prefSyncFreq = findPreference(KEY_SYNC_FREQ);

		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(activity);
		// Set up a listener whenever a key changes
		sharedPrefs.registerOnSharedPreferenceChangeListener(this);

		// Set summaries

		setAccountTitle(sharedPrefs);
		setFreqSummary(sharedPrefs);

		prefAccount
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					public boolean onPreferenceClick(Preference preference) {
						// Show dialog
						showAccountDialog();
						return true;
					}
				});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		PreferenceManager.getDefaultSharedPreferences(activity)
				.unregisterOnSharedPreferenceChangeListener(this);
	}

	private void showAccountDialog() {
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		Fragment prev = getFragmentManager().findFragmentByTag("accountdialog");
		if (prev != null) {
			ft.remove(prev);
		}
		ft.addToBackStack(null);

		// Create and show the dialog.
		// Bundle args = new Bundle();
		// args.putString(KEY_ACCOUNT, newPassword);
		DialogFragment newFragment = new AccountDialog();
		// newFragment.setArguments(args);
		newFragment.show(ft, "accountdialog");
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		try {
			Log.d("syncPrefs", "onChanged");
			if (activity.isFinishing()) {
				// Setting the summary now would crash it with
				// IllegalStateException since we are not attached to a view
			} else {
				if (KEY_SYNC_ENABLE.equals(key)) {
					toggleSync(sharedPreferences);
				} else if (KEY_SYNC_FREQ.equals(key)) {
					setSyncInterval(activity, sharedPreferences);
					setFreqSummary(sharedPreferences);
				} else if (KEY_ACCOUNT.equals(key)) {
					Log.d("syncPrefs", "account");
					prefAccount.setTitle(sharedPreferences.getString(
							KEY_ACCOUNT, ""));
				}
			}
		} catch (IllegalStateException e) {
			// This is just in case the "isFinishing" wouldn't be enough
			// The isFinishing will try to prevent us from doing something
			// stupid
			// This catch prevents the app from crashing if we do something
			// stupid
		}
	}

	/**
	 * Finds and returns the account of the name given
	 * 
	 * @param accountName
	 * @return
	 */
	public static Account getAccount(AccountManager manager, String accountName) {
		Account[] accounts = manager.getAccountsByType("com.google");
		for (Account account : accounts) {
			if (account.name.equals(accountName)) {
				return account;
			}
		}
		return null;
	}

	public static void setSyncInterval(Context activity,
			SharedPreferences sharedPreferences) {
		String accountName = sharedPreferences.getString(KEY_ACCOUNT, "");
		String sFreqMins = sharedPreferences.getString(KEY_SYNC_FREQ, "0");
		int freqMins = 0;
		try {
			freqMins = Integer.parseInt(sFreqMins);
		} catch (NumberFormatException e) {
			// Debugging error because of a mistake...
		}
		if (accountName == "") {
			// Something is very wrong if this happens
		} else if (freqMins == 0) {
			// Disable periodic syncing
			ContentResolver.removePeriodicSync(
					getAccount(AccountManager.get(activity), accountName),
					NotePad.AUTHORITY, new Bundle());
		} else {
			// Convert from minutes to seconds
			long pollFrequency = freqMins * 60;
			// Set periodic syncing
			ContentResolver.addPeriodicSync(
					getAccount(AccountManager.get(activity), accountName),
					NotePad.AUTHORITY, new Bundle(), pollFrequency);
		}
	}

	private void toggleSync(SharedPreferences sharedPreferences) {
		boolean enabled = sharedPreferences.getBoolean(KEY_SYNC_ENABLE, false);
		String accountName = sharedPreferences.getString(KEY_ACCOUNT, "");
		if (accountName.equals("")) {
			// do nothing yet
		} else if (enabled) {
			// set syncable
			ContentResolver.setIsSyncable(
					getAccount(AccountManager.get(activity), accountName),
					NotePad.AUTHORITY, 1);
			// Also set sync frequency
			setSyncInterval(activity, sharedPreferences);
		} else {
			// set unsyncable
			ContentResolver.setIsSyncable(
					getAccount(AccountManager.get(activity), accountName),
					NotePad.AUTHORITY, 0);
		}
	}

	private void setAccountTitle(SharedPreferences sharedPreferences) {
		prefAccount.setTitle(sharedPreferences.getString(KEY_ACCOUNT, ""));
		prefAccount.setSummary(R.string.settings_account_summary);
	}

	private void setFreqSummary(SharedPreferences sharedPreferences) {
		String sFreqMins = sharedPreferences.getString(KEY_SYNC_FREQ, "0");
		int freq = 0;
		try {
			freq = Integer.parseInt(sFreqMins);
		} catch (NumberFormatException e) {
			// Debugging error because of a mistake...
		}
		switch (freq) {
		case 0:
			prefSyncFreq.setSummary(R.string.manual);
			break;
		default:
			prefSyncFreq.setSummary(R.string.automatic);
			break;
		}
		// else if (freq == 60)
		// prefSyncFreq.setSummary(R.string.onehour);
		// else if (freq == 1440)
		// prefSyncFreq.setSummary(R.string.oneday);
		// else if (freq > 60)
		// prefSyncFreq.setSummary("" + freq/60 + " " +
		// getText(R.string.hours).toString());
		// else
		// prefSyncFreq.setSummary("" + freq + " " +
		// getText(R.string.minutes).toString());
	}

	public static class AccountDialog extends DialogFragment implements
			AccountManagerCallback<Bundle> {
		private Activity activity;
		private Account account;

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			this.activity = activity;
		}

		@Override
		public Dialog onCreateDialog(Bundle args) {
			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			builder.setTitle(R.string.select_account);
			final Account[] accounts = AccountManager.get(activity)
					.getAccountsByType("com.google");
			final int size = accounts.length;
			String[] names = new String[size];
			for (int i = 0; i < size; i++) {
				names[i] = accounts[i].name;
			}
			// TODO
			// Could add a clear alternative here
			builder.setItems(names, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// Stuff to do when the account is selected by the user
					accountSelected(accounts[which]);
				}
			});
			return builder.create();
		}

		/**
		 * Called from the activity, since that one builds the dialog
		 * 
		 * @param account
		 */
		public void accountSelected(Account account) {
			if (account != null) {
				Log.d("prefsActivity", "step one");
				this.account = account;
				// Request user's permission
				AccountManager.get(activity)
						.getAuthToken(account, SyncAdapter.AUTH_TOKEN_TYPE,
								null, activity, this, null);
				// work continues in callback, method run()
			}
		}

		/**
		 * User wants to select an account to sync with. If we get an approval,
		 * activate sync and set periodicity also.
		 */
		@Override
		public void run(AccountManagerFuture<Bundle> future) {
			try {
				Log.d("prefsActivity", "step two");
				// If the user has authorized
				// your application to use the
				// tasks API
				// a token is available.
				String token = future.getResult().getString(
						AccountManager.KEY_AUTHTOKEN);
				// Now we are authorized by the user.

				if (token != null && !token.equals("") && account != null) {
					Log.d("prefsActivity", "step three: " + account.name);
					SharedPreferences customSharedPreference = PreferenceManager
							.getDefaultSharedPreferences(activity);
					SharedPreferences.Editor editor = customSharedPreference
							.edit();
					editor.putString(SyncPrefs.KEY_ACCOUNT, account.name);
					editor.commit();

					// Set it syncable
					ContentResolver
							.setIsSyncable(account, NotePad.AUTHORITY, 1);
					// Set sync frequency
					SyncPrefs.setSyncInterval(activity, customSharedPreference);
				}
			} catch (OperationCanceledException e) {
				// if the request was canceled for any reason
			} catch (AuthenticatorException e) {
				// if there was an error communicating with the authenticator or
				// if the authenticator returned an invalid response
			} catch (IOException e) {
				// if the authenticator returned an error response that
				// indicates that it encountered an IOException while
				// communicating with the authentication server
			}

		}
	}
}