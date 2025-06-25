package com.zetalasis.commonloader;

import com.zetalasis.commonloader.inject.api.InjectRegistry;
import com.zetalasis.commonloader.inject.api.InjectableClassTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static final Logger LOGGER = LoggerFactory.getLogger("CommonLoader");

    public static void premain(String args, Instrumentation inst) {
        LOGGER.info("Hello, World!");

        InjectRegistry.bootstrap();
        inst.addTransformer(new InjectableClassTransformer(), false);
        InjectRegistry.loadRequired();

//        try {
//            Class<?> client = Class.forName("enn");
//            LOGGER.info("[premain] Loaded client!");
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }

        for (Class<?> clazz : inst.getAllLoadedClasses())
        {
            LOGGER.info("[premain] Class Name {}", clazz.getName());
        }

        try
        {
            Path libraries = Path.of(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
            Path minecraftPath = libraries.getParent().resolve("minecraft");

            Path modsPath = minecraftPath.resolve("mods");
            if (!Files.isDirectory(modsPath))
                Files.createDirectory(modsPath);

            LOGGER.info("modsPath {} found/created", modsPath);
            Loader.bootstrap(modsPath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}