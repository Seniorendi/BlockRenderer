package com.unascribed.blockrenderer.varia.logging;

import com.unascribed.blockrenderer.Reference;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class Markers {

    public static final Marker ROOT = marker();

    public static final Marker OPEN_GL_DEBUG = marker("OpenGLDebug");
    public static final Marker FILE = marker("FILE");
    public static final Marker MANAGER = marker("MANAGER");
    public static final Marker PROGRESS = marker("PROGRESS");
    public static final Marker SEARCH = marker("SEARCH");
    public static final Marker STB = marker("STB");

    private static Marker marker() {
        return MarkerManager.getMarker(Reference.NAME);
    }

    public static Marker marker(String name) {
        return MarkerManager.getMarker(Reference.NAME + "-" + name).addParents(ROOT);
    }

    public static Marker marker(String name, Marker parent) {
        return MarkerManager.getMarker(Reference.NAME + "-" + name).addParents(parent);
    }

}