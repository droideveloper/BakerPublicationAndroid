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
package org.fs.magazine.commons.components;

import com.google.gson.Gson;
import dagger.Component;
import javax.inject.Singleton;
import org.fs.magazine.commons.BakerFile;
import org.fs.magazine.commons.BakerService;
import org.fs.magazine.commons.BakerStorage;
import org.fs.magazine.commons.modules.AppModule;
import retrofit2.Retrofit;

@Singleton @Component(modules = AppModule.class)
public interface AppComponent {
  // shared for future use
  Gson serializer();
  Retrofit retrofit();
  // services
  BakerStorage storage();
  BakerService service();
  BakerFile file();
}