package cpw.mods.fml.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declare a variable to be populated by a Bukkit Plugin proxy instance if the bukkit coremod
 * is available. It can only be applied to field typed as {@link BukkitProxy}
 * Generally it should be used in conjunction with {@link Mod#bukkitPlugin()} specifying the
 * plugin to load.
 *
 * @author cpw
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface BukkitPluginRef
{
    /**
     * A reference (possibly version specific) to a Bukkit Plugin by name, using the name@versionbound
     * specification. If this is a bukkit enabled environment the field annotated by this
     * will be populated with a {@link BukkitProxy} instance if possible. This proxy will be gotten by
     * reflectively calling the "getModProxy" method on the bukkit plugin instance.
     * @return
     */
    String value();
}
