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
package com.optimusinfo.elasticpath.cortex.product.listing;

import com.optimusinfo.elasticpath.cortex.cart.CartFragment;
import com.optimusinfo.elasticpath.cortex.common.Constants;
import com.optimusinfo.elasticpath.cortex.common.EPFragment;
import com.optimusinfo.elasticpath.cortex.common.imageutils.ImageLoader;
import com.optimusinfo.elasticpath.cortex.product.details.ProductDetailsFragment;
import com.optimusinfo.elasticpath.cortex.product.listing.ProductListing.ProductAddToCartForm;
import com.optimusinfo.elasticpath.cortex.product.listing.ProductListing.ProductAssets;
import com.optimusinfo.elasticpath.cortex.product.listing.ProductListing.ProductAvailability;
import com.optimusinfo.elasticpath.cortex.product.listing.ProductListing.ProductDefinition;
import com.optimusinfo.elasticpath.cortex.product.listing.ProductListing.ProductElement;
import com.optimusinfo.elasticpath.cortex.product.listing.ProductListing.ProductLinks;
import com.optimusinfo.elasticpath.cortex.product.listing.ProductListing.ProductPrice;
import com.optimusinfo.elasticpath.cortex.product.listing.ProductListing.ProductRates;
import com.optimusinfo.elasticpath.cortexAPI.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ProductListingAdapter extends BaseAdapter {

	private EPFragment mCurrent;
	protected ProductListing mProducts;
	private ImageLoader mImageLoader;

	public ProductListingAdapter(EPFragment mContext, ProductListing mProducts) {
		super();
		this.mCurrent = mContext;
		this.mProducts = mProducts;
		mImageLoader = new ImageLoader(mContext.getActivity());
	}

	@Override
	public int getCount() {
		if (mProducts.getElements() != null) {
			return mProducts.getElements().length;
		}
		return 0;
	}

	@Override
	public Object getItem(int pos) {
		if (mProducts.getElements() != null) {
			return mProducts.getElements()[pos];
		}
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) mCurrent.getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View gridView;

		final ProductElement mElement = mProducts.getElements()[pos];

		final ProductDefinition[] definition = mElement.getDefinition();
		ProductPrice[] price = mElement.getPrice();
		ProductRates[] rate = mElement.getRates();
		ProductAssets[] asset = definition[0].getProductAssets();
		ProductAvailability[] availability = mElement.getAvailability();

		final ProductAddToCartForm addToCartForm = mElement.getAddToCartForm()[0];
		final ProductLinks addToCartLinks = addToCartForm.getProductLinks();

		gridView = new View(mCurrent.getActivity());

		gridView = inflater.inflate(R.layout.item_list_product_definition,
				parent, false);

		// Set product title
		if (definition != null) {
			((TextView) gridView.findViewById(R.id.tvProductListingTitle))
					.setText(definition[0].getDisplayName());
		}

		// set product price
		if (price != null) {
			((TextView) gridView.findViewById(R.id.tvProductListingPrice))
					.setText(price[0].getProductPrice()[0].getDisplay());
		} else if (rate != null) {
			((TextView) gridView.findViewById(R.id.tvProductListingPrice))
					.setText(rate[0].getProductRates()[0].getRate());
		} else {
			((TextView) gridView.findViewById(R.id.tvProductListingPrice))
					.setText("N/A");
		}
		// set product image
		if (asset != null) {
			ImageView ivProduct = ((ImageView) gridView
					.findViewById(R.id.ivProductListing));
			ivProduct.setTag(asset[0].getProductImages()[0].getImageUrl());
			mImageLoader.displayRoundedCornersImage(
					asset[0].getProductImages()[0].getImageUrl(), ivProduct,
					true);
			
		}
		TextView btAddToCart = ((TextView) gridView
				.findViewById(R.id.btAddToCart));
		btAddToCart.setCompoundDrawablePadding(5);
		btAddToCart.setCompoundDrawablesWithIntrinsicBounds(
				R.drawable.icon_cart, 0, 0, 0);
		// set product availability
		if (availability != null) {
			String isAvailable = availability[0].getState();
			if (isAvailable.equalsIgnoreCase(Constants.STATE_AVAILABLE)) {
			} else {
				btAddToCart.setEnabled(false);
			}
		}

		gridView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mCurrent.getMainFragment().detachChildFragments();
				// Start the product details activity
				ProductDetailsFragment mObjFragment = new ProductDetailsFragment(
						mElement.getSelf().getHref());
				((EPFragment) mCurrent).addFragmentToBreadcrumb(
						definition[0].getDisplayName(),
						R.id.fragment_container, mObjFragment);
			}
		});

		if (addToCartLinks != null) {
			((TextView) gridView.findViewById(R.id.btAddToCart))
					.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							mCurrent.getMainFragment().detachChildFragments();
							CartFragment mObjFragment = new CartFragment(
									addToCartLinks.getHREF(), "1");
							((EPFragment) mCurrent).addFragmentToBreadcrumb(
									"Cart", R.id.fragment_container,
									mObjFragment);
						}
					});
		}
		return gridView;
	}

}
