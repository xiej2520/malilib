package fi.dy.masa.malilib.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import com.mojang.blaze3d.systems.RenderSystem;
import fi.dy.masa.malilib.util.InfoUtils;

public class GuiTextFieldGeneric extends TextFieldWidget
{
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected int zLevel;

    public GuiTextFieldGeneric(int x, int y, int width, int height, TextRenderer textRenderer)
    {
        super(textRenderer, x, y, width, height, "");

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.setMaxLength(256);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
    {
        boolean ret = super.mouseClicked(mouseX, mouseY, mouseButton);

        if (this.isMouseOver((int) mouseX, (int) mouseY))
        {
            if (mouseButton == 1)
            {
                this.setText("");
            }

            this.setFocusedWrapper(true);

            return true;
        }
        else
        {
            this.setFocusedWrapper(false);
        }

        return ret;
    }

    public int getX()
    {
        return this.x;
    }

    public int getY()
    {
        return this.y;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public void setY(int y)
    {
        this.y = y;
    }

    public boolean isMouseOver(int mouseX, int mouseY)
    {
        return mouseX >= this.x && mouseX < this.x + this.width &&
               mouseY >= this.y && mouseY < this.y + this.height;
    }

    // For Compat/Crash prevention, avoid conflict with mojmap
    public void setFocusedWrapper(boolean isFocusedIn)
    {
        boolean wasFocused = this.isFocused();
        super.setFocused(isFocusedIn);

        if (this.isFocused() != wasFocused)
        {
            MinecraftClient.getInstance().keyboard.enableRepeatEvents(this.isFocused());
        }
    }

    public GuiTextFieldGeneric setZLevel(int zLevel)
    {
        this.zLevel = zLevel;
        return this;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        if (this.zLevel != 0)
        {
            RenderSystem.pushMatrix();
            RenderSystem.translatef(0, 0, this.zLevel);

            super.render(mouseX, mouseY, partialTicks);

            RenderSystem.popMatrix();
        }
        else
        {
            super.render(mouseX, mouseY, partialTicks);
        }
    }
}
