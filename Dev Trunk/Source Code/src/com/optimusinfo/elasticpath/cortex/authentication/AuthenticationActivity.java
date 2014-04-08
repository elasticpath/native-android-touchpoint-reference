/*
 * Copyright � 2014 Elastic Path Software Inc. All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.optimusinfo.elasticpath.cortex.authentication;

import com.optimusinfo.elasticpath.cortex.authentication.newaccount.ListenerAddUser;
import com.optimusinfo.elasticpath.cortex.authentication.newaccount.RegistrationModel;
import com.optimusinfo.elasticpath.cortex.common.Constants;
import com.optimusinfo.elasticpath.cortex.common.EPFragmentActivity;
import com.optimusinfo.elasticpath.cortex.common.NotificationUtils;
import com.optimusinfo.elasticpath.cortexAPI.R;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * This is the fragment to be opened when authentication is to be done
 * 
 * @author Optimus
 * 
 */
public class AuthenticationActivity extends EPFragmentActivity {

	protected ListenerAsyncTaskAuthentication mAuthListener;
	protected ListenerAddUser mNewAccountListener;
	protected EditText mTvUsername, mTvPassword;

	protected EditText mEtFirstname, mEtLastname, mEtUsername, mEtPassword;

	public void onCreate(Bundle savedInstanceState) {
		// default on create
		super.onCreate(savedInstanceState);
		// Set activity content
		setContentView(R.layout.fragment_authentication);
		super.initializeParams();
		// Disable the title
		mObjActionBar.setDisplayShowTitleEnabled(false);
		// Initialize views
		initializeViews();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * This function updates the views with data
	 */
	private void initializeViews() {
		// Set any progress bar to false
		setProgressBarIndeterminateVisibility(false);

		// Initialize login views
		mTvUsername = (EditText) findViewById(R.id.etUsername);
		mTvPassword = (EditText) findViewById(R.id.etPassword);

		Button btLogin = (Button) findViewById(R.id.btLogin);
		btLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!TextUtils.isEmpty(mTvUsername.getText().toString())
						&& !TextUtils.isEmpty(mTvPassword.getText().toString())) {
					getAuthenticationToken();
				} else {
					NotificationUtils.showNotificationToast(
							getApplicationContext(),
							"Please enter a username and password.");
				}
			}
		});

		// Initialize new account views
		mEtFirstname = (EditText) findViewById(R.id.etFirstName);
		mEtLastname = (EditText) findViewById(R.id.etLastName);
		mEtUsername = (EditText) findViewById(R.id.etRegisterUsername);
		mEtPassword = (EditText) findViewById(R.id.etRegisterPassword);

		Button btRegister = (Button) findViewById(R.id.btRegister);
		btRegister.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!TextUtils.isEmpty(mEtFirstname.getText().toString())
						&& !TextUtils.isEmpty(mEtLastname.getText().toString())
						&& !TextUtils.isEmpty(mEtUsername.getText().toString())
						&& !TextUtils.isEmpty(mEtPassword.getText().toString())) {
					addNewAccount();
				} else {
					NotificationUtils.showNotificationToast(
							getApplicationContext(),
							"Please enter all the fields.");
				}
			}
		});

	}

	/**
	 * This function creates an asynchronous task to get the public
	 * authentication token
	 */
	public void getAuthenticationToken() {

		mAuthListener = new ListenerAsyncTaskAuthentication() {

			@Override
			public void onTaskComplete(
					final Authentication authenticationResponse) {
				// Save the authentication token to application preferences
				mObjPreferences
						.edit()
						.putString(
								Constants.Preferences.KEY_ACCESS_TOKEN_REGISTERED,
								authenticationResponse.getAcessToken())
						.commit();

				mObjPreferences
						.edit()
						.putString(Constants.Preferences.KEY_USERNAME,
								mTvUsername.getText().toString()).commit();

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						setProgressBarIndeterminateVisibility(false);
						setResult(
								EPFragmentActivity.RESULT_CODE_AUTHENTICATION_SUCESSFUL,
								getIntent());
						finish();// finishing activity
					}
				});
			}

			@Override
			public void onTaskFailed(final int errorCode, final String response) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						setProgressBarIndeterminateVisibility(false);
						NotificationUtils.showNotificationToast(
								getApplicationContext(), errorCode + " - "
										+ "Incorrect username or password");
					}
				});
			}
		};

		setProgressBarIndeterminateVisibility(true);
		// get the user authentication and save it to shared preferences
		Authentication.getTokenFromServer(getApplicationContext(),
				mObjCortexParams.getEndpoint(), mAuthListener, mTvUsername
						.getText().toString(),
				mTvPassword.getText().toString(), mObjCortexParams.getScope(),
				"REGISTERED");

	}

	@Override
	public void onBackPressed() {
		// Incase user presses back button from top navigation
		setResult(0, getIntent());
		finish();// finishing activity
	}

	public void addNewAccount() {
		mNewAccountListener = new ListenerAddUser() {

			@Override
			public void onTaskSuccessful(int responseCode) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						setProgressBarIndeterminateVisibility(false);
						NotificationUtils.showNotificationToast(
								getApplicationContext(), "Success");
						mEtFirstname.getText().clear();
						mEtLastname.getText().clear();
						mEtUsername.getText().clear();
						mEtPassword.getText().clear();
					}
				});
			}

			@Override
			public void onTaskFailed(int errorCode) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						setProgressBarIndeterminateVisibility(false);

					}
				});

			}

			@Override
			public void onAuthenticationFailed() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						setProgressBarIndeterminateVisibility(false);

					}
				});

			}
		};
		setProgressBarIndeterminateVisibility(true);
		RegistrationModel.addNewAccount(getApplicationContext(),
				getnewAccountFormUrl(), getUserAuthenticationToken(),
				mNewAccountListener, RegistrationModel.getUserRegistrationJson(
						mEtFirstname.getText().toString(), mEtLastname
								.getText().toString(), mEtPassword.getText()
								.toString(), mEtUsername.getText().toString()));

	}

	/**
	 * This method retuens the new accunt address url
	 * 
	 * @return
	 */
	public String getnewAccountFormUrl() {
		String scope = mObjCortexParams.getScope();
		String url = mObjCortexParams.getEndpoint().concat("registrations/")
				.concat(scope).concat(Constants.ZoomUrl.URL_NEW_ACCOUNT);
		return url;
	}

}
