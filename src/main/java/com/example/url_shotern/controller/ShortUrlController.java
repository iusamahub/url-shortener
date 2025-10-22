package com.example.url_shotern.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.net.URI;
import org.springframework.beans.factory.annotation.Value;

import com.example.url_shotern.entity.*;
import com.example.url_shotern.service.ShortUrlService;
import com.example.url_shotern.dao.*;

@RestController
@RequestMapping("/api")
public class ShortUrlController {
	
	 @Value("${server.port}")
	 private String serverPort;

	@Autowired
	public ShortUrlService shortUrlService;
	
	record ShortenRequest(String originalUrl) {}
    record ShortenResponse(String shortUrl, String originalUrl) {}
	
//	@PostMapping("/shorten")
//    public ResponseEntity<?> shortenUrl(@RequestBody Map<String, String> body) {
//        String originalUrl = body.get("url");
//        String shortCode = shortUrlService.shortenUrl(originalUrl);
//        return ResponseEntity.ok().build();
//    }
	
	
	  @PostMapping("/shorten")
	    public ResponseEntity<ShortenResponse> shorten(@RequestBody ShortenRequest req, @RequestHeader(value = "Host", required = false) String host) {
	        String baseHost = (host == null || host.isBlank()) ? "localhost:8081" : host;
	        ShortUrl mapping = shortUrlService.shorten(req.originalUrl(), baseHost);
	        String result = baseHost + "/r/" + mapping.getShortUrl();
	        System.out.println("Handled by port: " + serverPort);

	        return ResponseEntity.ok(new ShortenResponse(result, mapping.getOriginalUrl()));
	    }

	    @GetMapping("/r/{token}")
	    public ResponseEntity<Void> redirect(@PathVariable String token) {
	        System.out.println("Handled by port: " + serverPort);

	        return shortUrlService.findByShort(token)
	                .map((ShortUrl m) -> ResponseEntity.status(302)
	                .location(URI.create(m.getOriginalUrl()))
	                .body((Void) null))
	                .orElse(ResponseEntity.notFound().build());
	    }
	
}
