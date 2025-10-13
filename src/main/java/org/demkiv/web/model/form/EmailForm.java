package org.demkiv.web.model.form;


public record EmailForm(
        String personId,
        String subject,
        String footer,
        String body) {}
