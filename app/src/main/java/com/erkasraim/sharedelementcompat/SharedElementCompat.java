package com.erkasraim.sharedelementcompat;

import android.annotation.TargetApi;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.transition.ChangeBounds;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by erkas on 15. 6. 29..
 */
public class SharedElementCompat {

    private static boolean isLollipop;
    private static SharedElementManager sharedElementManager;

    public static String TRANSIT_TYPE = "transit_type";
    public static final int TRANSIT_CHANGE_BOUNDS = 100;

    static {
        final int version = android.os.Build.VERSION.SDK_INT;
        if (version >= 21) {
            isLollipop = true;
        } else {
            sharedElementManager = new SharedElementManager();
        }
    }

    public static void addSharedElement(FragmentTransaction fragmentTransaction, View src, String targetTransitionName) {
        if (isLollipop) {
            fragmentTransaction.addSharedElement(src, targetTransitionName);
        } else {
            // 1. fragment 간 transition 시에 대상이 되는 View의 정보를 저장한다.
            int[] viewLoc = new int[2];
            src.getLocationOnScreen(viewLoc);

            Rect bounds = new Rect(viewLoc[0], viewLoc[1], viewLoc[0]+src.getMeasuredWidth(), viewLoc[1]+src.getMeasuredHeight());

            sharedElementManager.sharedElemntBounds.put(targetTransitionName, bounds);

            String sourceTransitionName = sharedElementManager.getTransitionName(src);
            if (!TextUtils.isEmpty(sourceTransitionName)) {
                sharedElementManager.sharedElementSourceNames.add(sourceTransitionName);
                sharedElementManager.sharedElementTargetNames.add(targetTransitionName);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void setSharedElementEnterTransitionChangeBounds(Fragment fragment) {
        if (isLollipop) {
            fragment.setSharedElementEnterTransition(new ChangeBounds());

        } else if (fragment instanceof SharedElementFragment) {
            Bundle args = fragment.getArguments();
            if (args == null) {
                args = new Bundle();
            }

            // 2. shared element transition type 을 설정한다.
            args.putInt(TRANSIT_TYPE, TRANSIT_CHANGE_BOUNDS);

            fragment.setArguments(args);
        } else {
            throw new IllegalArgumentException("Please use on API Level 21 or SharedElementCompat.SharedElementFragment");
        }
    }

    public static void setTransitionName(View view, String transitionName) {
        if (isLollipop) {
            ViewCompat.setTransitionName(view, transitionName);
        } else {
            sharedElementManager.transitionNameAndViews.put(transitionName, view);
        }
    }


    private static class SharedElementManager {
        ArrayMap<String, View> transitionNameAndViews = new ArrayMap<>();
        ArrayMap<String, Object> sharedElemntBounds = new ArrayMap<>();
        ArrayList<String> sharedElementSourceNames = new ArrayList<>();
        ArrayList<String> sharedElementTargetNames = new ArrayList<>();

        public String getTransitionName(View view) {
            String transitionName = null;
            if (transitionNameAndViews.containsValue(view)) {
                Set<String> keySet = transitionNameAndViews.keySet();
                for (String name : keySet) {
                    if (view.equals(transitionNameAndViews.get(name))) {
                        transitionName = name;
                        break;
                    }
                }
            }

            return transitionName;
        }
    }

    public static class SharedElementFragment extends Fragment {

        private SharedElementCallback mEnterTransitionCallback = null;
        private int transitionType;
        private ArrayMap<String, View> namedViews;

        @Override
        public void onViewCreated(final View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            if (isLollipop) {
                return;
            }

            namedViews = new ArrayMap<>();

            // setTransitionName()은 onCreateView()에서 호출되어야 한다.
            // 1. fragment 내의 View 중에 transitionNameAndViews에 등록된 view의 transitionName 을 추출한다.
            findNamedViews(namedViews, getView());

            if (namedViews.size() == 0) {
                return;
            }

            view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    view.getViewTreeObserver().removeOnPreDrawListener(this);

                    Bundle args = getArguments();
                    if (args != null) {
                        transitionType = args.getInt(TRANSIT_TYPE);
                    } else {
                        transitionType = TRANSIT_CHANGE_BOUNDS;
                    }

                    // 2. sharedElemntBounds 에 transitionName 이 있는지 찾는다.
                    Set<String> transitionNameSet = namedViews.keySet();
                    for (String transitionName : transitionNameSet) {
                        // 같은 transitionName 이 있는지 확인.
                        if (sharedElementManager.sharedElemntBounds.containsKey(transitionName)) {

                            Object src = sharedElementManager.sharedElemntBounds.get(transitionName);
                            View desc = namedViews.get(transitionName);

                            // 3. back stack transition 대상이 되는 View의 정보를 저장해둔다.
                            int[] viewLoc = new int[2];
                            desc.getLocationOnScreen(viewLoc);

                            Rect bounds = new Rect(viewLoc[0], viewLoc[1], viewLoc[0]+desc.getMeasuredWidth(), viewLoc[1]+desc.getMeasuredHeight());

                            int index = sharedElementManager.sharedElementTargetNames.lastIndexOf(transitionName);
                            if (index >= 0) {
                                String targetTransitionName = sharedElementManager.sharedElementSourceNames.get(index);
                                sharedElementManager.sharedElemntBounds.put(targetTransitionName, bounds);
                            }

                            // 4. 찾은 transitionName 에 해당하는 Object 를 이용해서 애니메이션 처리를 한다.
                            dispatchTransition(transitionType, src, desc);
                        }
                    }

                    return false;
                }
            });
        }

        @Override
        public void setEnterSharedElementCallback(SharedElementCallback callback) {
            if (isLollipop) {
                super.setEnterSharedElementCallback(callback);
            } else {
                mEnterTransitionCallback = callback;
            }
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();

            if (isLollipop) {
                return;
            }

            Collection<View> views = namedViews.values();
            for (View v : views) {
                v.setVisibility(View.GONE);
            }

        }

        @Override
        public void onDetach() {
            super.onDetach();

            if (isLollipop) {
                return;
            }

            // fragment 가 back stack에서 사라질 때 관련 정보를 삭제한다.
            Set<String> transitionNameSet = namedViews.keySet();
            sharedElementManager.transitionNameAndViews.removeAll(transitionNameSet);

            for (String transitionName : transitionNameSet) {
                int index = sharedElementManager.sharedElementSourceNames.lastIndexOf(transitionName);
                if (index >= 0) {
                    sharedElementManager.sharedElementSourceNames.remove(index);
                    sharedElementManager.sharedElementTargetNames.remove(index);
                }
                sharedElementManager.sharedElemntBounds.remove(transitionName);
            }
        }

        private static void findNamedViews(Map<String, View> namedViews, View view) {
            if (view.getVisibility() == View.VISIBLE) {
                String transitionName = sharedElementManager.getTransitionName(view);
                if (transitionName != null) {
                    namedViews.put(transitionName, view);
                }
                if (view instanceof ViewGroup) {
                    ViewGroup viewGroup = (ViewGroup) view;
                    int count = viewGroup.getChildCount();
                    for (int i = 0; i < count; i++) {
                        View child = viewGroup.getChildAt(i);
                        findNamedViews(namedViews, child);
                    }
                }
            }
        }

        private void dispatchTransition(int transitionType, Object src, View desc) {

            switch (transitionType) {
                case TRANSIT_CHANGE_BOUNDS:
                    transitionChangeBounds((Rect) src, desc);
                    break;
                default:
                    break;
            }
        }

        private void transitionChangeBounds(Rect srcBounds, final View targetView) {
            // bounds check & view 의 위치를 srcBounds 에 맞춘다.
            int[] targetLoc = new int[2];
            targetView.getLocationOnScreen(targetLoc);

            final float scaleX = (float) srcBounds.width() / targetView.getWidth();
            ViewHelper.setScaleX(targetView, scaleX);
            ViewHelper.setPivotX(targetView, scaleX);

            final float scaleY = (float) srcBounds.height() / targetView.getHeight();
            ViewHelper.setScaleY(targetView, scaleY);
            ViewHelper.setPivotY(targetView, scaleY);

            final int xTrans = srcBounds.left - targetLoc[0];
            final int yTrans = srcBounds.top - targetLoc[1];

            ViewHelper.setTranslationX(targetView, xTrans);
            ViewHelper.setTranslationY(targetView, yTrans);

            ValueAnimator startAnim = ValueAnimator.ofFloat(1.f, 0.f);
            startAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    Float offset = (Float) animation.getAnimatedValue();

                    float scaleXOffset = (1.f - scaleX) * (1.f - offset) + scaleX;
                    float scaleYOffset = (1.f - scaleY) * (1.f - offset) + scaleY;

                    ViewHelper.setScaleX(targetView, scaleXOffset);
                    ViewHelper.setPivotX(targetView, scaleXOffset);
                    ViewHelper.setScaleY(targetView, scaleYOffset);
                    ViewHelper.setPivotY(targetView, scaleYOffset);

                    ViewHelper.setTranslationX(targetView, xTrans * offset);
                    ViewHelper.setTranslationY(targetView, yTrans * offset);

                }
            });
            startAnim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (mEnterTransitionCallback != null) {
                        ArrayList<String> names = new ArrayList<String>(sharedElementManager.transitionNameAndViews.keySet());
                        ArrayList<View> views = new ArrayList<View>(sharedElementManager.transitionNameAndViews.values());
                        mEnterTransitionCallback.onSharedElementEnd(names, views, null);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            startAnim.setDuration(300);
            startAnim.setInterpolator(new DecelerateInterpolator());
            startAnim.start();
        }

        @Override
        public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {

            // sharedElement 를 사용하는 경우에는 fadeIn/fadeOut 으로 해야 해당 element view 의 애니메이션이 방해받지 않는다.
            return AnimationUtils.loadAnimation(getActivity(),
                    enter ? android.R.anim.fade_in : android.R.anim.fade_out);
        }
    }
}
