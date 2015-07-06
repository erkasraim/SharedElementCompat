package com.erkasraim.sharedelementcompat;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
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
public class SecondFragment extends SharedElementCompat.SharedElementFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_second, null);

        View view = rootView.findViewById(R.id.id_share2);
        SharedElementCompat.setTransitionName(view, "com.erkas.app.androidsample:shel");

        setEnterSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onSharedElementStart(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
                Log.e("second in", "onSharedElementStart");
            }

            @Override
            public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
                Log.e("second in", "onSharedElementEnd");
            }

            @Override
            public void onRejectSharedElements(List<View> rejectedSharedElements) {
                Log.e("second", "onRejectSharedElements");
            }

            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                Log.e("second", "onMapSharedElements");
            }

            @Override
            public Parcelable onCaptureSharedElementSnapshot(View sharedElement, Matrix viewToGlobalMatrix, RectF screenBounds) {
                Log.e("second in", "onCaptureSharedElementSnapshot");
                return super.onCaptureSharedElementSnapshot(sharedElement, viewToGlobalMatrix, screenBounds);
            }

            @Override
            public View onCreateSnapshotView(Context context, Parcelable snapshot) {
                Log.e("second in", "onCreateSnapshotView");
                return super.onCreateSnapshotView(context, snapshot);
            }
        });
        setExitSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onSharedElementStart(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
                Log.e("second out", "onSharedElementStart");
            }

            @Override
            public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
                Log.e("second out", "onSharedElementEnd");
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
