package org.acme;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Entity
public class Jogo extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(readOnly = true)
    public Long id;

    @NotBlank(message = "O título do jogo é obrigatório")
    @Size(max = 100, message = "O título deve ter no máximo 100 caracteres")
    public String titulo;

    @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres")
    public String descricao;

    @Min(value = 1950, message = "O ano de lançamento deve ser válido (a partir de 1950)")
    public int anoLancamento;

    // Construtor padrão
    public Jogo() {}
}