package com.example.youjiannuo.listviewfloattitle;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;
import android.widget.TextView;


/**
 * Created by youjiannuo on 15/8/5.
 */
public class ViewUtil {

    public static int VIEW_PAGE_ID = 100;

    public static float getOneLineTextHeight(TextView textView) {
        return textView.getPaint().getFontMetricsInt(null);
    }


    public static ScreenInfo getScreenInfo(View v) {
        return getScreenInfo(v, null);
    }

    public static ScreenInfo getScreenInfo(View v, View parent) {
        int XY[] = {0, 0};
        View mainView = null;
        while (v != null) {
            XY[0] += v.getLeft();
            XY[1] += v.getTop();
            if (parent != null && v == parent || v.toString().contains("DecorView")) {
                mainView = v;
                break;
            }
            v = (View) v.getParent();
        }

        ScreenInfo screenInfo = new ScreenInfo();
        screenInfo.x = XY[0];
        screenInfo.y = XY[1];
        screenInfo.parentView = mainView;

        return screenInfo;
    }


    public static class ScreenInfo {

        public int x;

        public int y;

        public View parentView = null;
    }


    public static Bitmap loadBitmapFromView(View v) {
        if (v == null) {
            return null;
        }

        Bitmap screenshot;
        screenshot = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.RGB_565);
        Canvas c = new Canvas(screenshot);
        c.translate(-v.getScrollX(), -v.getScrollY());
        v.draw(c);
        return screenshot;
    }

    /**
     * 截图
     *
     * @param v 需要进行截图的控件
     * @return 该控件截图的Bitmap对象。
     */
    public static Bitmap printScreen(View v) {
        v.setDrawingCacheEnabled(true);
        Bitmap bitmap = v.getDrawingCache();
        Bitmap bitmap1 = Bitmap.createBitmap(bitmap);
        v.setDrawingCacheEnabled(false);
        return bitmap1;
    }

    public static Bitmap getScreenFromParent(View v) throws IllegalArgumentException {
        Bitmap bitmap = printScreen((View) v.getParent());
        int x = v.getLeft();
        int y = v.getTop();
        int w = v.getWidth();
        int h = v.getHeight();
        return Bitmap.createBitmap(bitmap, x, y, w, h);
    }

    public static Bitmap getMaxScreenFromParent(View v, View parentView, int padding) {
        Bitmap bitmap = printScreen(parentView);
        ScreenInfo info = getScreenInfo(v, parentView);
        int size = v.getHeight() > v.getWidth() ? v.getHeight() : v.getWidth();

        int newWidthPadding = (size - v.getWidth() + padding) / 2;
        int newHeightPadding = (size - v.getHeight() + padding) / 2;

        int x = info.x - newWidthPadding;
        x = x > 0 ? x : 0;
        int y = info.y - newHeightPadding - SystemUtil.dipTOpx(50);
        y = y > 0 ? y : 0;
        int w = v.getWidth() + newWidthPadding * 2;
        int h = v.getHeight() + newHeightPadding * 2;
        return Bitmap.createBitmap(bitmap, x, y, w, h);
    }


}
