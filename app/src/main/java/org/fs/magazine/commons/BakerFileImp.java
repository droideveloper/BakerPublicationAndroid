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
package org.fs.magazine.commons;

import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.fs.common.AbstractManager;
import org.fs.magazine.BuildConfig;
import org.fs.publication.entities.Configuration;
import rx.Observable;

public final class BakerFileImp extends AbstractManager implements BakerFile {

  private final static String DIRECTORY = "baker";
  private final static int BUFFER_SIZE  = 8192;

  private final File directory;
  private final Gson serializer;

  public BakerFileImp(Context context, Gson serializer) {
    this.serializer = serializer;
    directory = new File(context.getFilesDir(), DIRECTORY);
    if (!directory.exists()) {
      boolean created =  directory.mkdir();
      log(Log.INFO,
          String.format(Locale.ENGLISH, "%s is created, %s.",
              directory.getAbsolutePath(), created));
    }
  }

  @Override public Observable<Configuration> extract(File file, String name) {
    return Observable.just(new File(directory, name))
        .map(f -> {
          if (f.exists()) {
            return read(f, "book.json");
          }
          unzip(f, file);
          return read(f, "book.json");
        });
  }

  @Override protected String getClassTag() {
    return BakerFileImp.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return BuildConfig.DEBUG;
  }

  private Configuration read(File f, String json) {
    File file = new File(f, json);
    try {
      InputStream stream = new FileInputStream(file);
      StringBuilder str = new StringBuilder();
      byte[] buffer = new byte[BUFFER_SIZE];
      int cursor;
      while ((cursor = stream.read(buffer)) != -1) {
        str.append(new String(buffer, 0, cursor));
      }
      stream.close();
      return serializer.fromJson(str.toString(), Configuration.class);
    } catch (IOException e) {
      return null;
    }
  }

  private void unzip(File directory, File zip) {
    try {
      ZipInputStream stream = new ZipInputStream(new FileInputStream(zip));
      try {
        byte[] buffer = new byte[BUFFER_SIZE];
        ZipEntry entry;
        while ((entry = stream.getNextEntry()) != null) {
          File file = new File(directory, entry.getName());
          File fileDirectory = new File(file.getParent());
          if (!fileDirectory.exists()) {
            boolean created = fileDirectory.mkdirs();
            log(Log.INFO, String.format(Locale.ENGLISH, "%s is created, %s.", fileDirectory.getAbsolutePath(),
                created));
          }
          FileOutputStream out = new FileOutputStream(file);
          int cursor;
          while ((cursor = stream.read(buffer)) != -1) {
            out.write(buffer, 0, cursor);
          }
          out.close();
        }
      } finally {
        try {
          stream.closeEntry();
          stream.close();
        } catch (IOException ignored) { }
      }
    } catch (IOException error) { }
  }
}