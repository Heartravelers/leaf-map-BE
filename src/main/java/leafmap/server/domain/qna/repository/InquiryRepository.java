package leafmap.server.domain.qna.repository;

import leafmap.server.domain.qna.entity.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
}
