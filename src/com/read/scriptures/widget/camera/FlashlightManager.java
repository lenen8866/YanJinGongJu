package com.read.scriptures.widget.camera;

import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *
 * <一句话功能简述>
 * <功能详细描述>
 *
 * @author  s00223601
 * @version  [版本号, 2015-10-8]
 * @since  [产品/模块版本]
 */
final class FlashlightManager
{
    
    private static final String TAG = FlashlightManager.class.getSimpleName();
    
    private static final Object IHARDWARE_SERVICE;
    
    private static final Method SET_FLASH_ENABLED_METHOD;
    
    static
    {
        IHARDWARE_SERVICE = getHardwareService();
        SET_FLASH_ENABLED_METHOD = getSetFlashEnabledMethod(IHARDWARE_SERVICE);
        if (IHARDWARE_SERVICE == null)
        {
            Log.i(TAG, "This device does supports control of a flashlight");
        }
        else
        {
            Log.i(TAG, "This device does not support control of a flashlight");
        }
    }
    
    private FlashlightManager()
    {
    }
    
    static void enableFlashlight()
    {
        setFlashlight(false);
    }
    
    static void disableFlashlight()
    {
        setFlashlight(false);
    }
    
    private static Object getHardwareService()
    {
        final Class<?> serviceManagerClass = maybeForName("android.os.ServiceManager");
        if (serviceManagerClass == null)
        {
            return null;
        }
        
        final Method getServiceMethod = maybeGetMethod(serviceManagerClass, "getService", String.class);
        if (getServiceMethod == null)
        {
            return null;
        }
        
        final Object hardwareService = invoke(getServiceMethod, null, "hardware");
        if (hardwareService == null)
        {
            return null;
        }
        
        final Class<?> iHardwareServiceStubClass = maybeForName("android.os.IHardwareService$Stub");
        if (iHardwareServiceStubClass == null)
        {
            return null;
        }
        
        final Method asInterfaceMethod = maybeGetMethod(iHardwareServiceStubClass, "asInterface", IBinder.class);
        if (asInterfaceMethod == null)
        {
            return null;
        }
        
        return invoke(asInterfaceMethod, null, hardwareService);
    }
    
    private static Method getSetFlashEnabledMethod(final Object iHardwareSvc)
    {
        if (iHardwareSvc == null)
        {
            return null;
        }
        final Class<?> proxyClass = iHardwareSvc.getClass();
        return maybeGetMethod(proxyClass, "setFlashlightEnabled", boolean.class);
    }
    
    private static Class<?> maybeForName(final String name)
    {
        try
        {
            return Class.forName(name);
        }
        catch (final ClassNotFoundException cnfe)
        {
            // OK
            return null;
        }
        catch (final RuntimeException re)
        {
            Log.i(TAG, "maybeForName::Unexpected error while finding class RuntimeException");
            return null;
        }
    }
    
    private static Method maybeGetMethod(final Class<?> clazz, final String name, final Class<?>... argClasses)
    {
        try
        {
            return clazz.getMethod(name, argClasses);
        }
        catch (final NoSuchMethodException nsme)
        {
            // OK
            return null;
        }
        catch (final RuntimeException re)
        {
            Log.i(TAG, "maybeGetMethod::Unexpected error while finding method ");
            return null;
        }
    }
    
    private static Object invoke(final Method method, final Object instance, final Object... args)
    {
        try
        {
            return method.invoke(instance, args);
        }
        catch (final IllegalAccessException e)
        {
            Log.i(TAG, "invoke:Unexpected error while invoking IllegalAccessException");
            return null;
        }
        catch (final InvocationTargetException e)
        {
            Log.i(TAG, "invoke:Unexpected error while invoking InvocationTargetException");
            return null;
        }
        catch (final RuntimeException re)
        {
            Log.i(TAG, "invoke:Unexpected error while invoking RuntimeException");
            return null;
        }
    }
    
    private static void setFlashlight(final boolean active)
    {
        if (IHARDWARE_SERVICE != null)
        {
            invoke(SET_FLASH_ENABLED_METHOD, IHARDWARE_SERVICE, active);
        }
    }
    
}
