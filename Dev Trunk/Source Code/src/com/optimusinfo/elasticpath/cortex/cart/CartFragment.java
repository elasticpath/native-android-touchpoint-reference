package com.optimusinfo.elasticpath.cortex.cart;

import com.optimusinfo.elasticpath.cortexAPI.R;
import com.optimusinfo.elasticpath.cortex.authentication.AuthenticationActivity;
import com.optimusinfo.elasticpath.cortex.checkout.CheckoutFragment;
import com.optimusinfo.elasticpath.cortex.common.Constants;
import com.optimusinfo.elasticpath.cortex.common.EPFragment;
import com.optimusinfo.elasticpath.cortex.common.EPFragmentActivity;
import com.optimusinfo.elasticpath.cortex.common.NotificationUtils;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CartFragment extends EPFragment {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments representing each object in a collection. We use a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter} derivative,
	 * which will destroy and re-create fragments as needed, saving and
	 * restoring their state in the process. This is important to conserve
	 * memory and is a best practice when allowing navigation between objects in
	 * a potentially large collection.
	 */
	protected String mAddToCartUrl, mProductQuantity, mPurchaseUrl;
	protected String mCheckOutLink;

	/**
	 * Constructor
	 * 
	 * @param mAddToCartUrl
	 * @param mProductQuantity
	 */
	public CartFragment(String mAddToCartUrl, String mProductQuantity) {
		super();
		this.mAddToCartUrl = mAddToCartUrl;
		this.mProductQuantity = mProductQuantity;
	}

	// Request call listeners
	protected ListenerGetCartForm mGetCartFormListener;
	protected ListenerAddToCart mAddToCartListner;
	protected ListenerGetCompleteCartItems mCartItemsListener;
	protected ListenerDeleteCartItems mDeleteItemsListener;
	protected ListenerUpdateCartItems mUpdateCartItems;
	protected CartModel mObjCart;

	// Page views
	protected RelativeLayout mLayout;
	protected Button mBTCheckout, mBTUpdate;
	protected ListView mLVCart;
	protected View mViewParent;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View viewNavigation = inflater.inflate(R.layout.activity_cart,
				container, false);
		mViewParent = viewNavigation;
		// Initialize the view elements
		initializeViews();
		if (mObjCart != null) {
			mPurchaseUrl = mObjCart.getOrderitems()[0].getPurchaseForms()[0]
					.getPurchaseLinks().getHREF();
			mCheckOutLink = mObjCart.getOrderitems()[0].getSelf()
					.getCheckOutLink();
			setViewData();
		} else {
			if (mAddToCartUrl == null) {
				getCompleteCart();
			} else {
				// Perform the add to cart method
				getAddToCartForm();
			}
		}
		return viewNavigation;
	}

	/**
	 * This function fires gets the Add to cart form
	 */
	private void getAddToCartForm() {

		mGetCartFormListener = new ListenerGetCartForm() {
			@Override
			public void onTaskSuccessful(final AddToCartModel objResponse) {
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						getActivity().setProgressBarIndeterminateVisibility(
								false);
						if (objResponse.getAddToCartLinks() != null
								&& objResponse.getAddToCartLinks().length > 0) {
							postAddToCart(objResponse.getAddToCartLinks()[0].mHREF);
						} else {
							NotificationUtils.showErrorToast(getActivity(),
									Constants.ErrorCodes.ERROR_SERVER);
						}
					}
				});
			}

			@Override
			public void onTaskFailed(final int errorCode) {
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						getActivity().setProgressBarIndeterminateVisibility(
								false);
						// TODO - For Future Req
						NotificationUtils.showErrorToast(getActivity(),
								errorCode);
					}
				});
			}

			@Override
			public void onAuthenticationFailed() {
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						getActivity().setProgressBarIndeterminateVisibility(
								false);
						// TODO For Future Req
					}
				});
			}
		};
		getActivity().setProgressBarIndeterminateVisibility(true);
		mLayout.setVisibility(View.GONE);
		AddToCartModel.getAddToCartForm(getActivity(), mAddToCartUrl,
				getUserAuthenticationToken(), mGetCartFormListener);
	}

	/**
	 * This function posts the add to cart object
	 * 
	 * @param mPostUrl
	 */
	public void postAddToCart(String mPostUrl) {

		mAddToCartListner = new ListenerAddToCart() {
			@Override
			public void onTaskSuccessful(final int response) {
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						getActivity().setProgressBarIndeterminateVisibility(
								false);
						// get the complete cart list
						getCompleteCart();
					}
				});
			}

			@Override
			public void onTaskFailed(final int errorCode) {
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						getActivity().setProgressBarIndeterminateVisibility(
								false);
						// TODO - For Future Req
						NotificationUtils.showErrorToast(getActivity(),
								errorCode);
					}
				});
			}

			@Override
			public void onAuthenticationFailed() {
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						getActivity().setProgressBarIndeterminateVisibility(
								false);
						// TODO For Future Req
					}
				});
			}
		};
		int quantiy = 0;
		if (mProductQuantity != null) {
			quantiy = Integer.parseInt(mProductQuantity);
		} else {
			quantiy = 1;
		}
		getActivity().setProgressBarIndeterminateVisibility(true);
		CartModel.addToCart(quantiy, mPostUrl,
				Constants.Config.CONTENT_TYPE_ADD_TO_CART,
				getUserAuthenticationToken(), mAddToCartListner);
	}

	/**
	 * This function gets the complete items cart
	 */
	public void getCompleteCart() {

		mCartItemsListener = new ListenerGetCompleteCartItems() {
			@Override
			public void onTaskSuccessful(CartModel response) {

				mObjCart = response;
				mPurchaseUrl = response.getOrderitems()[0].getPurchaseForms()[0]
						.getPurchaseLinks().getHREF();
				mCheckOutLink = response.getOrderitems()[0].getSelf()
						.getCheckOutLink();

				// Pass control back to main thread
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						getActivity().setProgressBarIndeterminateVisibility(
								false);
						// populate the list view
						setViewData();
					}
				});
			}

			@Override
			public void onTaskFailed(final int errorCode) {
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						getActivity().setProgressBarIndeterminateVisibility(
								false);
						// TODO - For Future Req
						NotificationUtils.showErrorToast(getActivity(),
								errorCode);
					}
				});
			}

			@Override
			public void onAuthenticationFailed() {
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						getActivity().setProgressBarIndeterminateVisibility(
								false);
						// TODO For Future Req
					}
				});
			}
		};
		getActivity().setProgressBarIndeterminateVisibility(true);
		mLayout.setVisibility(View.INVISIBLE);
		String cartsUrl = getObjCortexParams().getEndpoint().concat("carts/")
				.concat(getObjCortexParams().getScope())
				.concat(Constants.ZoomUrl.URL_ZOOM_CART);
		CartModel.getCartItems(getActivity(), cartsUrl,
				getUserAuthenticationToken(), mCartItemsListener);
	}

	public void deleteCartItem(String url) {

		mDeleteItemsListener = new ListenerDeleteCartItems() {

			@Override
			public void onTaskSuccessful(final int response) {
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						getActivity().setProgressBarIndeterminateVisibility(
								false);
						// TODO For Future Req
						if (response == Constants.ApiResponseCode.REQUEST_SUCCESSFUL_DELETED) {
							getCompleteCart();
						}
						NotificationUtils.showNotificationToastFromResources(
								getActivity(),
								R.string.messageDeleteCartSuccessful);
					}
				});
			}

			@Override
			public void onTaskFailed(int errorCode) {
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						getActivity().setProgressBarIndeterminateVisibility(
								false);
					}
				});
			}

			@Override
			public void onAuthenticationFailed() {
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						getActivity().setProgressBarIndeterminateVisibility(
								false);
						// TODO For Future Req
					}
				});

			}
		};
		getActivity().setProgressBarIndeterminateVisibility(true);
		CartModel.deleteCartItems(getActivity(), url,
				getUserAuthenticationToken(), mDeleteItemsListener);
	}

	public void updateCartItem(String url, String quantity) {
		mUpdateCartItems = new ListenerUpdateCartItems() {
			@Override
			public void onTaskSuccessful(final int response) {
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						getActivity().setProgressBarIndeterminateVisibility(
								false);

					}
				});
			}

			@Override
			public void onTaskFailed(int errorCode) {
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						getActivity().setProgressBarIndeterminateVisibility(
								false);
						// TODO For Future Req
					}
				});
			}

			@Override
			public void onAuthenticationFailed() {
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						getActivity().setProgressBarIndeterminateVisibility(
								false);
						// TODO For Future Req
					}
				});
			}
		};

		getActivity().setProgressBarIndeterminateVisibility(true);
		CartModel.updateCartItems(getActivity(), url,
				getUserAuthenticationToken(), mUpdateCartItems, quantity);
	}

	/**
	 * 
	 * This function updates the views with data
	 * 
	 * @param objCart
	 *            - cart elements array
	 */
	private void setViewData() {
		mLayout.setVisibility(View.VISIBLE);

		if (mObjCart.mTotalQuantity.equalsIgnoreCase("0")) {
			mBTCheckout.setVisibility(View.VISIBLE);
			mBTCheckout.setEnabled(false);

			mBTUpdate.setVisibility(View.VISIBLE);
			mBTUpdate.setEnabled(false);

			mLVCart.setVisibility(View.INVISIBLE);

			NotificationUtils.showNotificationToastFromResources(getActivity(),
					R.string.messageEmptyCart);

		} else {
			mBTCheckout.setVisibility(View.VISIBLE);
			mBTCheckout.setEnabled(true);

			mBTUpdate.setVisibility(View.VISIBLE);
			mBTUpdate.setEnabled(true);

			CartAdapter mListAdapter = new CartAdapter(this,
					mObjCart.getLineItems()[0].getElements());
			mLVCart.setVisibility(View.VISIBLE);
			mLVCart.setAdapter(mListAdapter);
		}

		// Set the total quantity
		TextView tvTotalQuantity = (TextView) mViewParent
				.findViewById(R.id.tvCartHeaderQuantity);
		tvTotalQuantity.setText(mObjCart.getTotalQuantity().concat(" ")
				.concat(this.getString(R.string.prefixItems)));

		TextView tvOrderTotal = (TextView) mViewParent
				.findViewById(R.id.tvCartHeaderTotal);
		tvOrderTotal.setText(mObjCart.getCartTotal()[0].getCosts()[0]
				.getTotalCost());
		TextView tvCartFooterTotal = (TextView) mViewParent
				.findViewById(R.id.tvCartFooterTotal);
		tvCartFooterTotal.setText(mObjCart.getCartTotal()[0].getCosts()[0]
				.getTotalCost());

		TextView tvCartFooterOrderSubtotal = (TextView) mViewParent
				.findViewById(R.id.tvCartFooterOrderSubtotal);
		tvCartFooterOrderSubtotal
				.setText(mObjCart.getCartTotal()[0].getCosts()[0]
						.getTotalCost());

	}

	/**
	 * This method initializes the page view elements
	 */
	private void initializeViews() {
		mLayout = (RelativeLayout) mViewParent.findViewById(R.id.rlCart);
		// Initialize the purchase button
		mBTCheckout = (Button) mViewParent.findViewById(R.id.btCheckOut);
		mBTCheckout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isUserLoggedIn()) {
					CheckoutFragment objFragment = new CheckoutFragment(
							mCheckOutLink);
					addFragmentToBreadcrumb("Checkout",
							R.id.fragment_container, objFragment);
				} else {
					Intent mAuthIntent = new Intent(getActivity(),
							AuthenticationActivity.class);
					startActivityForResult(mAuthIntent,
							EPFragmentActivity.REQUEST_CODE_AUTHENTICATION);
				}

			}
		});
		mLVCart = (ListView) mViewParent.findViewById(R.id.lvCartItems);

		mBTUpdate = (Button) mViewParent.findViewById(R.id.btUpdateCart);
		mBTUpdate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onRefreshData();
			}
		});
	}

	@Override
	public void onRefreshData() {
		// TODO Auto-generated method stub
		super.onRefreshData();
		if (mAddToCartUrl == null) {
			getCompleteCart();
		} else {
			// Perform the add to cart method
			getAddToCartForm();
		}

	}

	@Override
	public void detachChildFragments() {
		// No Child fragments added

	}
}