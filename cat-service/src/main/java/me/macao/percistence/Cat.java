package me.macao.percistence;

import jakarta.persistence.*;
import lombok.*;
import me.macao.data.CatColor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "cats")
public class Cat {

    @Id
    @SequenceGenerator(
            name = "cat_sequence",
            sequenceName = "cat_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "cat_sequence"
    )
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private LocalDate birthday;

    @Column(nullable = false, length = 50)
    private String breed;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CatColor color;

    @Column(nullable = false)
    private Long owner;

    @ManyToMany
    @Builder.Default
    @JoinTable(
            name = "friends", joinColumns = {
            @JoinColumn(name = "first")
    },
            inverseJoinColumns = @JoinColumn(name = "second")
    )
    private Collection<Cat> friends = new ArrayList<>();

    @ManyToMany
    @Builder.Default
    @JoinTable(
            name = "friendship_requests",
            joinColumns = {@JoinColumn(name = "src")},
            inverseJoinColumns = @JoinColumn(name = "tgt")
    )
    private Collection<Cat> reqsOut = new ArrayList<>();
}
