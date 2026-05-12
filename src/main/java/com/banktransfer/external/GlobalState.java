package com.banktransfer.external;

import java.util.HashMap;
import java.util.Map;

@DoNotModify
public class GlobalState {

    public static int transferCount = 0;
    public static boolean maintenance = false;
    public static Map<String, Integer> cache = new HashMap<>();
}