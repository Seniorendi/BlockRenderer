package com.unascribed.blockrenderer.client.render.request;

import com.unascribed.blockrenderer.client.render.IAnimatedRenderer;
import com.unascribed.blockrenderer.client.render.IRequest;
import com.unascribed.blockrenderer.client.render.RenderManager;

import javax.imageio.stream.ImageOutputStream;
import java.util.function.Consumer;
import java.util.function.Function;

public class AnimatedRenderingRequest<S, T> implements IRequest {

    private final IAnimatedRenderer<S, T> renderer;
    private final S parameters;
    private final T value;
    private final Function<T, ImageOutputStream> provider;
    private final Consumer<T> callback;
    private final int length;
    private final boolean loop;

    public AnimatedRenderingRequest(IAnimatedRenderer<S, T> renderer,
                                    S parameters,
                                    T value,
                                    int length,
                                    boolean loop,
                                    Function<T, ImageOutputStream> provider,
                                    Consumer<T> callback) {
        this.renderer = renderer;
        this.parameters = parameters;
        this.value = value;
        this.length = length;
        this.loop = loop;
        this.provider = provider;
        this.callback = callback;
    }

    /**
     * @return true when rendering has been completed
     */
    @Override
    public boolean render() {
        RenderManager.animated(renderer, provider, callback, parameters, length, loop, value);
        return true;
    }

}