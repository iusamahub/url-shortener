package com.example.url_shotern.dao;
import com.example.url_shotern.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ShortUrlDao extends JpaRepository<ShortUrl, Long> {
	
    Optional<ShortUrl> findByShortUrl(String shortUrl);
    
    Optional<ShortUrl> findByOriginalUrl(String originalUrl);

}
