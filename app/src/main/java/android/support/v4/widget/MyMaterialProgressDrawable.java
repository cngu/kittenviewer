package android.support.v4.widget;

import android.content.Context;
import android.view.View;

/**
 * This class' only purpose is simply to expose the hidden {@link MaterialProgressDrawable} in the Android
 * Support v4 Library.
 */
public class MyMaterialProgressDrawable extends MaterialProgressDrawable {

    public static final int CIRCLE_DIAMETER = 40;
    public static final int CIRCLE_DIAMETER_LARGE = 56;
    public static final int CIRCLE_BG_LIGHT = 0xFFFAFAFA;

    public static final int LARGE = MaterialProgressDrawable.LARGE;
    public static final int DEFAULT = MaterialProgressDrawable.DEFAULT;

    public static final int MAX_ALPHA = 255;

    public MyMaterialProgressDrawable(Context context, View parent) {
        super(context, parent);
    }
}
