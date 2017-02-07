package cn.yph.library;

import android.content.Context;
import android.util.Log;

/**
 * Created by yuanpenghao on 2017/2/7.
 */

public class Utils {

    public static int dp2px(Context context, int dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }


    public static int dp2px(Context context, float dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }


    public static void log(String name, String info) {
        Log.d("YPH", name + ":" + info);
    }

    public static void log(String name, int info) {
        Log.d("YPH", name + ":" + info);
    }

    public static void log(String name, float info) {
        Log.d("YPH", name + ":" + info);
    }
}
