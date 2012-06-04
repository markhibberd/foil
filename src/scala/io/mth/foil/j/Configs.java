package io.mth.foil.j;

import javax.servlet.Servlet;

public interface Configs {
    Config war(String context, String war);
    Config path(String context, String path);
    Config servlet(String context, String pattern, Servlet s);
    Config compound(Config... configs);
}
