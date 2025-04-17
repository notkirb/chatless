package com.notkirb.chatless

import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.GameRule

class Chatless : JavaPlugin(), Listener {
    lateinit var originalGamerule_ANNOUNCE_ADVANCEMENTS: Any
    lateinit var originalGamerule_SHOW_DEATH_MESSAGES: Any

    override fun onEnable() {
        server.pluginManager.registerEvents(this, this)
        originalGamerule_ANNOUNCE_ADVANCEMENTS = server.worlds[0].getGameRuleValue(GameRule.ANNOUNCE_ADVANCEMENTS)!!
        originalGamerule_SHOW_DEATH_MESSAGES = server.worlds[0].getGameRuleValue(GameRule.SHOW_DEATH_MESSAGES)!!

        setGlobalGamerule(GameRule.ANNOUNCE_ADVANCEMENTS, false)
        setGlobalGamerule(GameRule.SHOW_DEATH_MESSAGES, false)

        logger.info("Chatless started!")
    }

    override fun onDisable() {
        setGlobalGamerule(GameRule.ANNOUNCE_ADVANCEMENTS, originalGamerule_ANNOUNCE_ADVANCEMENTS)
        setGlobalGamerule(GameRule.SHOW_DEATH_MESSAGES, originalGamerule_SHOW_DEATH_MESSAGES)
        logger.info("Chatless shutting down. Goodbye!")
    }

    @EventHandler
    fun onChatMessage (event: AsyncChatEvent) {
        logger.info("A chat message was canceled. Original content: <" + event.player.displayName + "> " + PlainTextComponentSerializer.plainText().serialize(event.message()))
        event.isCancelled = true
        event.player.sendMessage("Don't send chat messages! Your message has been canceled and reported to server console.")
    }

    fun setGlobalGamerule(rule: GameRule<*>, value: Any) {
        for (world in server.worlds) {
            when (value) {
                is Boolean -> world.setGameRule(rule as GameRule<Boolean>, value)
                is Int -> world.setGameRule(rule as GameRule<Int>, value)
                else -> throw IllegalArgumentException("Unsupported value type for gamerule")
            }
        }
    }
}
