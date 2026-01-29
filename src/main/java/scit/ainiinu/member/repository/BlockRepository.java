package scit.ainiinu.member.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import scit.ainiinu.member.entity.Block;
import scit.ainiinu.member.entity.Member;

public interface BlockRepository extends JpaRepository<Block, Long> {
    boolean existsByBlockerAndBlocked(Member blocker, Member blocked);
    List<Block> findByBlocker(Member blocker);
    Optional<Block> findByBlockerAndBlocked(Member blocker, Member blocked);
}
