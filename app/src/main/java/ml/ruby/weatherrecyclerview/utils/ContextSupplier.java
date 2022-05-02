package ml.ruby.weatherrecyclerview.utils;

import android.content.Context;

/**
 * @author: jwhan
 * @createTime: 2022/05/02 11:58 AM
 * @description: This class will get context from MainActivity and store it. We can get it later if we need context
 */
public class ContextSupplier {
    private static Context context;

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        ContextSupplier.context = context;
    }
}
