package com.rabbitown.yachat.chat;

/**
 * Represents a chat function's priority.
 * 
 * @author Yoooooory
 */
public enum FunctionPriority {

    /**
     * The function in the lowest priority will be run the first.<br>
     * Usually use for text-change-only functions.
     */
    LOWEST,

    /**
     * The function will be run in low priority.
     */
    LOW,

    /**
     * The function will be run in normal priority.
     */
    NORMAL,

    /**
     * The function will be run in high priority.
     */
    HIGH,

    /**
     * The function will be run in the highest priority.
     */
    HIGHEST

}