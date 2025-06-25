package com.zetalasis.commonloader;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public class Loader {
    private static HashSet<ICommonMod> MODLIST = new HashSet<>();
    private static final Gson GSON = new Gson();

    public static void bootstrap(Path modsPath)
    {
        Main.LOGGER.info("Bootstrapping mods...");

        try (Stream<Path> mods = Files.list(modsPath))
        {
            mods.filter(file -> file.toString().endsWith(".jar")).forEach(filePath -> {
                File file = filePath.toFile();

                try (JarFile jar = new JarFile(file)) {
                    JarEntry modJsonEntry = jar.getJarEntry("common.mod.json");

                    if (modJsonEntry == null) {
                        Main.LOGGER.info("Skipping {} (no common.mod.json)", file.getName());
                        return;
                    }

                    Main.LOGGER.info("Loading file \"{}\"...", file.getName());

                    try (InputStream jsonStream = jar.getInputStream(modJsonEntry);
                         InputStreamReader jsonStreamReader = new InputStreamReader(jsonStream)) {
                        JsonObject root = JsonParser.parseReader(jsonStreamReader).getAsJsonObject();

                        String entryPoint = root.get("entrypoint").getAsString();

                        URL jarUrl = filePath.toUri().toURL();
                        URLClassLoader loader = new URLClassLoader(new URL[]{jarUrl}, Main.class.getClassLoader());

                        Class<?> modClass = Class.forName(entryPoint, true, loader);
                        Object instance = modClass.getDeclaredConstructor().newInstance();

                        if (!(instance instanceof ICommonMod)) {
                            Main.LOGGER.error("Entry point {} does not implement ICommonMod", entryPoint);
                            return;
                        }

                        ICommonMod mod = (ICommonMod) instance;
                        MODLIST.add(mod);

                        Main.LOGGER.info("Loaded mod {} successfully", mod.getModId());

                    } catch (Exception e) {
                        Main.LOGGER.error("Failed to load mod from {}", file.getName());
                        e.printStackTrace();
                    }

                    Main.LOGGER.info("Loaded mod \"{}\" succssfully!", file.getName());
                }
                catch (IOException ioException)
                {
                    Main.LOGGER.info("File {} not a jar file! Skipping...", file.getName());
                }
            });

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Main.LOGGER.info("Bootstrap complete! {} mods loaded.", MODLIST.size());
    }

    public static void load()
    {
        for (ICommonMod mod : MODLIST)
        {
            Main.LOGGER.info("Executing mod {}:{}...", mod.getModId(), mod.getDisplayName());
            mod.init();
        }
    }
}
