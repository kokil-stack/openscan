package com.quickscanner.app.data.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.quickscanner.app.data.model.ScanDocument;
import com.quickscanner.app.data.model.StringListConverter;
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
public final class ScanDocumentDao_Impl implements ScanDocumentDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ScanDocument> __insertionAdapterOfScanDocument;

  private final StringListConverter __stringListConverter = new StringListConverter();

  private final EntityDeletionOrUpdateAdapter<ScanDocument> __deletionAdapterOfScanDocument;

  private final EntityDeletionOrUpdateAdapter<ScanDocument> __updateAdapterOfScanDocument;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  public ScanDocumentDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfScanDocument = new EntityInsertionAdapter<ScanDocument>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `scanned_documents` (`id`,`title`,`pagePaths`,`pdfPath`,`pageCount`,`createdAt`,`ocrText`,`isFavorite`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ScanDocument entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        final String _tmp = __stringListConverter.fromList(entity.getPagePaths());
        statement.bindString(3, _tmp);
        if (entity.getPdfPath() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getPdfPath());
        }
        statement.bindLong(5, entity.getPageCount());
        statement.bindLong(6, entity.getCreatedAt());
        if (entity.getOcrText() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getOcrText());
        }
        final int _tmp_1 = entity.isFavorite() ? 1 : 0;
        statement.bindLong(8, _tmp_1);
      }
    };
    this.__deletionAdapterOfScanDocument = new EntityDeletionOrUpdateAdapter<ScanDocument>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `scanned_documents` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ScanDocument entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfScanDocument = new EntityDeletionOrUpdateAdapter<ScanDocument>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `scanned_documents` SET `id` = ?,`title` = ?,`pagePaths` = ?,`pdfPath` = ?,`pageCount` = ?,`createdAt` = ?,`ocrText` = ?,`isFavorite` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ScanDocument entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        final String _tmp = __stringListConverter.fromList(entity.getPagePaths());
        statement.bindString(3, _tmp);
        if (entity.getPdfPath() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getPdfPath());
        }
        statement.bindLong(5, entity.getPageCount());
        statement.bindLong(6, entity.getCreatedAt());
        if (entity.getOcrText() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getOcrText());
        }
        final int _tmp_1 = entity.isFavorite() ? 1 : 0;
        statement.bindLong(8, _tmp_1);
        statement.bindLong(9, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM scanned_documents WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertDocument(final ScanDocument doc,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfScanDocument.insertAndReturnId(doc);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteDocument(final ScanDocument doc,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfScanDocument.handle(doc);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateDocument(final ScanDocument doc,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfScanDocument.handle(doc);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteById(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteById.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ScanDocument>> getAllDocuments() {
    final String _sql = "SELECT * FROM scanned_documents ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"scanned_documents"}, new Callable<List<ScanDocument>>() {
      @Override
      @NonNull
      public List<ScanDocument> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfPagePaths = CursorUtil.getColumnIndexOrThrow(_cursor, "pagePaths");
          final int _cursorIndexOfPdfPath = CursorUtil.getColumnIndexOrThrow(_cursor, "pdfPath");
          final int _cursorIndexOfPageCount = CursorUtil.getColumnIndexOrThrow(_cursor, "pageCount");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfOcrText = CursorUtil.getColumnIndexOrThrow(_cursor, "ocrText");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final List<ScanDocument> _result = new ArrayList<ScanDocument>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ScanDocument _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final List<String> _tmpPagePaths;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfPagePaths);
            _tmpPagePaths = __stringListConverter.toList(_tmp);
            final String _tmpPdfPath;
            if (_cursor.isNull(_cursorIndexOfPdfPath)) {
              _tmpPdfPath = null;
            } else {
              _tmpPdfPath = _cursor.getString(_cursorIndexOfPdfPath);
            }
            final int _tmpPageCount;
            _tmpPageCount = _cursor.getInt(_cursorIndexOfPageCount);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpOcrText;
            if (_cursor.isNull(_cursorIndexOfOcrText)) {
              _tmpOcrText = null;
            } else {
              _tmpOcrText = _cursor.getString(_cursorIndexOfOcrText);
            }
            final boolean _tmpIsFavorite;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp_1 != 0;
            _item = new ScanDocument(_tmpId,_tmpTitle,_tmpPagePaths,_tmpPdfPath,_tmpPageCount,_tmpCreatedAt,_tmpOcrText,_tmpIsFavorite);
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

  @Override
  public Object getDocumentById(final long id,
      final Continuation<? super ScanDocument> $completion) {
    final String _sql = "SELECT * FROM scanned_documents WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ScanDocument>() {
      @Override
      @Nullable
      public ScanDocument call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfPagePaths = CursorUtil.getColumnIndexOrThrow(_cursor, "pagePaths");
          final int _cursorIndexOfPdfPath = CursorUtil.getColumnIndexOrThrow(_cursor, "pdfPath");
          final int _cursorIndexOfPageCount = CursorUtil.getColumnIndexOrThrow(_cursor, "pageCount");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfOcrText = CursorUtil.getColumnIndexOrThrow(_cursor, "ocrText");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final ScanDocument _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final List<String> _tmpPagePaths;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfPagePaths);
            _tmpPagePaths = __stringListConverter.toList(_tmp);
            final String _tmpPdfPath;
            if (_cursor.isNull(_cursorIndexOfPdfPath)) {
              _tmpPdfPath = null;
            } else {
              _tmpPdfPath = _cursor.getString(_cursorIndexOfPdfPath);
            }
            final int _tmpPageCount;
            _tmpPageCount = _cursor.getInt(_cursorIndexOfPageCount);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpOcrText;
            if (_cursor.isNull(_cursorIndexOfOcrText)) {
              _tmpOcrText = null;
            } else {
              _tmpOcrText = _cursor.getString(_cursorIndexOfOcrText);
            }
            final boolean _tmpIsFavorite;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp_1 != 0;
            _result = new ScanDocument(_tmpId,_tmpTitle,_tmpPagePaths,_tmpPdfPath,_tmpPageCount,_tmpCreatedAt,_tmpOcrText,_tmpIsFavorite);
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
  public Flow<List<ScanDocument>> searchDocuments(final String query) {
    final String _sql = "SELECT * FROM scanned_documents WHERE title LIKE '%' || ? || '%' OR ocrText LIKE '%' || ? || '%' ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    _argIndex = 2;
    _statement.bindString(_argIndex, query);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"scanned_documents"}, new Callable<List<ScanDocument>>() {
      @Override
      @NonNull
      public List<ScanDocument> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfPagePaths = CursorUtil.getColumnIndexOrThrow(_cursor, "pagePaths");
          final int _cursorIndexOfPdfPath = CursorUtil.getColumnIndexOrThrow(_cursor, "pdfPath");
          final int _cursorIndexOfPageCount = CursorUtil.getColumnIndexOrThrow(_cursor, "pageCount");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfOcrText = CursorUtil.getColumnIndexOrThrow(_cursor, "ocrText");
          final int _cursorIndexOfIsFavorite = CursorUtil.getColumnIndexOrThrow(_cursor, "isFavorite");
          final List<ScanDocument> _result = new ArrayList<ScanDocument>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ScanDocument _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final List<String> _tmpPagePaths;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfPagePaths);
            _tmpPagePaths = __stringListConverter.toList(_tmp);
            final String _tmpPdfPath;
            if (_cursor.isNull(_cursorIndexOfPdfPath)) {
              _tmpPdfPath = null;
            } else {
              _tmpPdfPath = _cursor.getString(_cursorIndexOfPdfPath);
            }
            final int _tmpPageCount;
            _tmpPageCount = _cursor.getInt(_cursorIndexOfPageCount);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpOcrText;
            if (_cursor.isNull(_cursorIndexOfOcrText)) {
              _tmpOcrText = null;
            } else {
              _tmpOcrText = _cursor.getString(_cursorIndexOfOcrText);
            }
            final boolean _tmpIsFavorite;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsFavorite);
            _tmpIsFavorite = _tmp_1 != 0;
            _item = new ScanDocument(_tmpId,_tmpTitle,_tmpPagePaths,_tmpPdfPath,_tmpPageCount,_tmpCreatedAt,_tmpOcrText,_tmpIsFavorite);
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
