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
package org.fs.magazine.entities;

import android.os.Parcel;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.util.Date;
import org.fs.core.AbstractEntity;
import org.fs.magazine.BuildConfig;
import org.fs.util.Objects;

@DatabaseTable public final class BakerMagazine extends AbstractEntity {

  @DatabaseField(generatedId = true) private Long id;
  @DatabaseField private String localStorage;
  @DatabaseField private long   localSize;
  @DatabaseField private long   remoteSize;
  @DatabaseField private Date   createdAt;
  @DatabaseField private Date   updatedAt;

  public final static BakerMagazine INVALID_ITEM = new BakerMagazine(null, null, Long.MIN_VALUE, Long.MIN_VALUE, null, null);

  public BakerMagazine() {/*default constructor*/}

  private BakerMagazine(Parcel input) {
    super(input);
  }

  private BakerMagazine(Long id, String localStorage, long localSize, long remoteSize, Date createdAt, Date updatedAt) {
    this.id = id;
    this.localStorage = localStorage;
    this.localSize = localSize;
    this.remoteSize = remoteSize;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  @Override protected void readParcel(Parcel input) {
    boolean hasId = input.readInt() == 1;
    if (hasId) {
      id = input.readLong();
    }
    boolean hasLocalStorage = input.readInt() == 1;
    if (hasLocalStorage) {
      localStorage = input.readString();
    }
    localSize = input.readLong();
    remoteSize = input.readLong();
    boolean hasCreatedAt = input.readInt() == 1;
    if (hasCreatedAt) {
      createdAt = new Date(input.readLong());
    }
    boolean hasUpdatedAt = input.readInt() == 1;
    if (hasUpdatedAt) {
      updatedAt = new Date(input.readLong());
    }
  }

  @Override public void writeToParcel(Parcel out, int flags) {
    boolean hasId = !Objects.isNullOrEmpty(id);
    out.writeInt(hasId ? 1 : 0);
    if (hasId) {
      out.writeLong(id);
    }
    boolean hasLocalStorage = !Objects.isNullOrEmpty(localStorage);
    out.writeInt(hasLocalStorage ? 1 : 0);
    if (hasLocalStorage) {
      out.writeString(localStorage);
    }
    out.writeLong(localSize);
    out.writeLong(remoteSize);
    boolean hasCreatedAt = !Objects.isNullOrEmpty(createdAt);
    out.writeInt(hasCreatedAt ? 1 : 0);
    if (hasCreatedAt) {
      out.writeLong(createdAt.getTime());
    }
    boolean hasUpdatedAt = !Objects.isNullOrEmpty(updatedAt);
    out.writeInt(hasUpdatedAt ? 1 : 0);
    if (hasUpdatedAt) {
      out.writeLong(updatedAt.getTime());
    }
  }

  public Long id() {
    return id;
  }

  public String localStorage() {
    return localStorage;
  }

  public long localSize() {
    return localSize;
  }

  public long remoteSize() {
    return remoteSize;
  }

  public Date createdAt() {
    return createdAt;
  }

  public Date updatedAt() {
    return updatedAt;
  }

  @Override protected String getClassTag() {
    return BakerMagazine.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return BuildConfig.DEBUG;
  }

  @Override public int describeContents() {
    return 0;
  }

  public Builder newBuilder() {
    return new Builder()
        .id(id)
        .localStorage(localStorage)
        .localSize(localSize)
        .remoteSize(remoteSize)
        .createdAt(createdAt)
        .updatedAt(updatedAt);
  }

  public final static Creator<BakerMagazine> CREATOR = new Creator<BakerMagazine>() {

    @Override public BakerMagazine createFromParcel(Parcel input) {
      return new BakerMagazine(input);
    }

    @Override public BakerMagazine[] newArray(int size) {
      return new BakerMagazine[size];
    }
  };

  public static class Builder {
    private Long id;
    private String localStorage;
    private long localSize;
    private long remoteSize;
    private Date createdAt;
    private Date updatedAt;

    public Builder() { }
    public Builder id(Long id) { this.id = id; return this; }
    public Builder localStorage(String localStorage) { this.localStorage = localStorage; return this; }
    public Builder localSize(long localSize) { this.localSize = localSize; return this; }
    public Builder remoteSize(long remoteSize) { this.remoteSize = remoteSize; return this; }
    public Builder createdAt(Date createdAt) { this.createdAt = createdAt; return this; }
    public Builder updatedAt(Date updatedAt) { this.updatedAt = updatedAt; return this; }

    public BakerMagazine build() {
      return new BakerMagazine(id, localStorage, localSize, remoteSize, createdAt, updatedAt);
    }
  }
}