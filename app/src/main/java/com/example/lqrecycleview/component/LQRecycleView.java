package com.example.lqrecycleview.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;


import com.example.lqrecycleview.R;

import java.util.ArrayList;
import java.util.List;

public class LQRecycleView extends ViewGroup {

    private MyAdapter myAdapter;

    private Recycler mRecycler;

    private boolean needRelayout;

    int scrollY;

    int firstRow = 0;

    private List<View> mCurrentViewList;

    int[] heights = null;

    float currentY = 0;

    private Context context;
    private int touchSlop;
    private int width;
    private int height;
    private int rowCount;

    public LQRecycleView(Context context) {
        super(context);

    }

    public LQRecycleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LQRecycleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LQRecycleView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context) {
        this.context = context;
        ViewConfiguration configuration = ViewConfiguration.get(context);
        touchSlop = configuration.getScaledTouchSlop();
        mCurrentViewList = new ArrayList<>();
        needRelayout = true;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int h;
        if (myAdapter != null) {
            rowCount = myAdapter.getItemCount();
            heights = new int[rowCount];
            for (int i = 0; i < heights.length; i++) {
                heights[i] = myAdapter.getHeight(i);

            }
        }
        int totalHe = sumArray(heights, 0, heights.length);
        h = Math.min(heightSize, totalHe);
        setMeasuredDimension(widthSize, h);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private int sumArray(int[] arr, int firstIndex, int count) {
        int sum  = 0;
        count += firstIndex;
        for (int i = firstIndex; i < count; i++) {
            sum += arr[i];
        }
        return sum;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (needRelayout && changed) {
            needRelayout = false;
            mCurrentViewList.clear();
            removeAllViews();
            if (myAdapter != null) {
                width = r - l;
                height = b - t;
                int top = 0;
                int bottom;
                for (int i = 0; i < rowCount && top < height; i++) {
                    bottom = heights[i] + top;
                    // create view
                    View view = makeAndStep(i, 0, top, width, bottom);
                    view.layout(0, top, width, bottom);
                    mCurrentViewList.add(view);
                    top = bottom;
                }
            }
        }
    }


    private View makeAndStep(int row, int left, int top, int right, int bottom) {

        View view = obtainView(row, right - left, bottom - top);

        view.layout(left, top, right, bottom);

        return view;

    }

    private View obtainView(int row, int width, int height) {

        int itemType = myAdapter.getItemViewType(row);

        // 通过指定 position 位置的View类型去回收池查找View，存在则复用，不存在则调用 onCreateViewHolder 创建

        View recycleView = mRecycler.get(itemType);

        View view;

        if (recycleView == null) {

            view = myAdapter.onCreateViewHolder(row, recycleView, this);

            if (view == null) {

                throw new RuntimeException("onCreateViewHolder 必须填充布局");

            }

        } else {

            view = myAdapter.onBindViewHolder(row, recycleView, this);

        }

        view.setTag(R.id.specificId, itemType);

        view.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));

        addView(view, 0);

        return view;

    }


    public MyAdapter getMyAdapter() {
        return myAdapter;
    }

    public void setMyAdapter(MyAdapter myAdapter) {
        this.myAdapter = myAdapter;
        if (myAdapter != null) {
            mRecycler = new Recycler(myAdapter.getViewTypeCount());
        }
        scrollY = 0;
        firstRow = 0;
        needRelayout = true;
        requestLayout();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercepted = false;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveY = currentY - ev.getRawY();
                if (Math.abs(moveY) > touchSlop) {
                    intercepted = true;
                }
                break;
            default:
                break;
        }
        return intercepted;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            int diff = (int) (currentY - event.getRawY());
            scrollBy(0, diff);
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void scrollBy(int x, int y) {
       scrollY += y;
       scrollY = scrollBounds(scrollY);
       if (scrollY > 0) {

           while (scrollY > heights[firstRow]) {
               removeView(mCurrentViewList.remove(0));
               scrollY -= heights[firstRow];
               firstRow++;
           }

           while (getFillHeight() < height) {
               int addLast = firstRow + mCurrentViewList.size();

               View view = obtainView(addLast, width, heights[addLast]);
               mCurrentViewList.add(mCurrentViewList.size(), view);

           }

       } else if (scrollY < 0) {

           while (scrollY < 0) {
               int firstAddRow = firstRow - 1;
               View view = obtainView(firstAddRow, width, heights[firstAddRow]);
               mCurrentViewList.add(0, view);
               firstRow--;
               scrollY += heights[firstRow + 1];
           }

           while (sumArray(heights, firstRow, mCurrentViewList.size()) - scrollY - heights[firstRow + mCurrentViewList.size() - 1] >= height)  {
               removeView(mCurrentViewList.remove(mCurrentViewList.size() - 1));
           }
       }

       repositionViews();
    }

    @Override
    public void removeView(View view) {
        super.removeView(view);
        int tag = (int) view.getTag(R.id.specificId);
        mRecycler.put(view, tag);
    }

    private int getFillHeight() {

        return sumArray(heights, firstRow, mCurrentViewList.size()) - scrollY;

    }

    private void repositionViews() {
        int left, top, right, bottom, i;
        top = -scrollY;
        i = firstRow;
        for (View view : mCurrentViewList) {
            bottom = top + heights[i++];
            view.layout(0, top, width, bottom);
            top = bottom;
        }

    }

    private int scrollBounds(int scrollY) {
        // 上滑

        if (scrollY > 0) {
            scrollY = Math.min(scrollY, sumArray(heights, firstRow, heights.length - firstRow) - height);
        } else {

        // 极限值 会取零 非极限值的情况下 scroll
            scrollY = Math.max(scrollY, -sumArray(heights, 0, firstRow));

        }
        return scrollY;
    }
}
