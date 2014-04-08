package com.optimusinfo.elasticpath.cortex.profile;

import com.optimusinfo.elasticpath.cortexAPI.R;
import com.optimusinfo.elasticpath.cortex.profile.ProfileModel.PurchaseElement;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class PurchaseAdapter extends ArrayAdapter<PurchaseElement> {

	private Activity mCurrent;

	protected PurchaseElement[] mElements;

	public PurchaseAdapter(Activity current, PurchaseElement[] mElements) {
		super(current, R.layout.item_list_profile_purchase_desc, mElements);
		this.mCurrent = current;
		this.mElements = mElements;
	}

	@Override
	public int getCount() {
		return mElements.length;
	}

	@Override
	public PurchaseElement getItem(int position) {
		return mElements[position];
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mCurrent
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View listView;
		if (convertView == null) {
			listView = new View(mCurrent);
			// get layout from mobile.xml
			listView = inflater.inflate(
					R.layout.item_list_profile_purchase_desc, null);

			// set value into textview
			TextView textView = (TextView) listView
					.findViewById(R.id.tvOrderNumber);
			textView.setText(mElements[position].mOrderNumber);

			TextView tvOrderDate = (TextView) listView
					.findViewById(R.id.tvOrderDate);
			tvOrderDate
					.setText(mElements[position].mPurchaseDate.mDisplayValue);

			TextView tvOrderTotal = (TextView) listView
					.findViewById(R.id.tvOrderTotal);
			tvOrderTotal
					.setText(mElements[position].mPurchaseTotals[0].mDisplayValue);

			TextView tvOrderStatus = (TextView) listView
					.findViewById(R.id.tvOrderStatus);
			tvOrderStatus.setText(mElements[position].mStatus);

		} else {
			listView = (View) convertView;
		}
		return listView;
	}
}
