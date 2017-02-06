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
package org.fs.magazine.views.vh;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.text.SimpleDateFormat;
import java.util.Locale;
import org.fs.core.AbstractRecyclerViewHolder;
import org.fs.magazine.BuildConfig;
import org.fs.magazine.R;
import org.fs.publication.entities.Book;

import static org.fs.util.ViewUtility.findViewById;

public class BookViewHolder extends AbstractRecyclerViewHolder<Book> {

  private final static SimpleDateFormat serializer = new SimpleDateFormat("HH:mm, dd MMM yy", Locale.getDefault());

  private Book data;

  private ImageView cover;
  private TextView  title;
  private TextView  date;
  private TextView  info;

  private ProgressBar percentage;
  private Button      action;
  private Button      delete;

  public BookViewHolder(View view) {
    super(view);
    cover = findViewById(view, R.id.cover);
    title = findViewById(view, R.id.title);
    date = findViewById(view, R.id.date);
    info = findViewById(view, R.id.info);
    percentage = findViewById(view, R.id.percentage);
    action = findViewById(view, R.id.action);
    delete = findViewById(view, R.id.delete);
  }

  @Override public final void onBindView(Book data) {
    this.data = data;
    // glide loader
    Glide.with(itemView.getContext())
        .load(data.cover())
        .crossFade()
        .into(cover);

    title.setText(data.title());
    date.setText(serializer.format(data.date()));
    info.setText(data.info());
  }

  @Override protected String getClassTag() {
    return BookViewHolder.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return BuildConfig.DEBUG;
  }
}