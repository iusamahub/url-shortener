package com.example.url_shotern.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import java.io.ByteArrayOutputStream;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    // comment
	
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
	        String result = baseHost + "/api/r/" + mapping.getShortUrl();
	        System.out.println("Handled by port: " + serverPort);

	        return ResponseEntity.ok(new ShortenResponse(result, mapping.getOriginalUrl()));
	    }

	    @GetMapping("/r/{token}")
	    public ResponseEntity<Void> redirect(@PathVariable("token") String token) {
	        System.out.println("Handled by port: " + serverPort);

	        return shortUrlService.findByShort(token)
	                .map((ShortUrl m) -> ResponseEntity.status(302)
	                .location(URI.create(m.getOriginalUrl()))
	                .body((Void) null))
	                .orElse(ResponseEntity.notFound().build());
	    }
	    

	    @GetMapping("/qrcode/{token}")
	    public ResponseEntity<byte[]> qrcode(@PathVariable("token") String token,
	                                         @RequestHeader(value = "Host", required = false) String host,
	                                         @RequestParam(value = "size", defaultValue = "300") int size) {
	    	System.out.println("inside qrcode");
	        String baseHost = (host == null || host.isBlank()) ? "localhost:8081" : host;
	        return shortUrlService.findByShort(token)
	                .map(m -> {
	                    try {
	                        String shortLink = "http://" + baseHost + "/api/r/" + m.getShortUrl();
	                        byte[] png = generateQrPng(shortLink, size);
	                        HttpHeaders headers = new HttpHeaders();
	                        headers.set("Content-Type", "image/png");
	                        return ResponseEntity.ok().headers(headers).body(png);
	                    } catch (Exception e) {
	                        return ResponseEntity.status(500).<byte[]>build();
	                    }
	                })
	                .orElse(ResponseEntity.notFound().build());
	    }
	    
	    private byte[] generateQrPng(String text, int size) throws Exception {
	        BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, size, size);
	        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
	            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", baos);
	            return baos.toByteArray();
	        }
	    }
	
}
