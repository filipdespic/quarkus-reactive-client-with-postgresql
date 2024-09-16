package com.reactive.client;

import io.smallrye.mutiny.Multi;
import io.vertx.mutiny.pgclient.PgPool;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("movies")
public class MovieResource {

    @Inject
    PgPool client;

    @PostConstruct
    void config() {
        initdb();
    }

    @GET
    public Multi<Movie> get() {
        return Movie.findAll(client);
    }

    private void initdb() {
        client.query("DROP TABLE IF EXISTS movies").execute()
                .flatMap(m -> client.query("CREATE TABLE movies (id SERIAL PRIMARY KEY, " +
                        "title TEXT NOT NULL)").execute())
                .flatMap(m -> client.query("INSERT INTO movies (movie) VALUES ('The Lord of the Rings')").execute())
                .flatMap(m -> client.query("INSERT INTO movies (movie) VALUES ('Harry Potter')").execute())
                .await()
                .indefinitely();
    }

}
