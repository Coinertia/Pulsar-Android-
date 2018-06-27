package com.pulsar.android.components.EditComponent;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.originqiu.library.FlowLayout;
import me.originqiu.library.R.styleable;
import me.originqiu.library.R.layout;

public class EditSpacebar extends FrameLayout implements OnClickListener, OnEditorActionListener, OnKeyListener, TextWatcher {
    private FlowLayout flowLayout;
    private EditText editText;
    private int tagViewLayoutRes;
    private int inputTagLayoutRes;
    private int deleteModeBgRes;
    private Drawable defaultTagBg;
    private boolean isEditableStatus;
    private TextView lastSelectTagView;
    private List<String> tagValueList;
    private boolean isDelAction;
    private me.originqiu.library.EditTag.TagAddCallback tagAddCallBack;
    private me.originqiu.library.EditTag.TagDeletedCallback tagDeletedCallback;

    public EditSpacebar(Context context) {
        this(context, (AttributeSet) null);
    }

    public EditSpacebar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EditSpacebar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.isEditableStatus = true;
        this.tagValueList = new ArrayList();
        this.isDelAction = false;
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, styleable.EditTag);
        this.tagViewLayoutRes = mTypedArray.getResourceId(styleable.EditTag_tag_layout, layout.view_default_tag);
        this.inputTagLayoutRes = mTypedArray.getResourceId(styleable.EditTag_input_layout, layout.view_default_input_tag);
        this.deleteModeBgRes = mTypedArray.getResourceId(styleable.EditTag_delete_mode_bg, ss.com.bannerslider.R.color.abc_background_cache_hint_selector_material_dark);
        mTypedArray.recycle();
        this.setupView();
    }

    private void setupView() {
        this.flowLayout = new FlowLayout(this.getContext());
        LayoutParams layoutParams = new LayoutParams(-2, -2);
        this.flowLayout.setLayoutParams(layoutParams);
        this.addView(this.flowLayout);
        this.addInputTagView();
    }

    private void addInputTagView() {
        this.editText = this.createInputTag(this.flowLayout);
        this.editText.setTag(new Object());
        this.editText.setOnClickListener(this);
        this.editText.addTextChangedListener(this);
        this.setupListener();
        this.flowLayout.addView(this.editText);
        this.isEditableStatus = true;
    }

    private void setupListener() {
        this.editText.setOnEditorActionListener(this);
        this.editText.setOnKeyListener(this);
    }

    public boolean onKey(View v, int keyCode, KeyEvent event) {
        boolean isHandle = false;
        if (keyCode == 67 && event.getAction() == 0) {
            String tagContent = this.editText.getText().toString();
            int tagCount;
            if (TextUtils.isEmpty(tagContent)) {
                tagCount = this.flowLayout.getChildCount();
                if (this.lastSelectTagView == null && tagCount > 1) {
                    if (this.isDelAction) {
                        this.flowLayout.removeViewAt(tagCount - 2);
                        if (this.tagDeletedCallback != null) {
                            this.tagDeletedCallback.onTagDelete((String) this.tagValueList.get(tagCount - 2));
                        }

                        this.tagValueList.remove(tagCount - 2);
                        isHandle = true;
                    } else {
                        TextView delActionTagView = (TextView) this.flowLayout.getChildAt(tagCount - 2);
                        delActionTagView.setBackgroundDrawable(this.getDrawableByResId(this.deleteModeBgRes));
                        this.lastSelectTagView = delActionTagView;
                        this.isDelAction = true;
                    }
                } else {
                    this.removeSelectedTag();
                }
            } else {
                tagCount = tagContent.length();
                this.editText.getText().delete(tagCount, tagCount);
            }
        }

        return isHandle;
    }

    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        boolean isHandle = false;
        if (actionId == 6) {
            String tagContent = this.editText.getText().toString();
            if (!TextUtils.isEmpty(tagContent) && (this.tagAddCallBack == null || this.tagAddCallBack != null && this.tagAddCallBack.onTagAdd(tagContent))) {
                TextView tagTextView = this.createTag(this.flowLayout, tagContent);
                if (this.defaultTagBg == null) {
                    this.defaultTagBg = tagTextView.getBackground();
                }

                tagTextView.setOnClickListener(this);
                this.flowLayout.addView(tagTextView, this.flowLayout.getChildCount() - 1);
                this.tagValueList.add(tagContent);
                this.editText.getText().clear();
                this.editText.performClick();
                this.isDelAction = false;
                isHandle = true;
            }
        }

        return isHandle;
    }

    public void onClick(View view) {
        if (view.getTag() == null && this.isEditableStatus) {
            if (this.lastSelectTagView == null) {
                this.lastSelectTagView = (TextView) view;
                view.setBackgroundDrawable(this.getDrawableByResId(this.deleteModeBgRes));
            } else if (this.lastSelectTagView.equals(view)) {
                this.removeSelectedTag();
//                this.lastSelectTagView.setBackgroundDrawable(this.defaultTagBg);
//                this.lastSelectTagView = null;
            } else {
                this.lastSelectTagView.setBackgroundDrawable(this.defaultTagBg);
                this.lastSelectTagView = (TextView) view;
                view.setBackgroundDrawable(this.getDrawableByResId(this.deleteModeBgRes));
            }
        } else if (this.lastSelectTagView != null) {
            this.lastSelectTagView.setBackgroundDrawable(this.defaultTagBg);
            this.lastSelectTagView = null;
        }

    }

    private void removeSelectedTag() {
        int size = this.tagValueList.size();
        if (size > 0 && this.lastSelectTagView != null) {
            int index = this.flowLayout.indexOfChild(this.lastSelectTagView);
            this.tagValueList.remove(index);
            this.flowLayout.removeView(this.lastSelectTagView);
            if (this.tagDeletedCallback != null) {
                this.tagDeletedCallback.onTagDelete(this.lastSelectTagView.getText().toString());
            }

            this.lastSelectTagView = null;
            this.isDelAction = false;
        }

    }

    private TextView createTag(ViewGroup parent, String s) {
        TextView tagTv = (TextView) LayoutInflater.from(this.getContext()).inflate(this.tagViewLayoutRes, parent, false);
        tagTv.setText(s);
        return tagTv;
    }

    private EditText createInputTag(ViewGroup parent) {
        this.editText = (EditText) LayoutInflater.from(this.getContext()).inflate(this.inputTagLayoutRes, parent, false);
        return this.editText;
    }

    private void addTagView(List<String> tagList) {
        int size = tagList.size();

        for (int i = 0; i < size; ++i) {
            this.addTag((String) tagList.get(i));
        }

    }

    private Drawable getDrawableByResId(int resId) {
        return this.getContext().getResources().getDrawable(resId);
    }

    public void setEditable(boolean editable) {
        if (editable) {
            if (!this.isEditableStatus) {
                this.flowLayout.addView(this.editText);
            }
        } else {
            int childCount = this.flowLayout.getChildCount();
            if (this.isEditableStatus && childCount > 0) {
                this.flowLayout.removeViewAt(childCount - 1);
                if (this.lastSelectTagView != null) {
                    this.lastSelectTagView.setBackgroundDrawable(this.defaultTagBg);
                    this.isDelAction = false;
                    this.editText.getText().clear();
                }
            }
        }

        this.isEditableStatus = editable;
    }

    public boolean addTag(String tagContent) {
        if (TextUtils.isEmpty(tagContent)) {
            return false;
        } else if (this.tagAddCallBack != null && (this.tagAddCallBack == null || !this.tagAddCallBack.onTagAdd(tagContent))) {
            return false;
        } else {
            TextView tagTextView = this.createTag(this.flowLayout, tagContent);
            if (this.defaultTagBg == null) {
                this.defaultTagBg = tagTextView.getBackground();
            }

            tagTextView.setOnClickListener(this);
            if (this.isEditableStatus) {
                this.flowLayout.addView(tagTextView, this.flowLayout.getChildCount() - 1);
            } else {
                this.flowLayout.addView(tagTextView);
            }

            this.tagValueList.add(tagContent);
            this.editText.getText().clear();
            this.editText.performClick();
            this.isDelAction = false;
            return true;
        }
    }
    public void reset() {
        this.flowLayout.removeAllViews();
        List<String> mTemp = new ArrayList();
        for (int i = 0; i < this.tagValueList.size(); i++) {
            mTemp.add(this.tagValueList.get(i));
        }
        this.tagValueList.clear();
        this.setTagList(mTemp);
    }

    public void setTagList(List<String> mTagList) {
        this.addTagView(mTagList);
    }

    public List<String> getTagList() {
        return this.tagValueList;
    }

    public void setTagAddCallBack(me.originqiu.library.EditTag.TagAddCallback tagAddCallBack) {
        this.tagAddCallBack = tagAddCallBack;
    }

    public void setTagDeletedCallback(me.originqiu.library.EditTag.TagDeletedCallback tagDeletedCallback) {
        this.tagDeletedCallback = tagDeletedCallback;
    }

    public void removeTag(String... tagValue) {
        List<String> tagValues = Arrays.asList(tagValue);
        int childCount = this.flowLayout.getChildCount();

        for (int i = 0; i < childCount; ++i) {
            if (tagValues.size() > 0) {
                View view = this.flowLayout.getChildAt(i);

                try {
                    String value = ((TextView) view).getText().toString();
                    if (tagValues.contains(value)) {
                        this.tagValueList.remove(value);
                        if (this.tagDeletedCallback != null) {
                            this.tagDeletedCallback.onTagDelete(value);
                        }

                        this.flowLayout.removeView(view);
                        i = 0;
                        childCount = this.flowLayout.getChildCount();
                    }
                } catch (ClassCastException var7) {
                    var7.printStackTrace();
                    break;
                }
            }
        }

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        String tagContent = this.editText.getText().toString();
        if (tagContent.length() > 0) {
            if (tagContent.charAt(tagContent.length() - 1) == ' ') {
                tagContent = tagContent.replace(" ", "");
                if (tagContent.length() > 0) {
                    if (!TextUtils.isEmpty(tagContent) && (this.tagAddCallBack == null || this.tagAddCallBack != null && this.tagAddCallBack.onTagAdd(tagContent))) {
                        TextView tagTextView = this.createTag(this.flowLayout, tagContent);
                        if (this.defaultTagBg == null) {
                            this.defaultTagBg = tagTextView.getBackground();
                        }
                        tagTextView.setOnClickListener(this);
                        this.flowLayout.addView(tagTextView, this.flowLayout.getChildCount() - 1);
                        tagContent = tagContent.replace(" ", "");
                        this.tagValueList.add(tagContent);
                        this.editText.getText().clear();
                        this.editText.performClick();
                        this.isDelAction = false;
                    }
                }
            }
        }
    }

    public interface TagDeletedCallback {
        void onTagDelete(String var1);
    }

    public interface TagAddCallback {
        boolean onTagAdd(String var1);
    }
}
