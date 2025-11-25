package org.acme;

import io.quarkus.hibernate.orm.panache.PanacheQuery; // Import necessário
import io.quarkus.panache.common.Sort; // Import necessário
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import org.acme.idempotency.Idempotent;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import java.net.URI;
import java.util.List; // Import necessário

@Path("/v1/jogos")
@Consumes("application/json")
@Produces("application/json")
public class JogoResource {

    @GET
    @Operation(summary = "Retorna todos os jogos")
    @APIResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Jogo.class, type = SchemaType.ARRAY)))
    public Response getAll() {
        return Response.ok(Jogo.listAll()).build();
    }

    @GET
    @Path("{id}")
    @Operation(summary = "Retorna um jogo por ID")
    @APIResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Jogo.class)))
    @APIResponse(responseCode = "404", description = "Não encontrado")
    public Response getById(@PathParam("id") Long id) {
        Jogo entity = Jogo.findById(id);
        if (entity == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(entity).build();
    }

    // --- NOVO MÉTODO DE BUSCA (SEARCH) ---
    @GET
    @Path("/search")
    @Operation(summary = "Pesquisa jogos")
    @APIResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Jogo.class, type = SchemaType.ARRAY)))
    public Response search(
            @QueryParam("q") String q,
            @QueryParam("sort") @DefaultValue("id") String sort,
            @QueryParam("direction") @DefaultValue("asc") String direction,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("4") int size
    ){
        Sort sortObj = Sort.by(sort, "desc".equalsIgnoreCase(direction) ? Sort.Direction.Descending : Sort.Direction.Ascending);
        PanacheQuery<Jogo> query;
        if (q == null || q.isBlank()) {
            query = Jogo.findAll(sortObj);
        } else {
            // Busca por título ou descrição (case insensitive)
            query = Jogo.find("lower(titulo) like ?1 or lower(descricao) like ?1", sortObj, "%" + q.toLowerCase() + "%");
        }
        List<Jogo> jogos = query.page(Math.max(page, 0), size).list();

        var response = new SearchJogoResponse();
        response.Jogos = jogos;
        response.TotalJogos = query.count();
        response.TotalPages = query.pageCount();
        response.HasMore = page < query.pageCount() - 1;
        response.NextPage = response.HasMore ? UriBuilder.fromPath("/v1/jogos/search").queryParam("q", q).queryParam("page", page + 1).queryParam("size", size).build().toString() : "";

        return Response.ok(response).build();
    }
    // -------------------------------------

    @POST
    @Transactional
    @Idempotent
    @Operation(summary = "Cadastra um jogo", description = "Requer chave de idempotência no header")
    @Parameter(name = "X-Idempotency-Key", in = ParameterIn.HEADER, required = true, description = "Chave única para garantir idempotência")
    @APIResponse(responseCode = "201", description = "Criado com sucesso", content = @Content(schema = @Schema(implementation = Jogo.class)))
    @APIResponse(responseCode = "200", description = "Replay (Idempotente)", headers = @Header(name = "X-Idempotency-Status", description = "IDEMPOTENT_REPLAY"))
    public Response insert(@Valid Jogo jogo) {
        Jogo.persist(jogo);
        URI location = UriBuilder.fromPath("/v1/jogos/{id}").build(jogo.id);
        return Response.created(location).entity(jogo).build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    @Idempotent
    @Operation(summary = "Atualiza um jogo")
    @Parameter(name = "X-Idempotency-Key", in = ParameterIn.HEADER, required = true)
    public Response update(@PathParam("id") Long id, @Valid Jogo novoJogo) {
        Jogo entity = Jogo.findById(id);
        if (entity == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        entity.titulo = novoJogo.titulo;
        entity.descricao = novoJogo.descricao;
        entity.anoLancamento = novoJogo.anoLancamento;

        return Response.ok(entity).build();
    }

    @DELETE
    @Path("{id}")
    @Transactional
    @Idempotent
    @Operation(summary = "Deleta um jogo")
    @Parameter(name = "X-Idempotency-Key", in = ParameterIn.HEADER, required = true)
    public Response delete(@PathParam("id") Long id) {
        boolean deleted = Jogo.deleteById(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}