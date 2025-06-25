package com.zetalasis.commonloader.inject;

import com.zetalasis.commonloader.Main;
import com.zetalasis.commonloader.inject.api.InjectPosition;
import com.zetalasis.commonloader.inject.api.Injectable;
import com.zetalasis.commonloader.inject.api.MethodInject;

@Injectable(classFQN = "net.minecraft.client.ClientBrandRetriever")
public class BrandingInject {
    @MethodInject(method = "getClientModName", position = InjectPosition.REPLACE)
    public static String getModName()
    {
        return "vanilla, commonloader";
    }
}