package app;

import com.netflix.zuul.ZuulFilter;

import com.netflix.zuul.context.RequestContext;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;


public class PreFilter extends ZuulFilter {

    @Value("${apibuilder.authz.path:null}")
    private String apiBuilderAuthPath;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();

        System.out.println("Pre Filter: " + request.getMethod() + " request to " + request.getRequestURL().toString());
        checkUserValidation();
        return null;
    }

    private void checkUserValidation(){
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest httpServletRequest = ctx.getRequest();
        String token = httpServletRequest.getHeader("Authorization");
        String path = httpServletRequest.getPathInfo();
        if (path == null) {
            path = httpServletRequest.getRequestURI();
        }
        try {
            HttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(RequestConfig.copy(RequestConfig.DEFAULT).build()).build();
            HttpPost request = new HttpPost(apiBuilderAuthPath);
            request.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
            request.setEntity(new StringEntity(
                    String.format("{\n    \"token\":\"%s\",\n    \"path\": \"%s\",\n    " +
                                    "\"method\": \"%s\"\n}"
                            , token,path
                            ,httpServletRequest.getMethod())));
            HttpResponse httpResponse = client.execute(request);
            int statusSeries = httpResponse.getStatusLine().getStatusCode()/100;
            if (statusSeries != 2) {
                Arrays.stream(httpResponse.getAllHeaders()).forEach(header -> ctx.addZuulResponseHeader(header.getName(), header.getValue()));
                ctx.setResponseStatusCode(httpResponse.getStatusLine().getStatusCode());
                ctx.setResponseBody(EntityUtils.toString(httpResponse.getEntity(), "UTF-8"));
                ctx.setSendZuulResponse(false);
            }
        } catch (IOException e) {
            ctx.setResponseStatusCode(500);
            ctx.setResponseBody(e.getMessage());
            ctx.setSendZuulResponse(false);
        }
    }
}
