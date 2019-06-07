package org.fogbowcloud.arrebol.execution.docker.request;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

public class HttpWrapper {

    private static final int SERVER_SIDE_ERRO_MAX = 505;
    private static final int CLIENT_SIDE_CODE_ERRO_INIT = 400;

    public static final String HTTP_CONTENT_JSON = "application/json";

    public static final String HTTP_METHOD_POST = HttpPost.METHOD_NAME;
    public static final String HTTP_METHOD_GET = HttpGet.METHOD_NAME;
    public static final String HTTP_METHOD_DELETE = HttpDelete.METHOD_NAME;

    private static HttpClient createHttpClient() {
        return HttpClients.createMinimal();
    }

    public String doRequest(String method, String endpoint) throws Exception {
        return doRequest(method, endpoint, new ArrayList<>());
    }

    public String doRequest(String method, String endpoint, List<Header> additionalHeaders) throws Exception {
        return doRequest(method, endpoint, additionalHeaders, null);
    }

    public String doRequest(String method, String endpoint, StringEntity body) throws Exception {
        return doRequest(method, endpoint, new ArrayList<>(), body);
    }

    public String doRequest(String method, String endpoint, List<Header> additionalHeaders, StringEntity body) throws Exception {

        HttpUriRequest request = null;

        if (method.equalsIgnoreCase(HTTP_METHOD_GET)) {
            request = new HttpGet(endpoint);
        } else if (method.equalsIgnoreCase(HTTP_METHOD_DELETE)) {
            request = new HttpDelete(endpoint);
        } else if (method.equalsIgnoreCase(HTTP_METHOD_POST)) {
            request = new HttpPost(endpoint);
            ((HttpPost) request).setEntity(body);
        }

        Header h = new BasicHeader("Content-Type", HTTP_CONTENT_JSON);
        request.addHeader(h);
        for (Header header : additionalHeaders) {
            request.addHeader(header);
        }

        HttpResponse response = createHttpClient().execute(request);
        HttpEntity entity = null;

        try {

            entity = response.getEntity();

            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED) {
                return EntityUtils.toString(response.getEntity());

            } else if(statusCode >= CLIENT_SIDE_CODE_ERRO_INIT && statusCode <= SERVER_SIDE_ERRO_MAX) {
                throw new Exception("Erro on request - Method ["+method+"] " +
                        "Endpoint: ["+endpoint+"] - Status: "+statusCode+" -  " +
                        "Msg: "+response.getStatusLine().toString());
            } else {
                return response.getStatusLine().toString();
            }

        } finally {
            try {
                if (entity != null) {
                    EntityUtils.toString(entity);
                }
            } catch (Exception e) {}
        }
    }
}