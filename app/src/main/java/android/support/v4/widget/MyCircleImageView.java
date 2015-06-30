package android.support.v4.widget;

import android.content.Context;

/**
 * This class' only purpose is simply to expose the hidden {@link CircleImageView} in the Android
 * Support v4 Library.
 */
public class MyCircleImageView extends CircleImageView {
    public MyCircleImageView(Context context, int color, float radius) {
        super(context, color, radius);
    }
}
