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

/**
 * DbException
 * <一句话功能简述>
 * <功能详细描述>
 * 
 * @author  s00223601
 * @version  [版本号, 2015-2-28]
 * @since  [产品/模块版本]
 */
public class DbException extends BaseException
{
    private static final long serialVersionUID = 1L;
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @see [类、类#方法、类#成员]
     */
    public DbException()
    {
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param detailMessage detailMessage
     * @see [类、类#方法、类#成员]
     */
    public DbException(final String detailMessage)
    {
        super(detailMessage);
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param detailMessage detailMessage
     * @param throwable throwable
     * @see [类、类#方法、类#成员]
     */
    public DbException(final String detailMessage, final Throwable throwable)
    {
        super(detailMessage, throwable);
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param throwable throwable
     * @see [类、类#方法、类#成员]
     */
    public DbException(final Throwable throwable)
    {
        super(throwable);
    }
}
