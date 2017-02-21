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
package org.fs.magazine.commons.modules;

import android.app.Application;
import android.os.Build;
import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;
import com.birbit.android.jobqueue.scheduling.FrameworkJobSchedulerService;
import com.birbit.android.jobqueue.scheduling.GcmJobSchedulerService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import org.fs.magazine.commons.BakerFile;
import org.fs.magazine.commons.BakerFileImp;
import org.fs.magazine.commons.BakerService;
import org.fs.magazine.commons.BakerStorage;
import org.fs.magazine.commons.BakerStorageImp;
import org.fs.magazine.services.MagazineGCMJobService;
import org.fs.magazine.services.MagazineJobService;
import org.fs.net.RxJavaCallAdapterFactory;
import org.fs.net.converter.GsonConverterFactory;
import retrofit2.Retrofit;

@Module public class AppModule {

  private final static int MIN_CONSUMER   = 0x01;
  private final static int MAX_CONSUMER   = 0x03;
  private final static int LOAD_FACTORY   = 0x03;
  private final static int MAX_KEEP_ALIVE = 0x78; // 120secs =)

  private final Application application;

  private final String uri;
  private final String dateFormat;

  public AppModule(final Application application, final String uri, final String dateFormat) {
    this.application = application;
    this.uri = uri;
    this.dateFormat = dateFormat;
  }

  @Singleton @Provides public Gson gson() {
    return new GsonBuilder()
      .setDateFormat(dateFormat)
      .create();
  }

  @Singleton @Provides public Retrofit retrofit(Gson gson) {
    return new Retrofit.Builder()
      .baseUrl(uri)
      .addConverterFactory(GsonConverterFactory.createWithGson(gson))
      .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
      .build();
  }

  @Singleton @Provides public BakerFile bakerFile(Gson serializer) {
    return new BakerFileImp(application, serializer);
  }

  @Singleton @Provides public BakerStorage bakerStorage() {
    return new BakerStorageImp(application);
  }

  @Singleton @Provides public BakerService bakerService(Retrofit retrofit) {
    return retrofit.create(BakerService.class);
  }

  @Singleton @Provides public JobManager jobManager() {
    Configuration.Builder config = new Configuration.Builder(application)
        .minConsumerCount(MIN_CONSUMER)
        .maxConsumerCount(MAX_CONSUMER)
        .loadFactor(LOAD_FACTORY)
        .consumerKeepAlive(MAX_KEEP_ALIVE);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      config.scheduler(FrameworkJobSchedulerService.createSchedulerFor(application,
          MagazineJobService.class), true);
    } else {
      int isGCMAvailable = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(application);
      if (isGCMAvailable == ConnectionResult.SUCCESS) {
        config.scheduler(GcmJobSchedulerService.createSchedulerFor(application,
            MagazineGCMJobService.class), true);
      }
    }
    return new JobManager(config.build());
  }
}