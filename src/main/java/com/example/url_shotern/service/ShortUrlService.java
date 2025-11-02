package com.example.url_shotern.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.url_shotern.entity.*;
import com.example.url_shotern.dao.ShortUrlDao;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
public class ShortUrlService {

	@Autowired
	private ShortUrlDao shortUrlDao;
	
	
    // Generate a short token using SHA-256 and base62-like encoding (URL-safe base64 trimmed)
    public String generateShortUrlToken(String originalUrl) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(originalUrl.getBytes(StandardCharsets.UTF_8));
            // use first 6 bytes -> 8 chars after base64 url-safe encoding
            byte[] slice = new byte[6];
            System.arraycopy(hash, 0, slice, 0, slice.length);
            String token = Base64.getUrlEncoder().withoutPadding().encodeToString(slice);
            // remove any non-alphanumeric characters (base64 url enc uses - _)
            token = token.replace("-", "").replace("_", "");
            // ensure token length reasonable
            if (token.length() > 8) token = token.substring(0, 8);
            return token;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ShortUrl shorten(String originalUrl, String baseHost) {
        // try to generate token and ensure uniqueness
    	Optional<ShortUrl> existing = shortUrlDao.findByOriginalUrl(originalUrl);
        if (existing.isPresent()) {
            // Return existing record without regenerating
            return existing.get();
        }
        
        String token = generateShortUrlToken(originalUrl + System.currentTimeMillis());
        String shortUrl = token;
        // collision handling: if exists, append timestamp and regenerate few times
        int attempts = 0;
        while (shortUrlDao.findByShortUrl(shortUrl).isPresent() && attempts++ < 5) {
            token = generateShortUrlToken(originalUrl + System.nanoTime());
            shortUrl = token;
        }

        ShortUrl mapping = new ShortUrl();
        
        mapping.setOriginalUrl(originalUrl);
        mapping.setShortUrl(shortUrl);
        mapping.setCreatedAt(LocalDateTime.now());
        ShortUrl saved = shortUrlDao.save(mapping);
        return saved;
    }

    public Optional<ShortUrl> findByShort(String shortUrl) {
        return shortUrlDao.findByShortUrl(shortUrl);
    }
}
