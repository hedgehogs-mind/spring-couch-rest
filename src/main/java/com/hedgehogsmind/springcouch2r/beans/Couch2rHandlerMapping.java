package com.hedgehogsmind.springcouch2r.beans;

import com.hedgehogsmind.springcouch2r.data.Couch2rMapping;
import com.hedgehogsmind.springcouch2r.util.Couch2rPathUtil;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Component
public class Couch2rHandlerMapping implements HandlerMapping, Ordered {

    private Set<Couch2rMapping> mappings = new HashSet<>();

    private Map<String, Couch2rMapping> mappingCache = new HashMap<>();

    @Override
    public int getOrder() {
        return -100;
    }

    @Override
    public HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
        final String path = Couch2rPathUtil.normalizeWithTrailingSlash(
                UrlPathHelper.defaultInstance.getPathWithinApplication(request)
        );

        // TODO @peter insert default interceptors
        final Couch2rMapping mapping = getAndCache(path);

        if ( mapping == null ) return null; // next HandlerMapping

        return new HandlerExecutionChain(mapping);
    }

    /**
     * <p>
     *     First tries to find mapping in cache map. If present it is returned.
     * </p>
     *
     * <p>
     *     Otherwise we search a mapping that fits. If one has been found, we cache it
     *     and return it. If no mapping has been found, we return null.
     * </p>
     *
     * @param path Path to get mapping for.
     * @return Mapping or null if no mapping fits.
     */
    protected Couch2rMapping getAndCache(final String path) {
        final Couch2rMapping cachedMapping = mappingCache.get(path);

        if ( cachedMapping != null ) return cachedMapping;

        final Optional<Couch2rMapping> matchingMapping = mappings.stream()
                .filter(mapping -> path.startsWith(mapping.getPathWithTrailingSlash()))
                .findAny();

        if ( matchingMapping.isEmpty() ) return null;

        mappingCache.put(path, matchingMapping.get());
        return matchingMapping.get();
    }

    /**
     * Adds mapping. Clears cache.
     *
     * @param couch2rMapping Mapping.
     */
    public void registerMapping(final Couch2rMapping couch2rMapping) {
        this.mappings.add(couch2rMapping);

        if ( !this.mappingCache.isEmpty() ) this.mappingCache.clear();
    }
}
