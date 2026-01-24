package com.tristankechlo.toolleveling.config.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import java.util.function.Function;

public class CodecHelper {

    public static final Codec<Short> NON_NEGATIVE_SHORT = shortRange((short) 0, Short.MAX_VALUE);
    public static final Codec<Short> POSITIVE_SHORT = shortRange((short) 1, Short.MAX_VALUE);
    public static final Codec<Long> NON_NEGATIVE_LONG = longRange(0L, Long.MAX_VALUE);
    public static final Codec<Long> POSITIVE_LONG = longRange(1L, Long.MAX_VALUE);
    public static final Codec<Double> PERCENTAGE = Codec.doubleRange(0.0D, 100.0D);

    public static Codec<Long> longRange(final long minInclusive, final long maxInclusive) {
        final Function<Long, DataResult<Long>> checker = checkRange(minInclusive, maxInclusive);
        return Codec.LONG.flatXmap(checker, checker);
    }

    public static Codec<Short> shortRange(final short minInclusive, final short maxInclusive) {
        final Function<Short, DataResult<Short>> checker = checkRange(minInclusive, maxInclusive);
        return Codec.SHORT.flatXmap(checker, checker);
    }

    private static <N extends Number & Comparable<N>> Function<N, DataResult<N>> checkRange(final N minInclusive, final N maxInclusive) {
        return value -> {
            if (value.compareTo(minInclusive) >= 0 && value.compareTo(maxInclusive) <= 0) {
                return DataResult.success(value);
            }
            return DataResult.error("Value " + value + " outside of range [" + minInclusive + ":" + maxInclusive + "]", value);
        };
    }

}
