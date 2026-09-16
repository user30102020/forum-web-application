package ru.kpfu.forum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.kpfu.forum.entity.Publication;

import java.util.List;
import java.util.Optional;

public interface PublicationRepository extends JpaRepository<Publication, Long> {

    @Query("select p from Publication p join fetch p.author order by p.createdAt desc")
    List<Publication> findAllWithAuthor();

    @Query("select p from Publication p join fetch p.author where p.id = :id")
    Optional<Publication> findByIdWithAuthor(Long id);
}
