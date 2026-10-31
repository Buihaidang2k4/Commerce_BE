package com.example.MyShop_API.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AddressTypeConstants {
    public static final String HOME = "HOME";
    public static final String WORK = "WORK";
    public static final String SCHOOL = "SCHOOL";
    public static final String OTHER = "OTHER";
    public static final Set<String> VALUES = Set.of(HOME, WORK, SCHOOL, OTHER);

    public static String valueOf(String value) {
        if (!VALUES.contains(value)) throw new IllegalArgumentException("Invalid AddressTypeConstants: " + value);
        return value;
    }
}
