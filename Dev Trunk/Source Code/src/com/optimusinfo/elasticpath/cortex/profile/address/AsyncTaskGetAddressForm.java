package com.optimusinfo.elasticpath.cortex.profile.address;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.optimusinfo.elasticpath.cortex.common.Constants;
import com.optimusinfo.elasticpath.cortex.common.Utils;
import com.optimusinfo.elasticpath.cortex.profile.address.AddressModel.CreateAddressFormModel;

import android.content.Context;
import android.os.AsyncTask;

/**
 * This class executes the get cart form request
 * 
 * @author Optimus
 * 
 */
public class AsyncTaskGetAddressForm extends AsyncTask<Void, Void, Boolean> {

	protected Context mCurrent;
	protected String mURL;
	protected String mAccessToken;
	protected String mHeaderContentTypeString;
	protected String mHeaderContentTypeValue;
	protected String mHeaderAuthorizationTypeString;
	protected String mHeaderAccessTokenInitializer;

	protected ListenerGetAddressForm mListener;

	/**
	 * Initialize the Async Task
	 * 
	 * @param url
	 * @param quantity
	 * @param contentType
	 * @param token
	 */
	public AsyncTaskGetAddressForm(Context current, String url, String token,
			String contentTypeString, String contentTypeValue,
			String authoriztionString, String accessTokenInitializer,
			ListenerGetAddressForm listener) {
		mCurrent = current;
		mURL = url;
		mAccessToken = token;
		mHeaderContentTypeString = contentTypeString;
		mHeaderContentTypeValue = contentTypeValue;
		mHeaderAuthorizationTypeString = authoriztionString;
		mHeaderAccessTokenInitializer = accessTokenInitializer;
		mListener = listener;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		try {
			if (Utils.isNetworkAvailable(mCurrent)) {
				String responseAddToCartForm = Utils.getJSONFromCortexUrl(mURL,
						mAccessToken, mHeaderContentTypeValue,
						mHeaderContentTypeString,
						mHeaderAuthorizationTypeString,
						mHeaderAccessTokenInitializer);
				if (responseAddToCartForm != null
						&& responseAddToCartForm.length() != 0) {
					if (0 == responseAddToCartForm
							.compareTo(Integer
									.toString(Constants.ApiResponseCode.UNAUTHORIZED_ACCESS))) {
						mListener.onAuthenticationFailed();
						return true;
					} else {
						mListener.onTaskSuccessful(new Gson().fromJson(
								responseAddToCartForm, CreateAddressFormModel.class));
						return true;
					}
				}
			} else {
				mListener.onTaskFailed(Constants.ErrorCodes.ERROR_NETWORK);
				return true;
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (JsonParseException e) {
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
