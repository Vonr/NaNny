package dev.qther.nanny;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.BooleanValue doHeal;
    public static final ModConfigSpec.DoubleValue health;
    public static final ModConfigSpec.BooleanValue log;

    static {
        BUILDER.push("Server Config for NaNny");

        doHeal = BUILDER.comment("Enable setting the health of an entity when NaN is dealt or healed to them (Default true)")
            .define("doHeal", true);

        health = BUILDER
            .comment("Percentage of max health to set the entity to when NaN dealt or healed to them (Default 1.0)")
            .defineInRange("health", 1.0, 0.0, 1.0);

        log = BUILDER
            .comment("Whether or not to log events that set entitys' health to NaN (Default true)")
            .define("log", true);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
