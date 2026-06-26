package com.example.androidclick.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.example.androidclick.domain.model.ClickMode;
import com.example.androidclick.domain.model.ClickPoint;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ClickScriptDao_Impl implements ClickScriptDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ClickScriptEntity> __insertionAdapterOfClickScriptEntity;

  private final ClickerConverters __clickerConverters = new ClickerConverters();

  private final EntityDeletionOrUpdateAdapter<ClickScriptEntity> __deletionAdapterOfClickScriptEntity;

  private final EntityDeletionOrUpdateAdapter<ClickScriptEntity> __updateAdapterOfClickScriptEntity;

  public ClickScriptDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfClickScriptEntity = new EntityInsertionAdapter<ClickScriptEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `click_scripts` (`id`,`name`,`clickMode`,`intervalMs`,`intervalRandom`,`intervalMaxMs`,`repeatCount`,`points`,`createdAt`,`updatedAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ClickScriptEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        final String _tmp = __clickerConverters.fromClickMode(entity.getClickMode());
        statement.bindString(3, _tmp);
        statement.bindLong(4, entity.getIntervalMs());
        final int _tmp_1 = entity.getIntervalRandom() ? 1 : 0;
        statement.bindLong(5, _tmp_1);
        if (entity.getIntervalMaxMs() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getIntervalMaxMs());
        }
        statement.bindLong(7, entity.getRepeatCount());
        final String _tmp_2 = __clickerConverters.fromClickPointList(entity.getPoints());
        statement.bindString(8, _tmp_2);
        statement.bindLong(9, entity.getCreatedAt());
        statement.bindLong(10, entity.getUpdatedAt());
      }
    };
    this.__deletionAdapterOfClickScriptEntity = new EntityDeletionOrUpdateAdapter<ClickScriptEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `click_scripts` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ClickScriptEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfClickScriptEntity = new EntityDeletionOrUpdateAdapter<ClickScriptEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `click_scripts` SET `id` = ?,`name` = ?,`clickMode` = ?,`intervalMs` = ?,`intervalRandom` = ?,`intervalMaxMs` = ?,`repeatCount` = ?,`points` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ClickScriptEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        final String _tmp = __clickerConverters.fromClickMode(entity.getClickMode());
        statement.bindString(3, _tmp);
        statement.bindLong(4, entity.getIntervalMs());
        final int _tmp_1 = entity.getIntervalRandom() ? 1 : 0;
        statement.bindLong(5, _tmp_1);
        if (entity.getIntervalMaxMs() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getIntervalMaxMs());
        }
        statement.bindLong(7, entity.getRepeatCount());
        final String _tmp_2 = __clickerConverters.fromClickPointList(entity.getPoints());
        statement.bindString(8, _tmp_2);
        statement.bindLong(9, entity.getCreatedAt());
        statement.bindLong(10, entity.getUpdatedAt());
        statement.bindLong(11, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final ClickScriptEntity entity,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfClickScriptEntity.insertAndReturnId(entity);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final ClickScriptEntity entity,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfClickScriptEntity.handle(entity);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final ClickScriptEntity entity,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfClickScriptEntity.handle(entity);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getById(final long id, final Continuation<? super ClickScriptEntity> $completion) {
    final String _sql = "SELECT * FROM click_scripts WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ClickScriptEntity>() {
      @Override
      @Nullable
      public ClickScriptEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfClickMode = CursorUtil.getColumnIndexOrThrow(_cursor, "clickMode");
          final int _cursorIndexOfIntervalMs = CursorUtil.getColumnIndexOrThrow(_cursor, "intervalMs");
          final int _cursorIndexOfIntervalRandom = CursorUtil.getColumnIndexOrThrow(_cursor, "intervalRandom");
          final int _cursorIndexOfIntervalMaxMs = CursorUtil.getColumnIndexOrThrow(_cursor, "intervalMaxMs");
          final int _cursorIndexOfRepeatCount = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatCount");
          final int _cursorIndexOfPoints = CursorUtil.getColumnIndexOrThrow(_cursor, "points");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final ClickScriptEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final ClickMode _tmpClickMode;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfClickMode);
            _tmpClickMode = __clickerConverters.toClickMode(_tmp);
            final long _tmpIntervalMs;
            _tmpIntervalMs = _cursor.getLong(_cursorIndexOfIntervalMs);
            final boolean _tmpIntervalRandom;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIntervalRandom);
            _tmpIntervalRandom = _tmp_1 != 0;
            final Long _tmpIntervalMaxMs;
            if (_cursor.isNull(_cursorIndexOfIntervalMaxMs)) {
              _tmpIntervalMaxMs = null;
            } else {
              _tmpIntervalMaxMs = _cursor.getLong(_cursorIndexOfIntervalMaxMs);
            }
            final int _tmpRepeatCount;
            _tmpRepeatCount = _cursor.getInt(_cursorIndexOfRepeatCount);
            final List<ClickPoint> _tmpPoints;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfPoints);
            _tmpPoints = __clickerConverters.toClickPointList(_tmp_2);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new ClickScriptEntity(_tmpId,_tmpName,_tmpClickMode,_tmpIntervalMs,_tmpIntervalRandom,_tmpIntervalMaxMs,_tmpRepeatCount,_tmpPoints,_tmpCreatedAt,_tmpUpdatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAll(final Continuation<? super List<ClickScriptEntity>> $completion) {
    final String _sql = "SELECT * FROM click_scripts ORDER BY updatedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ClickScriptEntity>>() {
      @Override
      @NonNull
      public List<ClickScriptEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfClickMode = CursorUtil.getColumnIndexOrThrow(_cursor, "clickMode");
          final int _cursorIndexOfIntervalMs = CursorUtil.getColumnIndexOrThrow(_cursor, "intervalMs");
          final int _cursorIndexOfIntervalRandom = CursorUtil.getColumnIndexOrThrow(_cursor, "intervalRandom");
          final int _cursorIndexOfIntervalMaxMs = CursorUtil.getColumnIndexOrThrow(_cursor, "intervalMaxMs");
          final int _cursorIndexOfRepeatCount = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatCount");
          final int _cursorIndexOfPoints = CursorUtil.getColumnIndexOrThrow(_cursor, "points");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<ClickScriptEntity> _result = new ArrayList<ClickScriptEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ClickScriptEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final ClickMode _tmpClickMode;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfClickMode);
            _tmpClickMode = __clickerConverters.toClickMode(_tmp);
            final long _tmpIntervalMs;
            _tmpIntervalMs = _cursor.getLong(_cursorIndexOfIntervalMs);
            final boolean _tmpIntervalRandom;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIntervalRandom);
            _tmpIntervalRandom = _tmp_1 != 0;
            final Long _tmpIntervalMaxMs;
            if (_cursor.isNull(_cursorIndexOfIntervalMaxMs)) {
              _tmpIntervalMaxMs = null;
            } else {
              _tmpIntervalMaxMs = _cursor.getLong(_cursorIndexOfIntervalMaxMs);
            }
            final int _tmpRepeatCount;
            _tmpRepeatCount = _cursor.getInt(_cursorIndexOfRepeatCount);
            final List<ClickPoint> _tmpPoints;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfPoints);
            _tmpPoints = __clickerConverters.toClickPointList(_tmp_2);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new ClickScriptEntity(_tmpId,_tmpName,_tmpClickMode,_tmpIntervalMs,_tmpIntervalRandom,_tmpIntervalMaxMs,_tmpRepeatCount,_tmpPoints,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ClickScriptEntity>> observeAll() {
    final String _sql = "SELECT * FROM click_scripts ORDER BY updatedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"click_scripts"}, new Callable<List<ClickScriptEntity>>() {
      @Override
      @NonNull
      public List<ClickScriptEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfClickMode = CursorUtil.getColumnIndexOrThrow(_cursor, "clickMode");
          final int _cursorIndexOfIntervalMs = CursorUtil.getColumnIndexOrThrow(_cursor, "intervalMs");
          final int _cursorIndexOfIntervalRandom = CursorUtil.getColumnIndexOrThrow(_cursor, "intervalRandom");
          final int _cursorIndexOfIntervalMaxMs = CursorUtil.getColumnIndexOrThrow(_cursor, "intervalMaxMs");
          final int _cursorIndexOfRepeatCount = CursorUtil.getColumnIndexOrThrow(_cursor, "repeatCount");
          final int _cursorIndexOfPoints = CursorUtil.getColumnIndexOrThrow(_cursor, "points");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<ClickScriptEntity> _result = new ArrayList<ClickScriptEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ClickScriptEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final ClickMode _tmpClickMode;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfClickMode);
            _tmpClickMode = __clickerConverters.toClickMode(_tmp);
            final long _tmpIntervalMs;
            _tmpIntervalMs = _cursor.getLong(_cursorIndexOfIntervalMs);
            final boolean _tmpIntervalRandom;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIntervalRandom);
            _tmpIntervalRandom = _tmp_1 != 0;
            final Long _tmpIntervalMaxMs;
            if (_cursor.isNull(_cursorIndexOfIntervalMaxMs)) {
              _tmpIntervalMaxMs = null;
            } else {
              _tmpIntervalMaxMs = _cursor.getLong(_cursorIndexOfIntervalMaxMs);
            }
            final int _tmpRepeatCount;
            _tmpRepeatCount = _cursor.getInt(_cursorIndexOfRepeatCount);
            final List<ClickPoint> _tmpPoints;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfPoints);
            _tmpPoints = __clickerConverters.toClickPointList(_tmp_2);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new ClickScriptEntity(_tmpId,_tmpName,_tmpClickMode,_tmpIntervalMs,_tmpIntervalRandom,_tmpIntervalMaxMs,_tmpRepeatCount,_tmpPoints,_tmpCreatedAt,_tmpUpdatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
