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
package org.fs.magazine.presenters;

import org.fs.common.AbstractPresenter;
import org.fs.magazine.BuildConfig;
import org.fs.magazine.commons.BakerFile;
import org.fs.magazine.commons.BakerService;
import org.fs.magazine.commons.BakerStorage;
import org.fs.magazine.views.BakerShelfActivityView;
import org.fs.publication.entities.Book;
import org.fs.util.Collections;
import org.fs.util.ObservableList;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class BakerShelfActivityPresenterImp extends AbstractPresenter<BakerShelfActivityView>
    implements BakerShelfActivityPresenter {

  private final ObservableList<Book> data;
  private final BakerFile file;
  private final BakerService service;
  private final BakerStorage storage;

  public BakerShelfActivityPresenterImp(BakerShelfActivityView view, ObservableList<Book> data, BakerFile file, BakerService service, BakerStorage storage) {
    super(view);
    this.data = data;
    this.file = file;
    this.service = service;
    this.storage = storage;
  }

  @Override public void onCreate() {
    view.setup();
  }

  @Override public void onStart() {
    view.showProgress();
    if (Collections.isNullOrEmpty(data)) {
      service.books()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe((entries) -> {
          if (view.isAvailable()) {
            data.addAll(entries);
            view.hideProgress();
          }
        }, this::log);
    }
  }

  @Override protected String getClassTag() {
    return BakerShelfActivityPresenterImp.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return BuildConfig.DEBUG;
  }

  @Override protected void log(Throwable error) {
    super.log(error);
    if (view.isAvailable()) {
      view.hideProgress();
      view.showError(error.getLocalizedMessage());
    }
  }
}