package com.rabbitown.yachat.chat;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.google.gson.JsonArray;
import lombok.Getter;

public abstract class ChatFunction {

    @Getter
    protected String name;
    @Getter
    protected ConfigurationSection config;
    
    /**
     * 构造一个聊天函数对象。
     * 
     * @param name   聊天函数的名称
     * @param config 聊天函数的配置项
     */
    protected ChatFunction(String name, ConfigurationSection config) {
        this.name = name;
        this.config = config;
    }
    
    /**
     * 加载该聊天函数时会调用此方法。
     * 
     * @return 聊天函数是否加载成功，若此处返回为 false 则不会在 ChatFormat 中注册该函数。
     */
    public abstract boolean loadFunction();
    
    /**
     * 插件监听到聊天事件时会逐个调用已注册聊天函数的这个方法，并将玩家发送的消息设置为获取到的 JSON 文本。
     * 
     * @param message 已经其他函数处理的聊天消息
     * @param player  发送该聊天的玩家
     * @return 原始 JSON 文本对象
     */
    public abstract JsonArray parseMessage(JsonArray message, Player player);

    /**
     * 插件监听到聊天事件时会逐个调用已注册聊天函数的这个方法，用以检查聊天限制，决定这条消息能否被发送。
     * 
     * @param message 聊天消息
     * @param sender  发送该聊天的玩家
     * @return 若为 true，则消息能够发送；若为 false，该消息将被拦截并无法发送。
     */
    public boolean checkLimit(String message, Player sender) {
        return true;
    }
    
}