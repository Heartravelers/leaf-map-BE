package leafmap.server.domain.qna.repository;

import leafmap.server.domain.qna.entity.Inquiry;
import leafmap.server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    List<Inquiry> findByUserAndCreatedAtBetween(User user, LocalDateTime startDate, LocalDateTime endDate);
}
