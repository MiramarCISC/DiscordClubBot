package club.sdcs.discordbot.model;


import jakarta.persistence.EntityListeners;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Timestamp;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class Auditable {
    @CreatedBy
    @ManyToOne
    private User createdBy;

    @CreatedDate
    private Timestamp createdDate;

    @LastModifiedBy
    @ManyToOne
    private User lastModifiedBy;

    @LastModifiedDate
    private Timestamp lastModifiedDate;
}
