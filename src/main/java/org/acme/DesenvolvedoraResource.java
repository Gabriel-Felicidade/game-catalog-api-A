package org.acme;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import org.acme.idempotency.Idempotent; // Sua anotação customizada
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import java.net.URI;
import java.util.List;

@Path("/v1/desenvolvedoras")
@Consumes("application/json")
@Produces("application/json")
public class DesenvolvedoraResource {

    @GET
    @Operation(summary = "Retorna todas as desenvolvedoras")
    @APIResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Desenvolvedora.class, type = SchemaType.ARRAY)))
    public Response getAll() {
        return Response.ok(Desenvolvedora.listAll()).build();
    }

    @GET
    @Path("{id}")
    @Operation(summary = "Retorna uma desenvolvedora por ID")
    @APIResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Desenvolvedora.class)))
    @APIResponse(responseCode = "404", description = "Não encontrada")
    public Response getById(@PathParam("id") Long id) {
        Desenvolvedora entity = Desenvolvedora.findById(id);
        if (entity == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(entity).build();
    }

    @POST
    @Transactional
    @Idempotent // Garante a idempotência usando sua implementação existente
    @Operation(summary = "Cadastra uma desenvolvedora", description = "Requer chave de idempotência no header")
    @Parameter(name = "X-Idempotency-Key", in = ParameterIn.HEADER, required = true, description = "Chave única para garantir idempotência")
    @APIResponse(responseCode = "201", description = "Criado com sucesso", content = @Content(schema = @Schema(implementation = Desenvolvedora.class)))
    @APIResponse(responseCode = "200", description = "Replay (Idempotente)", headers = @Header(name = "X-Idempotency-Status", description = "IDEMPOTENT_REPLAY"))
    public Response insert(@Valid Desenvolvedora desenvolvedora) {
        Desenvolvedora.persist(desenvolvedora);
        URI location = UriBuilder.fromPath("/v1/desenvolvedoras/{id}").build(desenvolvedora.id);
        return Response.created(location).entity(desenvolvedora).build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    @Idempotent
    @Operation(summary = "Atualiza uma desenvolvedora")
    @Parameter(name = "X-Idempotency-Key", in = ParameterIn.HEADER, required = true)
    public Response update(@PathParam("id") Long id, @Valid Desenvolvedora novaDesenvolvedora) {
        Desenvolvedora entity = Desenvolvedora.findById(id);
        if (entity == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        entity.nome = novaDesenvolvedora.nome;
        entity.dataDeFundacao = novaDesenvolvedora.dataDeFundacao;
        entity.paisDeOrigem = novaDesenvolvedora.paisDeOrigem;

        return Response.ok(entity).build();
    }

    @DELETE
    @Path("{id}")
    @Transactional
    @Idempotent
    @Operation(summary = "Deleta uma desenvolvedora")
    @Parameter(name = "X-Idempotency-Key", in = ParameterIn.HEADER, required = true)
    public Response delete(@PathParam("id") Long id) {
        boolean deleted = Desenvolvedora.deleteById(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}