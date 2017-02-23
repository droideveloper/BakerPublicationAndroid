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
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import io.reactivex.Observable;
import io.reactivex.Single;
import java.sql.SQLException;
import java.util.List;
import java8.util.function.Predicate;
import org.fs.core.AbstractOrmliteHelper;
import org.fs.magazine.BuildConfig;
import org.fs.magazine.R;
import org.fs.magazine.entities.BakerMagazine;

public final class BakerStorageImp extends AbstractOrmliteHelper implements BakerStorage {

  private final static String DB_NAME = "baker-storage.db";
  private final static int DB_VERSION = 1;

  private RuntimeExceptionDao<BakerMagazine, Long> bakerMagazines;

  public BakerStorageImp(Context context) {
    super(context, DB_NAME, DB_VERSION, R.raw.ormlite_config);
  }

  @Override protected void createTables(ConnectionSource conn) throws SQLException {
    TableUtils.createTable(conn, BakerMagazine.class);
  }

  @Override protected void dropTables(ConnectionSource conn) throws SQLException {
    TableUtils.dropTable(conn, BakerMagazine.class, false);
  }

  @Override public Observable<List<BakerMagazine>> all() {
    return Observable.just(createIfDaoNotExists())
        .map(RuntimeExceptionDao::queryForAll);
  }

  @Override public Single<BakerMagazine> firstOrDefault(BakerMagazine defaultValue, Predicate<BakerMagazine> where) {
    return all()
        .flatMap(Observable::fromIterable)
        .filter(where::test)
        .first(BakerMagazine.INVALID_ITEM);
  }

  @Override public Observable<Boolean> create(BakerMagazine magazine) {
    return Observable.just(createIfDaoNotExists())
        .map(dbSet -> dbSet.create(magazine) != -1);
  }

  @Override public Observable<Boolean> update(BakerMagazine magazine) {
    return Observable.just(createIfDaoNotExists())
        .map(dbSet -> dbSet.update(magazine) != -1);
  }

  @Override public Observable<Boolean> delete(BakerMagazine magazine) {
    return Observable.just(createIfDaoNotExists())
        .map(dbSet -> dbSet.delete(magazine) != -1);
  }

  @Override protected boolean isLogEnabled() {
    return BuildConfig.DEBUG;
  }

  @Override protected String getClassTag() {
    return BakerStorageImp.class.getSimpleName();
  }

  private RuntimeExceptionDao<BakerMagazine, Long> createIfDaoNotExists() {
    if (bakerMagazines == null) {
      bakerMagazines = getRuntimeExceptionDao(BakerMagazine.class);
    }
    return bakerMagazines;
  }
}
