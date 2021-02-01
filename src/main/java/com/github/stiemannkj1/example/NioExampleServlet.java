package com.github.stiemannkj1.example;

import static javax.servlet.http.HttpServletResponse.SC_OK;
import static javax.servlet.http.HttpServletResponse.SC_PRECONDITION_FAILED;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.servlet.AsyncContext;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = "/", asyncSupported = true)
public final class NioExampleServlet extends HttpServlet {
  @Override
  protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
      throws ServletException, IOException {
    final AsyncContext asyncContext = request.startAsync();
    final ServletInputStream input = request.getInputStream();
    final ServletOutputStream output = response.getOutputStream();
    final NonBlocking nonBlocking = new NonBlocking(asyncContext, input, output, response);
    input.setReadListener(nonBlocking);
    output.setWriteListener(nonBlocking);
  }

  private static final class NonBlocking implements ReadListener, WriteListener {
    final AsyncContext asyncContext;
    final ServletInputStream input;
    final ServletOutputStream output;
    final HttpServletResponse response;
    final AtomicBoolean writing = new AtomicBoolean(false);

    private NonBlocking(
        final AsyncContext asyncContext,
        final ServletInputStream input,
        final ServletOutputStream output,
        final HttpServletResponse response) {
      this.asyncContext = Objects.requireNonNull(asyncContext);
      this.input = Objects.requireNonNull(input);
      this.output = Objects.requireNonNull(output);
      this.response = Objects.requireNonNull(response);
    }

    @Override
    public void onDataAvailable() throws IOException {
      // Wait for all data to be read.
    }

    @Override
    public void onAllDataRead() throws IOException {
      serviceIfReady();
    }

    @Override
    public void onWritePossible() throws IOException {
      serviceIfReady();
    }

    private void serviceIfReady() throws IOException {
      if (input.isFinished() && output.isReady() && !writing.getAndSet(true)) {
        String parameterName = "name";
        String name = asyncContext.getRequest().getParameter(parameterName);
        name = name != null ? name.trim() : "";
        response.setContentType("text/html");

        if (name.isEmpty()) {
          response.sendError(
              SC_PRECONDITION_FAILED,
              "Please provide a <code>" + parameterName + "</code> parameter!");
          asyncContext.complete();
          return;
        }

        response.setStatus(SC_OK);
        output.print(
            "<html><body><p>Hello "
                // Strip all non-alphabetic characters to prevent XSS and other vulns.
                + name.replaceAll("[^A-Za-z]", "")
                + "!</p></body></html>");
        asyncContext.complete();
      }
    }

    @Override
    public void onError(final Throwable t) {
      asyncContext
          .getRequest()
          .getServletContext()
          .log(
              "Failed to handle request. allDataRead = "
                  + input.isFinished()
                  + ", writePossible = "
                  + output.isReady()
                  + ", writing = "
                  + writing,
              t);
      asyncContext.complete();
    }
  }
}
