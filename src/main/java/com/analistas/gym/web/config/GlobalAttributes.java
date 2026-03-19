package com.analistas.gym.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalAttributes {

    @Value("${gym.logo}")
    private String gymLogo;

    @ModelAttribute("gymLogo")
    public String gymLogo() {
        return gymLogo;
    }
}
