package ir.jin724.videochat.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout.LayoutParams;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;


public class ViewUtil {
    @SuppressWarnings("deprecation")
    public static void setBackground(final @NonNull View v, final @Nullable Drawable drawable) {
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
            v.setBackground(drawable);
        } else {
            v.setBackgroundDrawable(drawable);
        }
    }

    public static void setY(final @NonNull View v, final int y) {
        if (VERSION.SDK_INT >= 11) {
            ViewCompat.setY(v, y);
        } else {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            params.topMargin = y;
            v.setLayoutParams(params);
        }
    }

    public static float getY(final @NonNull View v) {
        if (VERSION.SDK_INT >= 11) {
            return ViewCompat.getY(v);
        } else {
            return ((ViewGroup.MarginLayoutParams) v.getLayoutParams()).topMargin;
        }
    }

    public static void setX(final @NonNull View v, final int x) {
        if (VERSION.SDK_INT >= 11) {
            ViewCompat.setX(v, x);
        } else {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            params.leftMargin = x;
            v.setLayoutParams(params);
        }
    }

    public static float getX(final @NonNull View v) {
        if (VERSION.SDK_INT >= 11) {
            return ViewCompat.getX(v);
        } else {
            return ((LayoutParams) v.getLayoutParams()).leftMargin;
        }
    }

    public static void swapChildInPlace(ViewGroup parent, View toRemove, View toAdd, int defaultIndex) {
        int childIndex = parent.indexOfChild(toRemove);
        if (childIndex > -1) parent.removeView(toRemove);
        parent.addView(toAdd, childIndex > -1 ? childIndex : defaultIndex);
    }

    @SuppressWarnings("unchecked")
    public static <T extends View> T inflateStub(@NonNull View parent, @IdRes int stubId) {
        return (T) ((ViewStub) parent.findViewById(stubId)).inflate();
    }

    @SuppressWarnings("unchecked")
    public static <T extends View> T findById(@NonNull View parent, @IdRes int resId) {
        return (T) parent.findViewById(resId);
    }

    @SuppressWarnings("unchecked")
    public static <T extends View> T findById(@NonNull Activity parent, @IdRes int resId) {
        return (T) parent.findViewById(resId);
    }


    private static Animation getAlphaAnimation(float from, float to, int duration) {
        final Animation anim = new AlphaAnimation(from, to);
        anim.setInterpolator(new FastOutSlowInInterpolator());
        anim.setDuration(duration);
        return anim;
    }

    public static void fadeIn(final @NonNull View view, final int duration) {
        animateIn(view, getAlphaAnimation(0f, 1f, duration));
    }


    public static void animateIn(final @NonNull View view, final @NonNull Animation animation) {
        if (view.getVisibility() == View.VISIBLE) return;

        view.clearAnimation();
        animation.reset();
        animation.setStartTime(0);
        view.setVisibility(View.VISIBLE);
        view.startAnimation(animation);
    }

    @SuppressWarnings("unchecked")
    public static <T extends View> T inflate(@NonNull LayoutInflater inflater,
                                             @NonNull ViewGroup parent,
                                             @LayoutRes int layoutResId) {
        return (T) (inflater.inflate(layoutResId, parent, false));
    }


    public static int dpToPx(Context context, int dp) {
        return (int) ((dp * context.getResources().getDisplayMetrics().density) + 0.5);
    }

    public static void updateLayoutParams(@NonNull View view, int width, int height) {
        view.getLayoutParams().width = width;
        view.getLayoutParams().height = height;
        view.requestLayout();
    }

    public static int getLeftMargin(@NonNull View view) {
        if (ViewCompat.getLayoutDirection(view) == ViewCompat.LAYOUT_DIRECTION_LTR) {
            return ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).leftMargin;
        }
        return ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).rightMargin;
    }

    public static int getRightMargin(@NonNull View view) {
        if (ViewCompat.getLayoutDirection(view) == ViewCompat.LAYOUT_DIRECTION_LTR) {
            return ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).rightMargin;
        }
        return ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).leftMargin;
    }

    public static void setLeftMargin(@NonNull View view, int margin) {
        if (ViewCompat.getLayoutDirection(view) == ViewCompat.LAYOUT_DIRECTION_LTR) {
            ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).leftMargin = margin;
        } else {
            ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).rightMargin = margin;
        }
        view.forceLayout();
        view.requestLayout();
    }

    public static void setTopMargin(@NonNull View view, int margin) {
        ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).topMargin = margin;
        view.requestLayout();
    }

    public static void setPaddingTop(@NonNull View view, int padding) {
        view.setPadding(view.getPaddingLeft(), padding, view.getPaddingRight(), view.getPaddingBottom());
    }

    public static void setPaddingBottom(@NonNull View view, int padding) {
        view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), padding);
    }

    public static boolean isPointInsideView(@NonNull View view, float x, float y) {
        int[] location = new int[2];

        view.getLocationOnScreen(location);

        int viewX = location[0];
        int viewY = location[1];

        return x > viewX && x < viewX + view.getWidth() &&
                y > viewY && y < viewY + view.getHeight();
    }

    public static int getStatusBarHeight(@NonNull View view) {
        int result = 0;
        int resourceId = view.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = view.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}