package org.acme;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import java.time.LocalDate;

@Entity
public class Desenvolvedora extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(readOnly = true)
    public Long id;

    @NotBlank(message = "O nome da desenvolvedora é obrigatório")
    public String nome;

    @PastOrPresent(message = "A data de fundação não pode ser no futuro")
    public LocalDate dataDeFundacao;

    @NotBlank(message = "O país de origem é obrigatório")
    public String paisDeOrigem;

    // Construtor padrão vazio exigido pelo JPA
    public Desenvolvedora() {}
}