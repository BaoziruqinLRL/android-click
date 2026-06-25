package com.example.androidclick.ui.home;

import com.example.androidclick.domain.usecase.ObserveClickStateUseCase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<ObserveClickStateUseCase> observeClickStateProvider;

  public HomeViewModel_Factory(Provider<ObserveClickStateUseCase> observeClickStateProvider) {
    this.observeClickStateProvider = observeClickStateProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(observeClickStateProvider.get());
  }

  public static HomeViewModel_Factory create(
      Provider<ObserveClickStateUseCase> observeClickStateProvider) {
    return new HomeViewModel_Factory(observeClickStateProvider);
  }

  public static HomeViewModel newInstance(ObserveClickStateUseCase observeClickState) {
    return new HomeViewModel(observeClickState);
  }
}
