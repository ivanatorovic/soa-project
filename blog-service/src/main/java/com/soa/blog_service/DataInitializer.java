package com.soa.blog_service;

import com.soa.blog_service.model.Blog;
import com.soa.blog_service.model.BlogLike;
import com.soa.blog_service.model.Comment;
import com.soa.blog_service.repository.BlogLikeRepository;
import com.soa.blog_service.repository.BlogRepository;
import com.soa.blog_service.repository.CommentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final BlogRepository blogRepository;
    private final CommentRepository commentRepository;
    private final BlogLikeRepository blogLikeRepository;

    public DataInitializer(BlogRepository blogRepository, CommentRepository commentRepository, BlogLikeRepository blogLikeRepository) {
        this.blogRepository = blogRepository;
        this.commentRepository = commentRepository;
        this.blogLikeRepository = blogLikeRepository;
    }

    @Override
    public void run(String... args) {
        if (blogRepository.count() > 4) return;

        Blog b1 = new Blog();
        b1.setTitle("Najlepše destinacije za leto");
        b1.setDescription("Otkrijte najlepše destinacije za savršeno letovanje širom Evrope i sveta.");
        b1.setAuthorId(3L);
        b1.setAuthorUsername("ivana");
        b1.setImageUrls(List.of("zakintos.jpg"));
        b1.prePersist();
        b1 = blogRepository.save(b1);

        Blog b2 = new Blog();
        b2.setTitle("Kako se spakovati za put");
        b2.setDescription("Saveti za efikasno pakovanje i organizaciju pre putovanja bez stresa.");
        b2.setAuthorId(4L);
        b2.setAuthorUsername("tijana");
        b2.setImageUrls(List.of("pakovanje.jpg"));
        b2.prePersist();
        b2 = blogRepository.save(b2);

        Blog b3 = new Blog();
        b3.setTitle("Top 3 grada za vikend");
        b3.setDescription("Ako planirate kratak odmor, ovo su idealne evropske destinacije za vikend.");
        b3.setAuthorId(3L);
        b3.setAuthorUsername("ivana");
        b3.setImageUrls(List.of("barsa.jpg", "rim.jpg", "pariz.jpg"));
        b3.prePersist();
        b3 = blogRepository.save(b3);

        Blog b4 = new Blog();
        b4.setTitle("Putovanje sa malim budžetom");
        b4.setDescription("Kako da obiđete svet i uštedite novac uz pametno planiranje.");
        b4.setAuthorId(3L);
        b4.setAuthorUsername("ivana");
        b4.setImageUrls(List.of("jeftino.jpg"));
        b4.prePersist();
        b4 = blogRepository.save(b4);

        Comment c1 = new Comment();
        c1.setBlogId(b1.getId());
        c1.setAuthorId(3L);
        c1.setAuthorUsername("ivana");
        c1.setText("Ovo izgleda prelepo, moram posetiti!");
        c1.setCreatedAt(LocalDateTime.now().minusHours(2));
        commentRepository.save(c1);

        Comment c2 = new Comment();
        c2.setBlogId(b1.getId());
        c2.setAuthorId(4L);
        c2.setAuthorUsername("tijana");
        c2.setText("Super preporuke, hvala!");
        c2.setCreatedAt(LocalDateTime.now().minusHours(1));
        commentRepository.save(c2);

        Comment c3 = new Comment();
        c3.setBlogId(b2.getId());
        c3.setAuthorId(4L);
        c3.setAuthorUsername("tijana");
        c3.setText("Ovo mi je baš trebalo, uvek nosim previše stvari");
        c3.setCreatedAt(LocalDateTime.now().minusHours(3));
        commentRepository.save(c3);

        Comment c4 = new Comment();
        c4.setBlogId(b3.getId());
        c4.setAuthorId(3L);
        c4.setAuthorUsername("ivana");
        c4.setText("Barselona je stvarno top izbor!");
        c4.setCreatedAt(LocalDateTime.now().minusDays(1));
        commentRepository.save(c4);

        Comment c5 = new Comment();
        c5.setBlogId(b3.getId());
        c5.setAuthorId(4L);
        c5.setAuthorUsername("tijana");
        c5.setText("Dodala bih i Lisabon na ovu listu.");
        c5.setCreatedAt(LocalDateTime.now().minusHours(20));
        commentRepository.save(c5);

        Comment c6 = new Comment();
        c6.setBlogId(b4.getId());
        c6.setAuthorId(3L);
        c6.setAuthorUsername("ivana");
        c6.setText("Odlični saveti za štednju!");
        c6.setCreatedAt(LocalDateTime.now().minusMinutes(30));
        commentRepository.save(c6);

        BlogLike l1 = new BlogLike(); l1.setBlogId(b1.getId()); l1.setUserId(3L); l1.prePersist(); blogLikeRepository.save(l1);
        BlogLike l2 = new BlogLike(); l2.setBlogId(b1.getId()); l2.setUserId(4L); l2.prePersist(); blogLikeRepository.save(l2);
        BlogLike l3 = new BlogLike(); l3.setBlogId(b2.getId()); l3.setUserId(4L); l3.prePersist(); blogLikeRepository.save(l3);
        BlogLike l4 = new BlogLike(); l4.setBlogId(b3.getId()); l4.setUserId(3L); l4.prePersist(); blogLikeRepository.save(l4);
        BlogLike l5 = new BlogLike(); l5.setBlogId(b3.getId()); l5.setUserId(4L); l5.prePersist(); blogLikeRepository.save(l5);
        BlogLike l6 = new BlogLike(); l6.setBlogId(b4.getId()); l6.setUserId(3L); l6.prePersist(); blogLikeRepository.save(l6);
    }
}