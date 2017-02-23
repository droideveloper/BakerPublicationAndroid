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
package org.fs.magazine.views.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.fs.core.AbstractRecyclerAdapter;
import org.fs.exception.AndroidException;
import org.fs.magazine.BuildConfig;
import org.fs.magazine.R;
import org.fs.magazine.views.vh.BookViewHolder;
import org.fs.publication.entities.Book;
import org.fs.util.IPropertyChangedListener;
import org.fs.util.ObservableList;

public class BookRecyclerAdapter extends AbstractRecyclerAdapter<Book, BookViewHolder>
    implements IPropertyChangedListener {

  public BookRecyclerAdapter(ObservableList<Book> dataSet, Context context) {
    super(dataSet, context);
    dataSet.registerPropertyChangedListener(this);
  }

  @Override public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
    super.onDetachedFromRecyclerView(recyclerView);
    if (dataSet instanceof ObservableList<?>) {
      // if we detached from it we no longer need this.
      ObservableList<?> observer = (ObservableList<?>) dataSet;
      observer.unregisterPropertyChangedListener(this);
    }
  }

  @Override public void onViewAttachedToWindow(BookViewHolder holder) {
    holder.attach();
  }

  @Override public void onViewDetachedFromWindow(BookViewHolder holder) {
    holder.detach();
  }

  @Override public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    final LayoutInflater factory = inflaterFactory();
    if (factory != null) {
      final View view = factory.inflate(R.layout.view_entry_layout, parent, false);
      return new BookViewHolder(view);
    }
    throw new AndroidException("your context destroyed for some reason");
  }

  @Override public void onBindViewHolder(BookViewHolder holder, int position) {
    final Book book = getItemAtIndex(position);
    holder.onBindView(book);
  }

  @Override public int getItemViewType(int position) {
    return 0;
  }

  @Override protected String getClassTag() {
    return BookRecyclerAdapter.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return BuildConfig.DEBUG;
  }

  @Override public void notifyItemsRemoved(int index, int size) {
    if (size == 1) {
      notifyItemRemoved(index);
    } else {
      notifyItemRangeRemoved(index, size);
    }
  }

  @Override public void notifyItemsInserted(int index, int size) {
    if (size == 1) {
      notifyItemInserted(index);
    } else {
      notifyItemRangeInserted(index, size);
    }
  }

  @Override public void notifyItemsChanged(int index, int size) {
    if (size == 1) {
      notifyItemChanged(index);
    } else {
      notifyItemRangeChanged(index, size);
    }
  }
}