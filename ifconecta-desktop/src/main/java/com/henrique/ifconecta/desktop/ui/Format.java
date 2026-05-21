package com.henrique.ifconecta.desktop.ui;

import java.time.Duration;
import java.time.LocalDateTime;

/** Utilitários de formatação — port de timeAgo() do front web (components/ui.jsx). */
public final class Format {

    private Format() {
    }

    public static String timeAgo(LocalDateTime date) {
        if (date == null) {
            return "";
        }
        long diff = Duration.between(date, LocalDateTime.now()).getSeconds();
        if (diff < 0) {
            diff = 0;
        }
        if (diff < 60) {
            return "agora";
        }
        if (diff < 3600) {
            return "há " + (diff / 60) + "m";
        }
        if (diff < 86_400) {
            return "há " + (diff / 3600) + "h";
        }
        if (diff < 172_800) {
            return "ontem";
        }
        if (diff < 604_800) {
            return "há " + (diff / 86_400) + " dias";
        }
        if (diff < 2_592_000) {
            return "há " + (diff / 604_800) + " sem";
        }
        long months = diff / 2_592_000;
        if (months < 12) {
            return "há " + months + (months > 1 ? " meses" : " mês");
        }
        return date.toLocalDate().toString();
    }
}
