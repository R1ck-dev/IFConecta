package com.henrique.ifconecta.desktop.ui;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/** Utilitários de formatação — port de timeAgo() do front web (components/ui.jsx). */
public final class Format {

    private static final DateTimeFormatter DATA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm");

    private Format() {
    }

    /** Formata uma data-hora ISO ("2026-05-21T14:30:00") como "21/05/2026 às 14:30". */
    public static String dataHora(String isoDateTime) {
        if (isoDateTime == null || isoDateTime.isBlank()) {
            return "";
        }
        try {
            return LocalDateTime.parse(isoDateTime).format(DATA_HORA);
        } catch (DateTimeParseException e) {
            return isoDateTime;
        }
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
