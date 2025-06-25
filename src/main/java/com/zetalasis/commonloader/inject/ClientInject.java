package com.zetalasis.commonloader.inject;

import com.zetalasis.commonloader.Loader;
import com.zetalasis.commonloader.Main;
import com.zetalasis.commonloader.inject.api.InjectPosition;
import com.zetalasis.commonloader.inject.api.Injectable;
import com.zetalasis.commonloader.inject.api.MethodInject;

@Injectable(classFQN = "enn")
public class ClientInject {
    @MethodInject(method = "<init>", position = InjectPosition.RETURN)
    public static void finishLoad()
    {
        Main.LOGGER.info("Minecraft loaded!");
        Loader.load();
    }
}