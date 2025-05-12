package com.asusoftware.AnonGram.post.config;

import com.asusoftware.AnonGram.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PostCleanupTask {

    private final PostRepository postRepository;

    @Scheduled(cron = "0 0 * * * *") // rulează la fiecare oră fixă
    public void deleteExpiredPosts() {
        int deleted = postRepository.deleteByExpiresAtBefore(LocalDateTime.now());
        if (deleted > 0) {
            System.out.println("Deleted " + deleted + " expired posts.");
        }
    }
}
