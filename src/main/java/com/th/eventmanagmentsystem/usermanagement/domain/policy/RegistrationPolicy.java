package com.th.eventmanagmentsystem.usermanagement.domain.policy;

public interface RegistrationPolicy<T> {
    /**
     * Prüft, ob die Anfrage gemäß der Regel gültig ist.
     * Wirft eine spezifische Exception, wenn die Regel verletzt wird.
     * @param request Das Anfrageobjekt, das validiert werden soll.
     */
    void check(T request);
}