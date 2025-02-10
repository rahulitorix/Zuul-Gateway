package app;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import javax.servlet.http.HttpServletRequest;


public class RouteFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return "route"; // During routing
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
        System.out.println("Route Filter: Routing request...");
        return null;
    }
}
