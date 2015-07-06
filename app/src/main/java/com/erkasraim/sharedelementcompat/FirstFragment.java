package com.erkasraim.sharedelementcompat;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.SharedElementCallback;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.Map;

/**
 * Created by erkas on 14. 11. 4..
 */
public class FirstFragment extends SharedElementCompat.SharedElementFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_first, null);

        final View element = rootView.findViewById(R.id.id_share);

//        element.setTransitionName("com.erkas.app.androidsample:srcview");
        SharedElementCompat.setTransitionName(element, "com.erkas.app.androidsample:srcview");
        element.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

//                fragmentTransaction.addSharedElement(element, "com.erkas.app.androidsample:shel");
                SharedElementCompat.addSharedElement(fragmentTransaction, element, "com.erkas.app.androidsample:shel");

                Fragment fragment = new SecondFragment();

//                fragment.setSharedElementEnterTransition(new ChangeBounds());
                SharedElementCompat.setSharedElementEnterTransitionChangeBounds(fragment);

                fragmentTransaction.replace(R.id.id_fragment_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                int[] viewLoc = new int[2];
                element.getLocationOnScreen(viewLoc);
            }
        });

        setEnterSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onSharedElementStart(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
                Log.e("first in", "onSharedElementStart");
            }

            @Override
            public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
                Log.e("first in", "onSharedElementEnd");
            }

            @Override
            public void onRejectSharedElements(List<View> rejectedSharedElements) {
                super.onRejectSharedElements(rejectedSharedElements);
            }

            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                super.onMapSharedElements(names, sharedElements);
            }

            @Override
            public Parcelable onCaptureSharedElementSnapshot(View sharedElement, Matrix viewToGlobalMatrix, RectF screenBounds) {
                return super.onCaptureSharedElementSnapshot(sharedElement, viewToGlobalMatrix, screenBounds);
            }

            @Override
            public View onCreateSnapshotView(Context context, Parcelable snapshot) {
                return super.onCreateSnapshotView(context, snapshot);
            }
        });
        setExitSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onSharedElementStart(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
                Log.e("first out", "onSharedElementStart");
            }

            @Override
            public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
                Log.e("first out", "onSharedElementEnd");
            }

            @Override
            public void onRejectSharedElements(List<View> rejectedSharedElements) {
                super.onRejectSharedElements(rejectedSharedElements);
            }

            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                super.onMapSharedElements(names, sharedElements);
            }

            @Override
            public Parcelable onCaptureSharedElementSnapshot(View sharedElement, Matrix viewToGlobalMatrix, RectF screenBounds) {
                return super.onCaptureSharedElementSnapshot(sharedElement, viewToGlobalMatrix, screenBounds);
            }

            @Override
            public View onCreateSnapshotView(Context context, Parcelable snapshot) {
                return super.onCreateSnapshotView(context, snapshot);
            }
        });

        return rootView;
    }
}
