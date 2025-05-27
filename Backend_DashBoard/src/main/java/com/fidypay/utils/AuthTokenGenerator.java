package com.fidypay.utils;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.springframework.stereotype.Component;

@Component
public class AuthTokenGenerator {

    private SecureRandom random = new SecureRandom();


    /**
     * Generates a 130 bit random string mixing 32 digit/characters.
     * 
     * @return
     */
    public String generateToken(int length) {
        return new BigInteger(length, random).toString(32);
    }

}
