package com.mrsneack.client.modules;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import com.mrsneack.client.XpushClient;

import java.awt.*;

public class XPushScreen extends Screen {

    public CheckboxWidget checkbox;

    public DelaySlider slider;

    public XPushScreen() {
        super(Text.of("XPush Menu"));
    }
    @Override
    protected void init() {
        super.init();

        int centerX = width / 2;
        int centerY = height / 2;

        this.checkbox = CheckboxWidget.builder(
                Text.translatable("checkbox.xpush.enable"),
                this.textRenderer

        ).pos(centerX - 100, centerY - 50).checked(XpushClient.toggle).callback((checkbox, checked) -> {
            XpushClient.toggle = checked;
            if (checked) {
                System.out.println("[XPush] Enabled from GUI");
            } else {
                System.out.println("[XPush] Disabled from GUI");
            }
        }).build();

        this.addDrawableChild(this.checkbox);

        this.slider = new DelaySlider(
            centerX - 100,
            centerY - 20,
            200,
            20,
            Text.literal("Delay"),
            (XpushClient.delay - 1) / 19.0,
            value -> {
                XpushClient.delay = value;
            }
        );
        this.addDrawableChild(this.slider);

    }
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {

        super.render(
                context,
                mouseX,
                mouseY,
                delta
        );

        MenuTitle(
                context,
                Text.translatable("text.xpush.menu"),
                this.width / 2,
                this.height / 2 - 90,
                0x00E5FF,
                0x9D00FF
        );

        context.drawCenteredTextWithShadow(
                this.textRenderer, Text.literal("Delay: " + XpushClient.delay), this.width / 2, this.height / 2 + 25, 0xFFFFFFFF
        );
        context.drawCenteredTextWithShadow(
                this.textRenderer, Text.literal("Enabled: " + XpushClient.toggle), this.width / 2, this.height / 2 + 40, 0xFFFFFFFF
        );
    }
    public static class DelaySlider extends SliderWidget {

        private final java.util.function.IntConsumer onChange;

        public DelaySlider(int x, int y, int width, int height, Text text, double value, java.util.function.IntConsumer onChange) {
            super(x ,y , width, height, text, value);
            this.onChange = onChange;
            updateMessage();
        }

        private int getDelay() {
            return 1 + (int) Math.round(this.value * 19.0);
        }
        @Override
        public void updateMessage() {
            this.setMessage(
                    Text.literal("Delay: " + this.getDelay())
            );
        }

        @Override
        protected void applyValue() {
            int delay =  this.getDelay();

            this.value = (delay - 1) / 19.0;

            this.onChange.accept(delay);

            updateMessage();
        }
    }

    private int interpolateColor(
            int startColor,
            int endColor,
            float progress
    ) {
        int startR = (startColor >> 16) & 0xFF;
        int startG = (startColor >> 8) & 0xFF;
        int startB = startColor & 0xFF;

        int endR = (endColor >> 16) & 0xFF;
        int endG = (endColor >> 8) & 0xFF;
        int endB = endColor & 0xFF;

        int r = (int) (startR + (endR - startR) * progress);
        int g = (int) (startG + (endG - startG) * progress);
        int b = (int) (startB + (endB - startB) * progress);

        return 0xFF000000
                | (r << 16)
                | (g << 8)
                | b;
    }

    private void MenuTitle(
            DrawContext context,
            Text text,
            int centerX,
            int y,
            int color1,
            int color2
    ) {
        String string = text.getString();

        int totalWidth = this.textRenderer.getWidth(string);
        int x = centerX - totalWidth / 2;

        float time = (System.currentTimeMillis() % 3000L) / 3000.0f;

        for (int i = 0; i < string.length(); i++) {

            String character = String.valueOf(string.charAt(i));

            float progress =
                    ((float) i / Math.max(1, string.length() - 1)
                            + time) % 1.0f;

            int color = interpolateColor(
                    color1,
                    color2,
                    progress
            );

            context.drawText(
                    this.textRenderer,
                    character,
                    x,
                    y,
                    color,
                    true
            );

            x += this.textRenderer.getWidth(character);
        }
    }
}
