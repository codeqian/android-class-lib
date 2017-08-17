package util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.qzd.utildemo.R;

/**
 * 带删除按钮的文本框
 * Created by QZD on 2017/8/17.
 */

public class deletableAutoCompleteTextView extends android.support.v7.widget.AppCompatAutoCompleteTextView implements View.OnTouchListener, View.OnFocusChangeListener,TextWatcher {
    private Drawable mClearTextIcon;
    private OnFocusChangeListener mOnFocusChangeListener;
    private OnTouchListener mOnTouchListener;
    public deletableAutoCompleteTextView(final Context context) {
        super(context);
        init(context);
    }

    public deletableAutoCompleteTextView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public deletableAutoCompleteTextView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(final Context context) {
        final Drawable drawable = ContextCompat.getDrawable(context, R.mipmap.ss_sc2);
        final Drawable wrappedDrawable = DrawableCompat.wrap(drawable); //Wrap以后才能够让Tint着色器支持API 22 以前的版本
//        DrawableCompat.setTint(wrappedDrawable, getCurrentHintTextColor());//这里使用的图片并不符合着色要求，所以这里就注释掉了（其实就是中间没有镂空，放在这里提醒有这么一个坑。）
        mClearTextIcon = wrappedDrawable;
        mClearTextIcon.setBounds(0, 0, mClearTextIcon.getIntrinsicHeight(), mClearTextIcon.getIntrinsicHeight());
        setClearIconVisible(false);
        super.setOnTouchListener(this);
        super.setOnFocusChangeListener(this);
        addTextChangedListener(this);
    }

    private void setClearIconVisible(final boolean visible) {
        mClearTextIcon.setVisible(visible, false);
        final Drawable[] compoundDrawables = getCompoundDrawables();
        setCompoundDrawables(
                compoundDrawables[0],
                compoundDrawables[1],
                visible ? mClearTextIcon : null,
                compoundDrawables[3]);
    }

    @Override
    public void setOnFocusChangeListener(final OnFocusChangeListener onFocusChangeListener) {
        mOnFocusChangeListener = onFocusChangeListener;
    }

    @Override
    public void setOnTouchListener(final OnTouchListener onTouchListener) {
        mOnTouchListener = onTouchListener;
    }

    @Override
    public void onFocusChange(final View view, final boolean hasFocus) {
        if (hasFocus) {
            setClearIconVisible(getText().length() > 0);
        } else {
            setClearIconVisible(false);
        }
        if (mOnFocusChangeListener != null) {
            mOnFocusChangeListener.onFocusChange(view, hasFocus);
        }
    }

    @Override
    public boolean onTouch(final View view, final MotionEvent motionEvent) {
        final int x = (int) motionEvent.getX();
        if (mClearTextIcon.isVisible() && x > getWidth() - getPaddingRight() - mClearTextIcon.getIntrinsicWidth()) {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                setText("");
            }
            return true;
        }
        return mOnTouchListener != null && mOnTouchListener.onTouch(view, motionEvent);
    }

    @Override
    public final void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
        if (isFocused()) {
            setClearIconVisible(s.length() > 0);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void afterTextChanged(Editable s) {
    }
}
