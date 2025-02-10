package app;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;


public class PostFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return "post"; // After routing
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
        System.out.println("Post Filter: Response status " + ctx.getResponseStatusCode());
        return null;
    }
}