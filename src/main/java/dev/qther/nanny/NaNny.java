package dev.qther.nanny;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EventBusSubscriber(modid = NaNny.MODID)
@Mod(NaNny.MODID)
public class NaNny {
    public static final String MODID = "nanny";
    private static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public NaNny(ModContainer container) {
        container.registerConfig(ModConfig.Type.SERVER, Config.SPEC, "nanny-server.toml");
    }

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent e) {
        LOGGER.info("Scouting a NaNny...");
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent e) {
        LOGGER.info("NaNny hired!");
    }

    @SubscribeEvent
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent e) {
        float dmg = e.getAmount();
        LivingEntity le = e.getEntity();
        if (Float.isNaN(dmg)) {
            e.setCanceled(true);
            rectify(le);
            warn("An incoming damage event tried to deal NaN damage to " + le.getName().getString() + "! Source: " + e.getSource());
        }

        if (Float.isNaN(le.getHealth())) {
            rectify(le);
            warn("A heal event set %s's health to NaN!", le.getName().getString());
        }
    }

    @SubscribeEvent
    public static void onLivingDamagePre(LivingDamageEvent.Pre e) {
        float dmg = e.getNewDamage();
        LivingEntity le = e.getEntity();
        if (Float.isNaN(dmg)) {
            e.setNewDamage(0);
            warn("A damage event tried to deal NaN damage to " + le.getName().getString() + "! Source: " + e.getSource());
        }

        if (Float.isNaN(le.getHealth())) {
            rectify(le);
            warn("A heal event set %s's health to NaN!", le.getName().getString());
        }
    }

    @SubscribeEvent
    public static void onLivingDamagePost(LivingDamageEvent.Post e) {
        float dmg = e.getInflictedDamage();
        LivingEntity le = e.getEntity();
        if (Float.isNaN(dmg)) {
            rectify(le);
            warn("A damage event tried to deal NaN damage to " + le.getName().getString() + "! Source: " + e.getSource());
        }

        if (Float.isNaN(le.getHealth())) {
            rectify(le);
            warn("A heal event set %s's health to NaN!", le.getName().getString());
        }
    }

    @SubscribeEvent
    public static void onLivingHeal(LivingHealEvent e) {
        float amount = e.getAmount();
        LivingEntity le = e.getEntity();
        if (Float.isNaN(amount)) {
            e.setCanceled(true);
            warn("A heal event tried to heal NaN health to %s!", le.getName().getString());
            return;
        }

        if (Float.isNaN(le.getHealth())) {
            e.setCanceled(true);
            rectify(le);
            warn("A heal event set %s's health to NaN!", le.getName().getString());
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent e) {
        LivingEntity le = e.getEntity();
        if (Float.isNaN(le.getHealth())) {
            e.setCanceled(true);
            rectify(le);
            warn("A death event set %s's health to NaN! Source: %s", le.getName().getString(), e.getSource());
        }
    }

    private static void warn(String format, Object... args) {
        if (Config.log.get()) {
            LOGGER.warn(format, args);
        }
        if (Config.stacktrace.get()) {
            LOGGER.warn("Stacktrace:", new Throwable("Stacktrace requested due to NaNny config"));
        }
    }

    private static void rectify(LivingEntity le) {
        if (Config.doHeal.get()) {
            le.setHealth(le.getMaxHealth() * Config.health.get().floatValue());
        }
        le.setAbsorptionAmount(0);
    }
}
