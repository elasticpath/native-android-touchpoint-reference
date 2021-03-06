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
package com.optimusinfo.elasticpath.cortex.profile.address;

import org.apache.http.ParseException;
import org.json.JSONObject;

import com.optimusinfo.elasticpath.cortex.common.Constants;
import com.optimusinfo.elasticpath.cortex.common.Utils;

import android.os.AsyncTask;

/**
 * This class executes the add adress to profile
 * 
 * @author Optimus
 * 
 */
public class AsyncTaskAddAddress extends AsyncTask<Void, Void, Boolean> {

	protected String urlAddToCartForm;
	protected String contentTypeRequest;
	protected String accessToken;
	protected JSONObject mObjInput;

	protected ListenerAddAddress mListener;

	/**
	 * Initialize the Async Task
	 * 
	 * @param url
	 * @param input
	 * @param contentType
	 * @param token
	 */
	public AsyncTaskAddAddress(String url, String contentType, String token,
			JSONObject input, ListenerAddAddress listener) {
		urlAddToCartForm = url;
		contentTypeRequest = contentType;
		accessToken = token;
		mListener = listener;
		mObjInput = input;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		try {
			int responseCode = Utils.postData(urlAddToCartForm, mObjInput,
					accessToken, Constants.RequestHeaders.CONTENT_TYPE,
					Constants.RequestHeaders.AUTHORIZATION_INITIALIZER);
			if (responseCode == Constants.ApiResponseCode.REQUEST_SUCCESSFUL_CREATED
					|| responseCode == Constants.ApiResponseCode.REQUEST_SUCCESSFUL_UPDATED) {
				mListener.onTaskSuccessful(responseCode);
			} else {
				mListener.onTaskFailed(responseCode);
			}
			return true;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		if (result != null && !result) {
			mListener.onTaskFailed(Constants.ErrorCodes.ERROR_SERVER);
		}
	}
}
