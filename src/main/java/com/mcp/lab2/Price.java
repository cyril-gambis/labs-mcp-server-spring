package com.mcp.lab2;

import java.math.BigDecimal;

public record Price(BigDecimal amount, Currency currency) {
    public Price(double amount, Currency currency) {
        this(BigDecimal.valueOf(amount), currency);
    }

    @Override
    public final String toString() {
        return String.format("%.2f %s", amount, currency);
    }

    static public enum Currency {
        EUR, USD, GBP
    }
}

