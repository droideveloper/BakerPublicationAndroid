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

import dagger.Component;
import org.fs.magazine.commons.scopes.PerService;
import org.fs.magazine.services.MagazineGCMJobService;
import org.fs.magazine.services.MagazineJobService;

@PerService
@Component(dependencies = AppComponent.class)
public interface ServiceComponent {
  void inject(MagazineGCMJobService service);
  void inject(MagazineJobService service);
}
