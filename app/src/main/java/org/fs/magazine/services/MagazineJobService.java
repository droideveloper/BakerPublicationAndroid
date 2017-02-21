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
package org.fs.magazine.services;

import android.support.annotation.NonNull;
import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.scheduling.FrameworkJobSchedulerService;
import javax.inject.Inject;
import org.fs.magazine.BakerApplication;
import org.fs.magazine.commons.components.DaggerServiceComponent;

public class MagazineJobService extends FrameworkJobSchedulerService {

  @Inject JobManager jobManager;

  @Override public void onCreate() {
    super.onCreate();
    BakerApplication baker = bakerApplication();
    DaggerServiceComponent.builder()
        .appComponent(baker.component())
        .build()
        .inject(this);
  }

  @NonNull @Override protected JobManager getJobManager() {
    return jobManager;
  }

  private final BakerApplication bakerApplication() {
    return (BakerApplication) getApplication();
  }
}
