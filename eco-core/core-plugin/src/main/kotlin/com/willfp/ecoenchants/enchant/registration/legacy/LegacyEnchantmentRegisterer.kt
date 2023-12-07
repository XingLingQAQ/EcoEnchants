package com.willfp.ecoenchants.enchant.registration.legacy

import com.willfp.ecoenchants.enchant.EcoEnchant
import com.willfp.ecoenchants.enchant.impl.EcoEnchantBase
import com.willfp.ecoenchants.enchant.registration.EnchantmentRegisterer
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment

@Suppress("UNCHECKED_CAST")
object LegacyEnchantmentRegisterer : EnchantmentRegisterer {
    override fun register(enchant: EcoEnchant): Enchantment {
        Enchantment::class.java.getDeclaredField("acceptingNew")
            .apply {
                isAccessible = true
                set(null, true)
            }

        val enchantment = LegacyDelegatedEnchantment(enchant)

        Enchantment.registerEnchantment(enchantment)

        return enchantment
    }

    override fun unregister(enchant: EcoEnchant) {
        Enchantment::class.java.getDeclaredField("byKey")
            .apply {
                isAccessible = true
                (get(null) as MutableMap<NamespacedKey, Enchantment>).apply { remove(enchant.enchantmentKey) }
            }

        Enchantment::class.java.getDeclaredField("byName")
            .apply {
                isAccessible = true
                (get(null) as MutableMap<String, Enchantment>).apply { remove(enchant.id.uppercase()) }
            }
    }
}