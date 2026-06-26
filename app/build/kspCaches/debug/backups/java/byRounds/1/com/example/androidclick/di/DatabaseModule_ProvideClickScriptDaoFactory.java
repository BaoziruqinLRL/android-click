package com.example.androidclick.di;

import com.example.androidclick.data.local.AppDatabase;
import com.example.androidclick.data.local.ClickScriptDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideClickScriptDaoFactory implements Factory<ClickScriptDao> {
  private final Provider<AppDatabase> databaseProvider;

  public DatabaseModule_ProvideClickScriptDaoFactory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public ClickScriptDao get() {
    return provideClickScriptDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideClickScriptDaoFactory create(
      Provider<AppDatabase> databaseProvider) {
    return new DatabaseModule_ProvideClickScriptDaoFactory(databaseProvider);
  }

  public static ClickScriptDao provideClickScriptDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideClickScriptDao(database));
  }
}
