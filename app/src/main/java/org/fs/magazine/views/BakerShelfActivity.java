/*
 * BakerPublicationAndroid Copyright (C) 2017 Fatih.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fs.magazine.views;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import javax.inject.Inject;
import org.fs.core.AbstractActivity;
import org.fs.magazine.BakerApplication;
import org.fs.magazine.BuildConfig;
import org.fs.magazine.R;
import org.fs.magazine.commons.components.AppComponent;
import org.fs.magazine.commons.components.DaggerActivityCompatComponent;
import org.fs.magazine.commons.modules.ActivityCompatModule;
import org.fs.magazine.presenters.BakerShelfActivityPresenter;
import org.fs.magazine.views.adapter.BookRecyclerAdapter;
import org.fs.util.ViewUtility;

import static org.fs.magazine.R.layout.view_baker_shelf_activity;

public class BakerShelfActivity extends AbstractActivity<BakerShelfActivityPresenter>
    implements BakerShelfActivityView {

  @Inject BakerShelfActivityPresenter presenter;
  @Inject BookRecyclerAdapter recyclerAdapter;

  private ProgressBar   progress;
  private RecyclerView  recycler;
  private Toolbar       toolbar;

  @Override public void onCreate(Bundle restoreState) {
    super.onCreate(restoreState);
    setContentView(view_baker_shelf_activity);
    // load views
    progress = ViewUtility.findViewById(this, R.id.progress);
    toolbar = ViewUtility.findViewById(this, R.id.toolbar);
    recycler = ViewUtility.findViewById(this, R.id.recycler);
    // inject it this way
    DaggerActivityCompatComponent.builder()
        .activityCompatModule(new ActivityCompatModule(this))
        .appComponent(dependency())
        .build()
        .inject(this);
    presenter.restoreState(restoreState != null ? restoreState : getIntent().getExtras());
    presenter.onCreate();
  }

  @Override public void onSaveInstanceState(Bundle storeState) {
    super.onSaveInstanceState(storeState);
    presenter.storeState(storeState);
  }

  @Override public void onStart() {
    super.onStart();
    presenter.onStart();
  }

  @Override public void onStop() {
    presenter.onStop();
    super.onStop();
  }

  @Override public void showError(String errorString) {
    final View view = view();
    if (view != null) {
      Snackbar.make(view, errorString, Snackbar.LENGTH_LONG).show();
    }
  }

  @Override public void showError(String errorString, String actionTextString,
      View.OnClickListener callback) {
    final View view = view();
    if (view != null) {
      final Snackbar snackbar = Snackbar.make(view, errorString, Snackbar.LENGTH_LONG);
      snackbar.setAction(actionTextString, v -> {
        if (callback != null) {
          callback.onClick(v);
        }
        snackbar.dismiss();
      });
      snackbar.show();
    }
  }

  @Override public void setup() {
    recycler.setItemAnimator(new DefaultItemAnimator());
    recycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    recycler.setHasFixedSize(true);
    recycler.setAdapter(recyclerAdapter);
  }

  @Override public void showProgress() {
    progress.setIndeterminate(true);
    progress.setVisibility(View.VISIBLE);
  }

  @Override public void hideProgress() {
    progress.setIndeterminate(false);
    progress.setVisibility(View.INVISIBLE);
  }

  @Override public String getStringResource(@StringRes int stringId) {
    return getString(stringId);
  }

  @Override public Context getContext() {
    return this;
  }

  @Override public boolean isAvailable() {
    return !isFinishing();
  }

  @Override protected boolean isLogEnabled() {
    return BuildConfig.DEBUG;
  }

  @Override protected String getClassTag() {
    return BakerShelfActivity.class.getSimpleName();
  }

  private View view() {
    return findViewById(android.R.id.content);
  }

  private AppComponent dependency() {
    BakerApplication app = (BakerApplication) getApplication();
    if (app != null) {
      return app.component();
    }
    return null;
  }
}