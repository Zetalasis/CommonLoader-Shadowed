package com.zetalasis.commonloader.inject;

import com.zetalasis.commonloader.inject.api.InjectPosition;
import com.zetalasis.commonloader.inject.api.Injectable;
import com.zetalasis.commonloader.inject.api.MethodInject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

@Injectable(classFQN = "euw")
public class TitleScreenInject {
    @MethodInject(method = "render", position = InjectPosition.RETURN)
    public static void finishRender(DrawContext context)
    {
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;

        String customText = "CommonLoader (1.0.0)";
        int color = 0xFFFFFF;

        int x = 2;
        int y = client.getWindow().getScaledHeight() - 20;

        context.drawTextWithShadow(textRenderer, customText, x, y, color);
    }
}