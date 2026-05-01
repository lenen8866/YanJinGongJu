/*
 * Copyright (c) 2013. wyouflf (wyouflf@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zxl.common.db.sqlite;

import android.database.Cursor;

import com.read.scriptures.util.LogUtil;

import java.util.concurrent.ConcurrentHashMap;

/**
 * <一句话功能简述>
 * <功能详细描述>
 *
 * @author
 * @version [版本号, 2015-2-28]
 * @since [产品/模块版本]
 */
public class CursorUtils {

    /**
     * <默认构造函数>
     */
    private CursorUtils() {

    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     *
     * @param db                db
     * @param cursor            cursor
     * @param entityType        entityType
     * @param findCacheSequence findCacheSequence
     * @param <T>               T
     * @return getEntity
     * @see [类、类#方法、类#成员]
     */
    public static <T> T getEntity(final DbUtils db, final Cursor cursor, final Class<T> entityType, final long findCacheSequence) {
        if ((db == null) || (cursor == null)) {
            return null;
        }

        EntityTempCache.setSeq(findCacheSequence);
        try {
            final Table table = Table.get(db, entityType);
            final Id id = table.getId();
            final String idColumnName = id.getColumnName();
            int idIndex = id.getIndex();
            if (idIndex < 0) {
                idIndex = cursor.getColumnIndex(idColumnName);
            }
            final Object idValue = id.getColumnConverter().getFieldValue(cursor, idIndex);
            T entity = EntityTempCache.get(entityType, idValue);
            if (entity == null) {
                entity = entityType.newInstance();
                id.setValue2Entity(entity, cursor, idIndex);
                EntityTempCache.put(entityType, idValue, entity);
            } else {
                return entity;
            }
            final int columnCount = cursor.getColumnCount();
            for (int i = 0; i < columnCount; i++) {
                final String columnName = cursor.getColumnName(i);
                final Column column = table.getColumnMap().get(columnName);
                if (column != null) {
                    column.setValue2Entity(entity, cursor, i);
                }
            }

            // init finder
            for (final Finder finder : table.getFinderMap().values()) {
                finder.setValue2Entity(entity, null, 0);
            }
            return entity;
        } catch (final InstantiationException e) {
            LogUtil.error("InstantiationException");
        } catch (final IllegalAccessException e) {
            LogUtil.error("IllegalAccessException");
        }
        return null;
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     *
     * @param cursor cursor
     * @return getDbModel
     * @see [类、类#方法、类#成员]
     */
    public static DbModel getDbModel(final Cursor cursor) {
        DbModel result = null;
        if (cursor != null) {
            result = new DbModel();
            final int columnCount = cursor.getColumnCount();
            for (int i = 0; i < columnCount; i++) {
                result.add(cursor.getColumnName(i), cursor.getString(i));
            }
        }
        return result;
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     *
     * @author
     * @version [版本号, 2015-2-28]
     * @since [产品/模块版本]
     */
    public static class FindCacheSequence {
        private static long seq = 0;

        private static final String FOREIGN_LAZY_LOADER_CLASS_NAME = ForeignLazyLoader.class.getName();

        private static final String FINDER_LAZY_LOADER_CLASS_NAME = FinderLazyLoader.class.getName();

        private FindCacheSequence() {
        }

        /**
         * <一句话功能简述>
         * <功能详细描述>
         *
         * @return getSeq
         * @see [类、类#方法、类#成员]
         */
        public static long getSeq() {
            final String findMethodCaller = Thread.currentThread().getStackTrace()[4].getClassName();
            if (!findMethodCaller.equals(FOREIGN_LAZY_LOADER_CLASS_NAME)
                    && !findMethodCaller.equals(FINDER_LAZY_LOADER_CLASS_NAME)) {
                ++seq;
            }
            return seq;
        }
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     *
     * @author
     * @version [版本号, 2015-2-28]
     * @since [产品/模块版本]
     */
    private static class EntityTempCache {

        private static final ConcurrentHashMap<String, Object> CACHE = new ConcurrentHashMap<String, Object>();

        private static long seq = 0;

        private EntityTempCache() {
        }

        public static <T> void put(final Class<T> entityType, final Object idValue, final Object entity) {
            CACHE.put(entityType.getName() + "#" + idValue, entity);
        }

        @SuppressWarnings("unchecked")
        public static <T> T get(final Class<T> entityType, final Object idValue) {
            return (T) CACHE.get(entityType.getName() + "#" + idValue);
        }

        public static void setSeq(final long seq) {
            if (EntityTempCache.seq != seq) {
                CACHE.clear();
                EntityTempCache.seq = seq;
            }
        }
    }
}
