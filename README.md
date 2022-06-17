# NaNny

## Because some mods can't keep their floats in control.

NaNny is a Minecraft 1.16.5 Forge mod that cleans up after mods that set entitys' health and/or absorption to NaN.

This is accomplished by hooking into the following events and cancelling and optionally healing the affected entities:

- LivingAttackEvent
- LivingDamageEvent
- LivingHealEvent
- LivingHurtEvent

NaNny creates a TOML configuration file in `world/serverconfig/nanny-server.toml`

The configuration file has the following options at present:

- doHeal - Enable setting the health of an entity when NaN is dealt or healed to them (Default true)
- health - Percentage of max health to set the entity to when NaN dealt or healed to them (Default 1.0)
- log - Whether or not to log events that set entitys' health to NaN (Default true)
