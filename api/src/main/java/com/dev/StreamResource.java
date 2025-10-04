package com.dev;

import io.reactivex.rxjava3.core.Observable;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Path("/html-chunked")
public class StreamResource {

    private static long timeout = 1000;
    static final Observable<String> model = Observable
            .fromArray("Streaming ", "my ", "footer ", "!")
            .concatMap(item -> Observable.just(item).delay(timeout, TimeUnit.MILLISECONDS));

    public static void viewTopScores(Writer writer) throws IOException {
//        writer.write("<h1>");
        model.blockingForEach(item -> {
            writer.write("<span>" + item + "</span>");
            writer.flush();
        });
//        writer.write("</h1>");
        writer.close();
    }

    @Path("/writer")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Reader getHtml() throws IOException {
        return buildReaderFromWriterBlock(writer -> {
            try {
                viewTopScores(writer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }

    @Path("/stream")
    @GET
    @Produces(MediaType.TEXT_HTML)  // Can be JSON, plain text, etc.
    public Response streamResponse() {
        StreamingOutput stream = (OutputStream output) -> {
            viewTopScores(new OutputStreamWriter(output));
        };
        return Response.ok(stream).build();
    }


    private static Reader buildReaderFromWriterBlock(Consumer<Writer> block) throws IOException {
        PipedWriter writer = new PipedWriter();
        PipedReader reader = new PipedReader(writer); // Connect the reader to the writer
        Thread writerThread = new Thread(() -> {
            block.accept(writer);
        });
        writerThread.start();
        return reader;
    }
}
