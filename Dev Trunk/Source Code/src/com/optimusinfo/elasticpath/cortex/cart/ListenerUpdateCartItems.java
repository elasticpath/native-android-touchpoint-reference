package com.optimusinfo.elasticpath.cortex.cart;

/**
 * This is the listener for Product Listing asynchronous task
 * 
 * @author Optimus
 * 
 */
public interface ListenerUpdateCartItems {

	void onTaskSuccessful(int response);

	void onTaskFailed(int errorCode);

	void onAuthenticationFailed();
}
