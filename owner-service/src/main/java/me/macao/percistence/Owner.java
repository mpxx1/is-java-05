package me.macao.percistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "owners")
public class Owner {

    @Id
    private Long id;

    @Column(nullable = false)
    private String email;

    private String name;

    private LocalDate birthday;
}
