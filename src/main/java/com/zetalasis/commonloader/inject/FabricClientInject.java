package com.zetalasis.commonloader.inject;

import com.zetalasis.commonloader.Loader;
import com.zetalasis.commonloader.Main;
import com.zetalasis.commonloader.inject.api.InjectPosition;
import com.zetalasis.commonloader.inject.api.Injectable;
import com.zetalasis.commonloader.inject.api.MethodInject;

/** Compatibility with Fabric's KnotClient */
@Injectable(classFQN = "net.fabricmc.loader.impl.launch.knot.KnotClient")
public class FabricClientInject {
    @MethodInject(method = "main", position = InjectPosition.TAIL)
    public static void fabricLoaded()
    {
        Main.LOGGER.info("Fabric loaded.. loading mods");
        Loader.load();
    }
}