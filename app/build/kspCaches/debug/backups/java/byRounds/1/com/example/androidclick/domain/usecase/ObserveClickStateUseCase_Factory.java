package com.example.androidclick.domain.usecase;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation"
})
public final class ObserveClickStateUseCase_Factory implements Factory<ObserveClickStateUseCase> {
  @Override
  public ObserveClickStateUseCase get() {
    return newInstance();
  }

  public static ObserveClickStateUseCase_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static ObserveClickStateUseCase newInstance() {
    return new ObserveClickStateUseCase();
  }

  private static final class InstanceHolder {
    private static final ObserveClickStateUseCase_Factory INSTANCE = new ObserveClickStateUseCase_Factory();
  }
}
