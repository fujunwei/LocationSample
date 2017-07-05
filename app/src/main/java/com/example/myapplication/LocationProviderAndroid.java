// Copyright 2017 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package com.example.myapplication;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import java.util.Iterator;
import java.util.List;

/**
 * This is a LocationProvider using Android APIs [1]. It is a separate class for clarity
 * so that it can manage all processing completely on the UI thread. The container class
 * ensures that the start/stop calls into this class are done on the UI thread.
 *
 * [1] https://developer.android.com/reference/android/location/package-summary.html
 */
public class LocationProviderAndroid
        implements LocationListener {
    private static final String TAG = "cr_LocationProvider";

    private LocationManager mLocationManager;
    private Context mContext;

    LocationProviderAndroid(Context context) {
        mContext = context;
    }


    @Override
    public void onLocationChanged(Location location) {
        // Callbacks from the system location service are queued to this thread, so it's
        // possible that we receive callbacks after unregistering. At this point, the
        // native object will no longer exist.
        Log.d(TAG, "========location " + location.toString());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(TAG, "========onStatusChanged " + provider);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(TAG, "========onProviderEnabled " + provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(TAG, "========onProviderDisabled " + provider);
    }


    private void createLocationManagerIfNeeded() {
        if (mLocationManager != null) return;
        mLocationManager = (LocationManager) mContext.getSystemService(
                Context.LOCATION_SERVICE);
        if (mLocationManager == null) {
            Log.e(TAG, "Could not get location manager.");
        }

        List<String> providerList=mLocationManager.getProviders(true);
        for (Iterator<String> iterator = providerList.iterator(); iterator.hasNext();) {
            String provider = (String) iterator.next();
            Log.e(TAG, "==============provide " + provider);
        }
    }

    /**
     * Registers this object with the location service.
     */
    public void registerForLocationUpdates(boolean enableHighAccuracy) {
        createLocationManagerIfNeeded();

        try {
            Criteria criteria = new Criteria();
            if (enableHighAccuracy) criteria.setAccuracy(Criteria.ACCURACY_FINE);
//            mLocationManager.requestLocationUpdates(
//                    0, 0, criteria, this, Looper.getMainLooper());
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    0, 0, this);
        } catch (SecurityException e) {
            Log.e(TAG,
                    "Caught security exception while registering for location updates "
                            + "from the system. The application does not have sufficient "
                            + "geolocation permissions.");
            unregisterFromLocationUpdates();
            // Propagate an error to JavaScript, this can happen in case of WebView
            // when the embedding app does not have sufficient permissions.
            Log.d(TAG,
                    "application does not have sufficient geolocation permissions.");
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Caught IllegalArgumentException registering for location updates.");
            unregisterFromLocationUpdates();
            assert false;
        }
    }

    /**
     * Unregisters this object from the location service.
     */
    private void unregisterFromLocationUpdates() {
//        mLocationManager.removeUpdates(this);
    }
}
