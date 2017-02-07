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

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;
import org.fs.common.BusManager;
import org.fs.core.AbstractRecyclerViewHolder;
import org.fs.magazine.BuildConfig;
import org.fs.magazine.R;
import org.fs.magazine.entities.events.BookChange;
import org.fs.magazine.entities.events.PercentageChange;
import org.fs.magazine.entities.events.TextChange;
import org.fs.publication.entities.Book;
import rx.Subscription;

import static org.fs.util.ViewUtility.findViewById;

public class BookViewHolder extends AbstractRecyclerViewHolder<Book>  {

  private final static SimpleDateFormat serializer = new SimpleDateFormat("HH:mm, dd MMM yy", Locale.getDefault());

  private Book data;

  private ImageView cover;
  private TextView  title;
  private TextView  date;
  private TextView  info;

  private ProgressBar percentage;
  private Button      action;
  private Button      delete;

  private Subscription register;

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

  public final void attach() {
    register = BusManager.add((evt) -> {
      if (evt instanceof PercentageChange) {
        PercentageChange event = (PercentageChange) evt;
        if (event.book().equals(data)) {
          percentage.setProgress(event.percentage());
        }
      } else if (evt instanceof TextChange) {
        TextChange event = (TextChange) evt;
        if (event.book().equals(data)) {
          action.setText(event.stringId());
          if (event.stringId() == R.string.view_action) {
            delete.setVisibility(View.VISIBLE);
          } else {
            delete.setVisibility(View.GONE);
          }
        }
      }
    });
    action.setOnClickListener((view) -> {
      final String str = action.getText().toString();
      if (str.equals(view.getContext().getString(R.string.start_action))) {
        BusManager.send(new BookChange(data, BookChange.Action.START));
      } else if (str.equals(view.getContext().getString(R.string.stop_action))) {
        BusManager.send(new BookChange(data, BookChange.Action.STOP));
      } else if (str.equals(view.getContext().getString(R.string.view_action))) {
        BusManager.send(new BookChange(data, BookChange.Action.VIEW));
      }
    });
    delete.setOnClickListener((view) -> BusManager.send(new BookChange(data, BookChange.Action.DELETE)));
  }

  public final void detach() {
    action.setOnClickListener(null);
    delete.setOnClickListener(null);
    if (register != null) {
      BusManager.remove(register);
      register = null;
    }
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

    File file = new File(directory(), hpub());
    if (file.exists()) {
      action.setText(R.string.view_action);
      delete.setVisibility(View.VISIBLE);
    } else {
      delete.setVisibility(View.GONE);
    }
  }

  @Override protected String getClassTag() {
    return BookViewHolder.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return BuildConfig.DEBUG;
  }

  private Context context() {
    return itemView.getContext();
  }

  private File directory() {
    return new File(context().getFilesDir(), "baker");
  }

  private String hpub() {
    return String.format(Locale.ENGLISH, "%s.hpub", data.name());
  }
}