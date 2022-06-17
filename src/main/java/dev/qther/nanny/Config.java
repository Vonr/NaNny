package dev.qther.nanny;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue doHeal;
    public static final ForgeConfigSpec.DoubleValue health;
    public static final ForgeConfigSpec.BooleanValue log;

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
