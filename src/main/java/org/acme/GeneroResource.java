package org.acme;

import io.quarkus.hibernate.orm.panache.PanacheQuery; // Adicionar import
import io.quarkus.panache.common.Sort; // Adicionar import
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
import java.util.List; // Adicionar import

@Path("/v1/generos")
@Consumes("application/json")
@Produces("application/json")
public class GeneroResource {

    @GET
    @Operation(summary = "Retorna todos os gêneros")
    @APIResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Genero.class, type = SchemaType.ARRAY)))
    public Response getAll() {
        return Response.ok(Genero.listAll()).build();
    }

    @GET
    @Path("{id}")
    @Operation(summary = "Retorna um gênero por ID")
    @APIResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Genero.class)))
    @APIResponse(responseCode = "404", description = "Não encontrado")
    public Response getById(@PathParam("id") Long id) {
        Genero entity = Genero.findById(id);
        if (entity == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(entity).build();
    }

    // --- NOVO MÉTODO DE BUSCA (SEARCH) ---
    @GET
    @Path("/search")
    @Operation(summary = "Pesquisa gêneros")
    @APIResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Genero.class, type = SchemaType.ARRAY)))
    public Response search(
            @QueryParam("q") String q,
            @QueryParam("sort") @DefaultValue("id") String sort,
            @QueryParam("direction") @DefaultValue("asc") String direction,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("4") int size
    ){
        Sort sortObj = Sort.by(sort, "desc".equalsIgnoreCase(direction) ? Sort.Direction.Descending : Sort.Direction.Ascending);
        PanacheQuery<Genero> query;
        if (q == null || q.isBlank()) {
            query = Genero.findAll(sortObj);
        } else {
            // Busca por nome ou descrição (case insensitive)
            query = Genero.find("lower(nome) like ?1 or lower(descricao) like ?1", sortObj, "%" + q.toLowerCase() + "%");
        }
        List<Genero> generos = query.page(Math.max(page, 0), size).list();

        var response = new SearchGeneroResponse();
        response.Generos = generos;
        response.TotalGeneros = query.count();
        response.TotalPages = query.pageCount();
        response.HasMore = page < query.pageCount() - 1;
        response.NextPage = response.HasMore ? UriBuilder.fromPath("/v1/generos/search").queryParam("q", q).queryParam("page", page + 1).queryParam("size", size).build().toString() : "";

        return Response.ok(response).build();
    }
    // -------------------------------------

    @POST
    @Transactional
    @Idempotent
    @Operation(summary = "Cadastra um gênero", description = "Requer chave de idempotência no header")
    @Parameter(name = "X-Idempotency-Key", in = ParameterIn.HEADER, required = true, description = "Chave única para garantir idempotência")
    @APIResponse(responseCode = "201", description = "Criado com sucesso", content = @Content(schema = @Schema(implementation = Genero.class)))
    @APIResponse(responseCode = "200", description = "Replay (Idempotente)", headers = @Header(name = "X-Idempotency-Status", description = "IDEMPOTENT_REPLAY"))
    public Response insert(@Valid Genero genero) {
        Genero.persist(genero);
        URI location = UriBuilder.fromPath("/v1/generos/{id}").build(genero.id);
        return Response.created(location).entity(genero).build();
    }

    @PUT
    @Path("{id}")
    @Transactional
    @Idempotent
    @Operation(summary = "Atualiza um gênero")
    @Parameter(name = "X-Idempotency-Key", in = ParameterIn.HEADER, required = true)
    public Response update(@PathParam("id") Long id, @Valid Genero novoGenero) {
        Genero entity = Genero.findById(id);
        if (entity == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        entity.nome = novoGenero.nome;
        entity.descricao = novoGenero.descricao;

        return Response.ok(entity).build();
    }

    @DELETE
    @Path("{id}")
    @Transactional
    @Idempotent
    @Operation(summary = "Deleta um gênero")
    @Parameter(name = "X-Idempotency-Key", in = ParameterIn.HEADER, required = true)
    public Response delete(@PathParam("id") Long id) {
        boolean deleted = Genero.deleteById(id);
        if (!deleted) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.noContent().build();
    }
}