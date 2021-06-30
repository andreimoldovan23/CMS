package raven.iss.web.controllers;

import raven.iss.data.exceptions.NotFoundException;
import raven.iss.web.security.jwt.SecurityUtils;

public abstract class UtilsHelper {
    public static String getUsername() {
        return SecurityUtils.getCurrentUsername().orElseThrow(() -> {
            throw new NotFoundException("No such user");
        });
    }
}
