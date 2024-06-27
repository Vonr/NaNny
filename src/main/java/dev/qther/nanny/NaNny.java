package dev.qther.nanny;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(NaNny.MODID)
public class NaNny {
    public static final String MODID = "nanny";
    private static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public NaNny(ModContainer container) {
        NeoForge.EVENT_BUS.register(this);
        container.registerConfig(ModConfig.Type.SERVER, Config.SPEC, "nanny-server.toml");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent e) {
        LOGGER.info("Scouting a NaNny...");
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent e) {
        LOGGER.info("NaNny hired!");
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent e) {
        float dmg = e.getAmount();
        LivingEntity le = e.getEntity();
        if (Float.isNaN(dmg)) {
            e.setCanceled(true);
            rectify(le);
            warn("A hurt event tried to deal NaN damage to " + getName(le) + "! Source: " + e.getSource());
        }
    }

    @SubscribeEvent
    public void onLivingDamage(LivingDamageEvent e) {
        LivingEntity le = e.getEntity();
        float dmg = e.getAmount();
        if (Float.isNaN(dmg)) {
            e.setCanceled(true);
            rectify(le);
            warn("A damage event tried to deal NaN damage to " + getName(le) + "! Source: " + e.getSource());
        }
    }

    @SubscribeEvent
    public void onAttackEntity(LivingAttackEvent e) {
        LivingEntity le = e.getEntity();
        float dmg = e.getAmount();
        if (Float.isNaN(dmg)) {
            e.setCanceled(true);
            rectify(le);
            warn("A attack event tried to deal NaN damage to " + getName(le) + "! Source: " + e.getSource());
        }
    }

    @SubscribeEvent
    public void onLivingHeal(LivingHealEvent e) {
        float amount = e.getAmount();
        LivingEntity le = e.getEntity();
        if (Float.isNaN(amount)) {
            e.setCanceled(true);
            warn("A heal event tried to heal NaN health to " + getName(le) + "!");
            return;
        }

        if (Float.isNaN(le.getHealth())) {
            e.setCanceled(true);
            rectify(le);
            warn("A heal event set " + le.getName().getString() + "'s health to NaN!");
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent e) {
        LivingEntity le = e.getEntity();
        float hp = le.getHealth();
        if (Float.isNaN(hp)) {
            e.setCanceled(true);
            rectify(le);
            warn("A death event set " + getName(le) + "'s health to NaN! Source: " + e.getSource());
        }
    }

    private void warn(String msg) {
        if (Config.log.get()) {
            LOGGER.warn(msg);
        }
    }

    private void rectify(LivingEntity le) {
        if (Config.doHeal.get()) {
            le.setHealth(le.getMaxHealth() * Config.health.get().floatValue());
        }
        le.setAbsorptionAmount(0);
    }

    private String getName(LivingEntity e) {
        return e.getName().getString();
    }
}
