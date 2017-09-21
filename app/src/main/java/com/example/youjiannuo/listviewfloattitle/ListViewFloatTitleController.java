package com.example.youjiannuo.listviewfloattitle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static com.example.youjiannuo.listviewfloattitle.ContextManager.getContext;


/**
 * Created by youjiannuo on 17/8/18.
 */

public class ListViewFloatTitleController {

    public final int MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;
    public final int WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT;

    //需要添加浮动标题
    private ViewGroup mView;
    private ViewGroup mListView;
    private ViewGroup mViewGroup;

    //正在移动浮动标题
    private boolean mIsMoveFloatTitle = false;
    //当前移动float标题
    private String mMoveTitleString = "";
    //当前处在哪一行
    private int mFirstVisibleItem = 0;

    private OperationListener mOperationListener;
    private int mLayout;
    private LinearLayoutManager mLinearLayoutManager;
    //title的标题
    private TextView mTitleTextView;

    private ListViewFloatTitleController(ViewGroup v, int layout) {
        mView = v;
        mLayout = layout;
    }

    /**
     * @param v      浮动的View的父控件,必须是RelativeLayout或者是FragmentLayout
     * @param layout 浮动View的布局资源
     */
    public ListViewFloatTitleController(ListView listView, ViewGroup v, int layout) {
        this(v, layout);
        mListView = listView;
        listView.setOnScrollListener(getOnScrollerListener());
    }

    public ListViewFloatTitleController(RecyclerView recyclerView, ViewGroup v, final int layout) {
        this(v, layout);
        mListView = recyclerView;
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                View topView = recyclerView.getChildAt(0);
                if (mLinearLayoutManager == null) {
                    mLinearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                }
                if (topView != null && mLinearLayoutManager != null) {
                    int lastPosition = mLinearLayoutManager.getPosition(topView);
                    listViewOnScroll(lastPosition);

                }
            }
        });
    }

    /**
     * 必须调用这个方法
     *
     * @param l 操作回调
     */
    public void setOperationListener(OperationListener l) {
        mOperationListener = l;
    }

    public int getFirstVisibleItem() {
        return mFirstVisibleItem;
    }

    public void closeTitleView() {
        if (mViewGroup != null) {
            mViewGroup.setVisibility(View.GONE);
        }
    }

    public void setTitleText(String title) {
        if (mTitleTextView != null) {
            mTitleTextView.setText(title);
        }
    }


    public void setTitleView(TextView titleView) {
        mTitleTextView = titleView;
    }

    private boolean isCheck(View view) {
        if (view instanceof RelativeLayout) {
            return false;
        } else if (view instanceof FrameLayout) {
            return false;
        }
        return true;
    }

    public AbsListView.OnScrollListener getOnScrollerListener() {
        return new OnScrollerListener();
    }

    private class OnScrollerListener implements AbsListView.OnScrollListener {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            listViewOnScroll(firstVisibleItem);
        }
    }

    public void listViewOnScroll(int firstVisibleItem) {
        if (mOperationListener == null || !mOperationListener.isShowTitle()) {
            return;
        }
        mFirstVisibleItem = firstVisibleItem;
        int index = firstVisibleItem - mOperationListener.getHead();

        if (mViewGroup == null) {
            mViewGroup = (ViewGroup) LayoutInflater.from(getContext()).inflate(mLayout, null);
            int y = 0;
            if (mView == null) {
                mView = (ViewGroup) mListView.getParent();
                while (isCheck(mView)) {
                    mView = (ViewGroup) mView.getParent();
                }
                ViewUtil.ScreenInfo screenInfo = ViewUtil.getScreenInfo(mListView, mView);
                y = screenInfo.y;
            }
            if (mView instanceof RelativeLayout) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
                params.topMargin = y;
                mView.addView(mViewGroup, params);
            } else {
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
                params.topMargin = y;
                mView.addView(mViewGroup, params);
            }
            mViewGroup.setVisibility(View.VISIBLE);
            mViewGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            moveTitlePosition(mViewGroup, 0);
        } else {
            if (index < 0) {
                mViewGroup.setVisibility(View.GONE);
            } else {
                mViewGroup.setVisibility(View.VISIBLE);
            }
        }

        if (mOperationListener.isResetTitle()) {
            mOperationListener.setTitle(index, mViewGroup);
        }

        moveTitleCalc(index, mViewGroup);
    }

    private void moveTitleCalc(int position, View view) {
        int index = position + 1;
        String title1 = mOperationListener.getTitleString(position);
        String title2 = mOperationListener.getTitleString(index);
//        SystemUtil.printlnInfo("t1 = " + title1 + "  t2 = " + title2);
        if (!StringUtil.isEmpty(title1)
                && !StringUtil.isEmpty(title2)
                && !title1.equals(title2)) {
            View childView = mListView.getChildAt(1);
            ViewUtil.ScreenInfo screenInfo = ViewUtil.getScreenInfo(childView, mListView);
            if (mIsMoveFloatTitle && !title2.equals(mMoveTitleString)) {
                mIsMoveFloatTitle = false;
            }
//            SystemUtil.printlnInfo("screenInfo = " + screenInfo.y + "   height = " + view.getHeight());
            if (!mIsMoveFloatTitle && screenInfo.y <= view.getHeight()) {
                mIsMoveFloatTitle = true;
                mMoveTitleString = title2;
            } else if (screenInfo.y >= view.getHeight()) {
                mIsMoveFloatTitle = false;
                moveTitlePosition(view, 0);
            }
            if (mIsMoveFloatTitle) {
                int y = screenInfo.y - view.getHeight();
                moveTitlePosition(view, y);
            }
        } else if (mIsMoveFloatTitle) {
            mIsMoveFloatTitle = false;
            moveTitlePosition(view, 0);
        }
    }

    private void moveTitlePosition(View view, int y) {
//        SystemUtil.printlnInfo("y = " + y);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
        params.topMargin = y;
        view.setLayoutParams(params);
//        view.layout(0, y, view.getWidth(), view.getHeight() + y);
    }


    public interface OperationListener {
        //是否重复设置Title不布局
        boolean isResetTitle();

        //可以在这里去设置title view的值
        void setTitle(int position, ViewGroup view);

        //listview添加多少个HeadView
        int getHead();

        //需要显示title的
        boolean isShowTitle();

        /**
         * 获取每一栏的title 对应的key ,例如A,B
         *
         * @param position 位置
         * @return
         */
        String getTitleString(int position);
    }
}
