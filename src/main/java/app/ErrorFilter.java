package app;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;


public class ErrorFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return "error";
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
        System.out.println("Error Filter: Handling error...");
        return null;
    }
}