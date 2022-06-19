package dev.qther.nanny;

import net.minecraft.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("nanny")
public class NaNny {
    private static final Logger LOGGER = LogManager.getLogger();

    public NaNny() {
        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SPEC, "nanny-server.toml");
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent e) {
        LOGGER.info("Scouting a NaNny...");
    }

    @SubscribeEvent
    public void onServerStarted(FMLServerStartedEvent e) {
        LOGGER.info("NaNny hired!");
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent e) {
        float dmg = e.getAmount();
        LivingEntity le = e.getEntityLiving();
        if (Float.isNaN(dmg)) {
            e.setCanceled(true);
            rectify(le);
            warn("A hurt event tried to deal NaN damage to " + getName(le) + "! Source: " + e.getSource());
            return;
        }
    }

    @SubscribeEvent
    public void onLivingDamage(LivingDamageEvent e) {
        LivingEntity le = e.getEntityLiving();
        float dmg = e.getAmount();
        if (Float.isNaN(dmg)) {
            e.setCanceled(true);
            rectify(le);
            warn("A damage event tried to deal NaN damage to " + getName(le) + "! Source: " + e.getSource());
            return;
        }
    }

    @SubscribeEvent
    public void onAttackEntity(LivingAttackEvent e) {
        LivingEntity le = e.getEntityLiving();
        float dmg = e.getAmount();
        if (Float.isNaN(dmg)) {
            e.setCanceled(true);
            rectify(le);
            warn("A attack event tried to deal NaN damage to " + getName(le) + "! Source: " + e.getSource());
            return;
        }
    }

    @SubscribeEvent
    public void onLivingHeal(LivingHealEvent e) {
        float amount = e.getAmount();
        LivingEntity le = e.getEntityLiving();
        if (Float.isNaN(amount)) {
            e.setCanceled(true);
            warn("A heal event tried to heal NaN health to " + getName(le) + "!");
            return;
        }

        if (Float.isNaN(le.getHealth())) {
            e.setCanceled(true);
            rectify(le);
            warn("A heal event set " + le.getName().getString() + "'s health to NaN!");
            return;
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent e) {
        LivingEntity le = e.getEntityLiving();
        float hp = le.getHealth();
        if (Float.isNaN(hp)) {
            e.setCanceled(true);
            rectify(le);
            warn("A death event set " + getName(le) + "'s health to NaN! Source: " + e.getSource());
            return;
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
