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
package org.fs.magazine;

import org.fs.core.AbstractApplication;
import org.fs.magazine.commons.components.AppComponent;
import org.fs.magazine.commons.components.DaggerAppComponent;
import org.fs.magazine.commons.modules.AppModule;

public class BakerApplication extends AbstractApplication {

  private final static String URL         = "http://bakerframework.com/";
  private final static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

  private AppComponent component;

  public BakerApplication() {
    super(BuildConfig.DEBUG);
  }

  @Override public void onCreate() {
    super.onCreate();
    component = DaggerAppComponent.builder()
      .appModule(new AppModule(this, URL, DATE_FORMAT))
      .build();
  }

  @Override protected String getClassTag() {
    return BakerApplication.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return BuildConfig.DEBUG;
  }

  public final AppComponent component() {
    return component;
  }
}