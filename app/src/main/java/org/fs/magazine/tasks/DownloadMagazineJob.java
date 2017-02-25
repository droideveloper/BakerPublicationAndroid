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
package org.fs.magazine.tasks;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.fs.common.BusManager;
import org.fs.common.ThreadManager;
import org.fs.magazine.commons.BakerFile;
import org.fs.magazine.entities.events.FileChange;
import org.fs.publication.entities.Book;
import org.fs.util.Objects;

public final class DownloadMagazineJob extends Job {

  public static final int PRIORITY = 1;
  // 1MB each request
  public static final long PART_SIZE = 1024 * 1024;
  public static final int  TIMEOUT = 5000;

  // length of content
  public static final String CONTENT_LENGTH = "Content-Length";
  public static final String RANGE = "Range";

  /**
   * it will not try to serialize those now
   */
  private transient final Book book;
  private transient final BakerFile bakerFile;
  private transient final File f;

  private long contentSize;

  public DownloadMagazineJob(final Book book, BakerFile bakerFile) {
    super(new Params(PRIORITY).requireNetwork());
    this.book = book;
    this.bakerFile = bakerFile;
    if(Objects.isNullOrEmpty(book)) {
      throw new RuntimeException("book can not be null");
    }
    this.f = new File(bakerFile.directory(),
        String.format(Locale.ENGLISH, "%s.hpub", book.name()));
  }

  @Override protected void onCancel(int cancelReason, @Nullable Throwable throwable) { }
  @Override public void onAdded() { }

  @Override public void onRun() throws Throwable {
    contentSize = toHead(book.url());
    if (f.exists()) {
      long persistedSize = f.length();
      if (persistedSize == contentSize) {
        ThreadManager.runOnUiThread(() -> BusManager.send(new FileChange(f, book)));
      } else {
        final List<RangePart> parts = toRangeParts(persistedSize, contentSize);
        toPart(parts, book, bakerFile, f, contentSize);
        ThreadManager.runOnUiThread(() -> BusManager.send(new FileChange(f, book)));
      }
    } else {
      final List<RangePart> parts = toRangeParts(0, contentSize);
      toPart(parts, book, bakerFile, f, contentSize);
      ThreadManager.runOnUiThread(() -> BusManager.send(new FileChange(f, book)));
    }
  }

  @Override protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
    return RetryConstraint.createExponentialBackoff(runCount, 1000);
  }

  private static void toPart(List<RangePart> parts, Book book, BakerFile bakerFile, File file, long expected) throws IOException {
    HttpURLConnection urlConnection = null;
    try {
      for (int i = 0, z = parts.size(); i < z; i++) {
        final RangePart part = parts.get(i);
        if (urlConnection != null) {
          urlConnection.disconnect();
        }
        urlConnection = openConnection(toURL(book.url()));
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty(RANGE, part.toString());
        urlConnection.setDoInput(true);
        urlConnection.setConnectTimeout(TIMEOUT);
        urlConnection.setReadTimeout(TIMEOUT);
        urlConnection.connect();
        if (httpResponse(urlConnection.getResponseCode())) {
          bakerFile.write(file, urlConnection.getInputStream(), book, expected);
        } else {
          throw new RuntimeException("response code is " + urlConnection.getResponseCode());
        }
      }
    } finally {
      if (urlConnection != null) {
        urlConnection.disconnect();
      }
    }
  }

  private static long toHead(String url) throws IOException, NumberFormatException {
    HttpURLConnection urlConnection = openConnection(toURL(url));
    try {
      urlConnection.setRequestMethod("HEAD");
      urlConnection.connect();
      if (httpResponse(urlConnection.getResponseCode())) {
        String headerValue = urlConnection.getHeaderField(CONTENT_LENGTH);
        return Long.parseLong(Objects.isNullOrEmpty(headerValue) ? "0L" : headerValue);
      } else {
        throw new RuntimeException("response code is " + urlConnection.getResponseCode());
      }
    } finally {
      urlConnection.disconnect();
    }
  }

  private static HttpURLConnection openConnection(URL url) throws IOException {
    return (HttpURLConnection) url.openConnection();
  }

  private static URL toURL(final String url) throws MalformedURLException {
    return new URL(url);
  }

  private static boolean httpResponse(int responseCode) {
    return (responseCode / 100) <= 2;
  }

  private static List<RangePart> toRangeParts(long start, long total) {
    List<RangePart> list = new ArrayList<>();
    for(; start < total; ) {
      long end = (start + PART_SIZE) < total ? (start + PART_SIZE - 1) : (total - 1);
      list.add(RangePart.create(start, end));
      start = end + 1;
    }
    return list;
  }

  /**
   * Range Header Values
   */
  static class RangePart {

    final long start;
    final long end;

    public static RangePart create(long start, long end) {
      return new RangePart(start, end);
    }

    private RangePart(long start, long end) {
      this.start = start;
      this.end = end;
    }

    @Override public String toString() {
      return "bytes=" + start + "-" + end;
    }
  }
}
