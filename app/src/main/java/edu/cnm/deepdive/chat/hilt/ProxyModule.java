package edu.cnm.deepdive.chat.hilt;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import javax.inject.Inject;
import javax.inject.Singleton;

@Module
@InstallIn(SingletonComponent.class)
public class ProxyModule {

  @Inject
  ProxyModule() {}

//writing methods to build GSON object, ie register type adapter. For deserializing incoming and outgoing data.
//we will only pay attention to fields with @Expose annotation
  //create builder objects (GsonBuilder) and invoke methods on the builder object (.exclude, .create, etc),
// to return the object we want to build
  @Provides
  @Singleton
  Gson provideGson() {
    return new GsonBuilder()
    // TODO: 3/12/25 Register type adapters as necessary (e.g., for UUID, Instant)
        .excludeFieldsWithoutExposeAnnotation() //returns fields we've created already
        .create();  //returns new fields
  }






}
