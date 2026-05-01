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
 * Author: wyouflf
 * Date: 13-7-24
 * Time: 下午3:00
 */
public class BaseException extends Exception
{
    private static final long serialVersionUID = 1L;
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @see [类、类#方法、类#成员]
     */
    public BaseException()
    {
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param detailMessage detailMessage
     * @see [类、类#方法、类#成员]
     */
    public BaseException(final String detailMessage)
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
    public BaseException(final String detailMessage, final Throwable throwable)
    {
        super(detailMessage, throwable);
    }
    
    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param throwable throwable
     * @see [类、类#方法、类#成员]
     */
    public BaseException(final Throwable throwable)
    {
        super(throwable);
    }
}