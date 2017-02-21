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
package org.fs.magazine.commons.modules;

import com.birbit.android.jobqueue.JobManager;
import dagger.Module;
import dagger.Provides;
import java.util.Locale;
import org.fs.common.IView;
import org.fs.exception.AndroidException;
import org.fs.magazine.commons.BakerFile;
import org.fs.magazine.commons.BakerService;
import org.fs.magazine.commons.BakerStorage;
import org.fs.magazine.presenters.BakerShelfActivityPresenter;
import org.fs.magazine.presenters.BakerShelfActivityPresenterImp;
import org.fs.magazine.views.BakerShelfActivityView;
import org.fs.magazine.views.adapter.BookRecyclerAdapter;
import org.fs.publication.commons.scopes.PerActivity;
import org.fs.publication.entities.Book;
import org.fs.util.ObservableList;

@Module public class ActivityCompatModule {

  public final IView view;
  public final ObservableList<Book> data;

  public ActivityCompatModule(final IView view) {
    this.view  = view;
    this.data = new ObservableList<>();
  }

  @PerActivity @Provides public BakerShelfActivityPresenter bakerShelfActivityPresenter(BakerFile file, BakerService service, BakerStorage storage, JobManager jobManager) {
    if (view instanceof BakerShelfActivityView) {
      return new BakerShelfActivityPresenterImp((BakerShelfActivityView) view, data, file, service, storage, jobManager);
    }
    throw new AndroidException(
        String.format(Locale.ENGLISH,
            "%s is not valid for this module",
            view.getClass().getSimpleName())
    );
  }

  @PerActivity @Provides public BookRecyclerAdapter bookRecyclerAdapter() {
    return new BookRecyclerAdapter(data, view.getContext());
  }
}