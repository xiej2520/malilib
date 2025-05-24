package fi.dy.masa.malilib.interfaces;

import java.util.function.Supplier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public interface IRenderer
{
    /**
     * Called after the vanilla overlays have been rendered
     */
    default void onRenderGameOverlayPost(float partialTicks) {}

    /**
     * Called after vanilla world rendering
     */
    default void onRenderWorldLast(float partialTicks, MatrixStack matrixStack) {}

    /**
     * Called after the tooltip text of an item has been rendered
     */
    default void onRenderTooltipLast(ItemStack stack, int x, int y) {}

    /**
     * Returns a supplier for the profiler section name that should be used for this renderer
     */
    default Supplier<String> getProfilerSectionSupplier()
    {
        return () -> this.getClass().getName();
    }
}
