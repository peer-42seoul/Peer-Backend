package peer.backend.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@Entity
@EnableJpaAuditing
@EntityListeners(AuditingEntityListener.class)
@Table(name = "Contact_Us")
public class ContactUs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 90)
    private String firstName;

    @Column(length = 90)
    private String lastName;

    @Column(length = 40)
    private String email;

    @Column(length = 200)
    private String companyAndSite;

    @CreatedDate
    private LocalDateTime createdAt;

    @Column(length = 1500)
    private String text;

    @Column
    private boolean emailClientSent = false;

    @Column
    private boolean emailManagementSent = false;
}
