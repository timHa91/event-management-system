package de.tim.evenmanagmentsystem.security.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class LoginAttemptService {

    private final Map<String, AttemptInfo> attemptsCache = new ConcurrentHashMap<>();
    private final int MAX_ATTEMPTS = 5;
    private final int BLOCK_DURATION_MINUTES = 15;

    /**
     * Erhöht den Zähler für fehlgeschlagene Anmeldeversuche für eine E-Mail-Adresse.
     *
     * @param email Die E-Mail-Adresse
     */
    public void loginFailed(String email) {
        AttemptInfo attemptInfo = attemptsCache.getOrDefault(email, new AttemptInfo());
        attemptInfo.incrementAttempts();
        attemptInfo.setLastAttempt(LocalDateTime.now());
        attemptsCache.put(email, attemptInfo);
    }

    /**
     * Entfernt eine E-Mail-Adresse aus dem Cache (nach erfolgreicher Anmeldung).
     *
     * @param email Die E-Mail-Adresse
     */
    public void loginSucceeded(String email) {
        attemptsCache.remove(email);
    }

    /**
     * Überprüft, ob eine E-Mail-Adresse aufgrund zu vieler fehlgeschlagener Anmeldeversuche gesperrt ist.
     *
     * @param email Die E-Mail-Adresse
     * @return true, wenn die E-Mail-Adresse gesperrt ist, sonst false
     */
    public boolean isBlocked(String email) {
        AttemptInfo attemptInfo = attemptsCache.get(email);
        if (attemptInfo == null) {
            return false;
        }

        // Wenn die Sperrzeit abgelaufen ist, setze den Zähler zurück
        if (attemptInfo.getLastAttempt().plusMinutes(BLOCK_DURATION_MINUTES).isBefore(LocalDateTime.now())) {
            attemptsCache.remove(email);
            return false;
        }

        return attemptInfo.getAttempts() >= MAX_ATTEMPTS;
    }

    /**
     * Innere Klasse zur Speicherung von Informationen über Anmeldeversuche.
     */
    private static class AttemptInfo {
        private final AtomicInteger attempts = new AtomicInteger(0);
        private LocalDateTime lastAttempt = LocalDateTime.now();

        public int getAttempts() {
            return attempts.get();
        }

        public void incrementAttempts() {
            attempts.incrementAndGet();
        }

        public LocalDateTime getLastAttempt() {
            return lastAttempt;
        }

        public void setLastAttempt(LocalDateTime lastAttempt) {
            this.lastAttempt = lastAttempt;
        }
    }
}