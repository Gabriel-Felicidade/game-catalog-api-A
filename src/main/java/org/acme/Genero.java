package org.acme;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Entity
public class Genero extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(readOnly = true)
    public Long id;

    @NotBlank(message = "O nome do gênero é obrigatório")
    @Size(max = 50, message = "O nome deve ter no máximo 50 caracteres")
    public String nome;

    @Size(max = 200, message = "A descrição deve ter no máximo 200 caracteres")
    public String descricao;

    // Construtor padrão
    public Genero() {}
}