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

import android.content.Intent;
import java.io.File;
import java.util.Locale;
import org.fs.common.AbstractPresenter;
import org.fs.common.BusManager;
import org.fs.evoke.DownloadManager;
import org.fs.evoke.NetworkJob;
import org.fs.magazine.BuildConfig;
import org.fs.magazine.commons.BakerFile;
import org.fs.magazine.commons.BakerService;
import org.fs.magazine.commons.BakerStorage;
import org.fs.magazine.entities.events.BookChange;
import org.fs.magazine.entities.events.PercentageChange;
import org.fs.magazine.views.BakerShelfActivityView;
import org.fs.publication.entities.Book;
import org.fs.publication.views.ReadActivity;
import org.fs.util.Collections;
import org.fs.util.ObservableList;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class BakerShelfActivityPresenterImp extends AbstractPresenter<BakerShelfActivityView>
    implements BakerShelfActivityPresenter {

  private final ObservableList<Book> data;
  private final BakerFile file;
  private final BakerService service;
  private final BakerStorage storage;

  private Subscription register;

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
    } else {
      view.hideProgress();
    }
    DownloadManager.register(view.getContext());
    register = BusManager.add((evt) -> {
      if (evt instanceof BookChange) {
        BookChange event = (BookChange) evt;
        final Book book = event.book();
        // start action
        if (event.action().equals(BookChange.Action.START)) {

          final NetworkJob job = new NetworkJob.Builder()
            .fileName(String.format(Locale.ENGLISH, "%s.hpub", book.name()))
            .url(book.url())
            .type(NetworkJob.ConnectionType.UNSPECIFIED)
            .policy(NetworkJob.Policy.DELETE_ON_ERROR)
            .build();

          final DownloadManager.SimpleJobListener callback = new DownloadManager.SimpleJobListener() {
            @Override public void onComplete(File f) {
              file.extract(f, book.name())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(configuration -> {});
              int index = data.indexOf((d) -> d.url().equalsIgnoreCase(book.url()));
              if (index != -1) {
                data.set(index, book);
              }
            }

            @Override public void onProgress(int percentage) {
              BusManager.send(new PercentageChange(book, percentage));
            }
          };
          DownloadManager.schedule(job, callback);
        } else if (event.action().equals(BookChange.Action.VIEW)) {
          file.extract(null, book.name())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(configuration -> {
              if (view.isAvailable()) {
                Intent intent = new Intent(view.getContext(), ReadActivity.class);
                intent.putExtra("key.configuration.object", configuration);
                view.startActivity(intent);
              }
            });
        }
      }
    });
  }

  @Override public void onStop() {
    DownloadManager.unregister(view.getContext());
    if (register != null) {
      BusManager.remove(register);
      register = null;
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