package com.zetalasis.commonloader.inject.api;

import com.zetalasis.commonloader.Main;
import com.zetalasis.commonloader.inject.BrandingInject;
import com.zetalasis.commonloader.inject.ClientInject;
import com.zetalasis.commonloader.inject.FabricClientInject;
import com.zetalasis.commonloader.inject.TitleScreenInject;

import java.util.HashSet;

public class InjectRegistry {
    public static HashSet<Class<?>> REGISTRAR = new HashSet<>();

    public static void register(Class<?> clazz)
    {
        REGISTRAR.add(clazz);
    }

    public static void bootstrap()
    {
        Main.LOGGER.info("Bootstrapping Injectables...");

        try {
            Class.forName("com.zetalasis.commonloader.inject.api.Injectable");
            Class.forName("com.zetalasis.commonloader.inject.api.MethodInject");

            register(ClientInject.class);
            register(FabricClientInject.class);
            register(BrandingInject.class);
            register(TitleScreenInject.class);
//            Class.forName("com.zetalasis.commonloader.inject.ClientInject", true, ClassLoader.getSystemClassLoader());
//            Class.forName("com.zetalasis.commonloader.inject.FabricClientInject", true, ClassLoader.getSystemClassLoader());
        } catch (Exception e) {
            throw new RuntimeException("Failed to load injectables", e);
        }
    }

    public static void loadRequired()
    {
        for (Class<?> clazz : REGISTRAR)
        {
            try {
                Injectable injectable = clazz.getAnnotation(Injectable.class);
                if (injectable == null)
                    return;

                Class.forName(injectable.classFQN());
                REGISTRAR.add(clazz);
                Main.LOGGER.info("Created patch \"{}\"", clazz.getSimpleName());
            } catch (Exception e) {
                Main.LOGGER.warn("Failed to load class for injectable \"{}\", not adding to registrar", clazz.getSimpleName());
                REGISTRAR.remove(clazz);
            }
        }
    }
}