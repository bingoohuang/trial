package hello;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.val;

public class RequestForwarder {
    private final HttpServletRequest req;
    private final HttpServletResponse rsp;
    private final String method;
    private final boolean hasoutbody;

    private HttpURLConnection conn;

    public RequestForwarder(HttpServletRequest req, HttpServletResponse rsp) {
        this.req = req;
        this.rsp = rsp;
        this.method = req.getMethod();
        this.hasoutbody = this.method.equals("POST");
    }

    public void forward(String forwardUrl) throws Exception {
        val url = new URL(forwardUrl // no trailing slash
                + req.getRequestURI() + (req.getQueryString() != null ? "?" + req.getQueryString() : ""));
        this.conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);

        copyRequestHeaders();

        // conn.setFollowRedirects(false); // throws AccessDenied exception
        conn.setUseCaches(false);
        conn.setDoInput(true);

        conn.setDoOutput(hasoutbody);
        conn.connect();

        val buffer = new byte[16384];
        copyReqBody(buffer);

        rsp.setStatus(conn.getResponseCode());

        copyRspHeaders();
        copyRspBody(buffer);
    }

    private void copyRspBody(byte[] buffer) throws IOException {
        while (true) {
            val read = conn.getInputStream().read(buffer);
            if (read <= 0)
                break;
            rsp.getOutputStream().write(buffer, 0, read);
        }
    }

    private void copyRspHeaders() {
        for (int i = 0;; ++i) {
            val header = conn.getHeaderFieldKey(i);
            if (header == null)
                break;
            val value = conn.getHeaderField(i);
            rsp.setHeader(header, value);
        }
    }

    private void copyReqBody(byte[] buffer) throws IOException {
        while (hasoutbody) {
            val read = req.getInputStream().read(buffer);
            if (read <= 0)
                break;
            conn.getOutputStream().write(buffer, 0, read);
        }
    }

    private void copyRequestHeaders() {
        val headers = req.getHeaderNames();
        while (headers.hasMoreElements()) {
            val header = headers.nextElement();
            val values = req.getHeaders(header);
            while (values.hasMoreElements()) {
                val value = values.nextElement();
                conn.addRequestProperty(header, value);
            }
        }
    }
}