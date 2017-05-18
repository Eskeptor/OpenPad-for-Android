package com.eskeptor.openTextViewer;

import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A {@link android.preference.PreferenceActivity} which implements and proxies the necessary calls
 * to be used with AppCompat.
 */
public abstract class AppCompatPreferenceActivity extends PreferenceActivity
{

    private AppCompatDelegate mDelegate;

    @Override
    protected void onCreate(Bundle _savedInstanceState)
    {
        getDelegate().installViewFactory();
        getDelegate().onCreate(_savedInstanceState);
        super.onCreate(_savedInstanceState);
    }

    @Override
    protected void onPostCreate(Bundle _savedInstanceState)
    {
        super.onPostCreate(_savedInstanceState);
        getDelegate().onPostCreate(_savedInstanceState);
    }

    public ActionBar getSupportActionBar()
    {
        return getDelegate().getSupportActionBar();
    }

    public void setSupportActionBar(@Nullable Toolbar _toolbar)
    {
        getDelegate().setSupportActionBar(_toolbar);
    }

    @Override
    public MenuInflater getMenuInflater()
    {
        return getDelegate().getMenuInflater();
    }

    @Override
    public void setContentView(@LayoutRes int _layoutResID)
    {
        getDelegate().setContentView(_layoutResID);
    }

    @Override
    public void setContentView(View _view)
    {
        getDelegate().setContentView(_view);
    }

    @Override
    public void setContentView(View _view, ViewGroup.LayoutParams _params)
    {
        getDelegate().setContentView(_view, _params);
    }

    @Override
    public void addContentView(View _view, ViewGroup.LayoutParams _params)
    {
        getDelegate().addContentView(_view, _params);
    }

    @Override
    protected void onPostResume()
    {
        super.onPostResume();
        getDelegate().onPostResume();
    }

    @Override
    protected void onTitleChanged(CharSequence _title, int _color)
    {
        super.onTitleChanged(_title, _color);
        getDelegate().setTitle(_title);
    }

    @Override
    public void onConfigurationChanged(Configuration _newConfig)
    {
        super.onConfigurationChanged(_newConfig);
        getDelegate().onConfigurationChanged(_newConfig);
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        getDelegate().onStop();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        getDelegate().onDestroy();
        if(mDelegate != null)
        {
            mDelegate = null;
        }
    }

    public void invalidateOptionsMenu()
    {
        getDelegate().invalidateOptionsMenu();
    }

    private AppCompatDelegate getDelegate()
    {
        if (mDelegate == null)
        {
            mDelegate = AppCompatDelegate.create(this, null);
        }
        return mDelegate;
    }
}
