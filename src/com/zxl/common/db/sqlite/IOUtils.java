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

import java.io.Closeable;
import java.io.IOException;

/**
 * <一句话功能简述>
 *
 * @author yWX272422
 * @version V100R001C13, 2015-3-2
 * @since V100R001C13
 */
public final class IOUtils {

    private IOUtils() {
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     *
     * @param closeable closeable
     * @see [类、类#方法、类#成员]
     */
    public static void closeQuietly(final Closeable closeable) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (final IOException e) {
                LogUtil.error("closeQuietly closeable close failed");
            }
        }
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     *
     * @param cursor cursor
     * @see [类、类#方法、类#成员]
     */
    public static void closeQuietly(final Cursor cursor) {
        if (null != cursor) {
            cursor.close();
        }
    }
}
