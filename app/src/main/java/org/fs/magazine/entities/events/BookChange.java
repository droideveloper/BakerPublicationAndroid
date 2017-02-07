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
package org.fs.magazine.entities.events;

import org.fs.common.IEvent;
import org.fs.publication.entities.Book;

public final class BookChange implements IEvent {

  public enum Action {
    START,
    STOP,
    VIEW,
    DELETE
  }

  private final Book book;
  private final Action action;

  public BookChange(final Book book, final Action action) {
    this.book = book;
    this.action = action;
  }

  public final Book book() {
    return this.book;
  }

  public final Action action() {
    return this.action;
  }
}