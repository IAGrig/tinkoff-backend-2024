package edu.database.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@Entity
@Table(name = "links")
public class Link {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "domain_name")
    private String domain;
    private String url;
    private OffsetDateTime registered;
    @Column(name = "last_update")
    private OffsetDateTime lastUpdate;
    @Column(name = "last_check")
    private OffsetDateTime lastCheck;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToMany(mappedBy = "links")
    private List<User> users;

    public Link(
        Long id,
        String domain,
        String url,
        OffsetDateTime registered,
        OffsetDateTime lastUpdate,
        OffsetDateTime lastCheck
    ) {
        this.id = id;
        this.domain = domain;
        this.url = url;
        this.registered = registered;
        this.lastUpdate = lastUpdate;
        this.lastCheck = lastCheck;
    }

}
