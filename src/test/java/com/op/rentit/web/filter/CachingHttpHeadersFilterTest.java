package com.op.rentit.web.filter;

import com.op.rentit.config.JHipsterProperties;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
@RunWith(MockitoJUnitRunner.class)
public class CachingHttpHeadersFilterTest {

    @InjectMocks
    CachingHttpHeadersFilter cachingHttpHeadersFilter;

    @Mock
    HttpServletRequest servletRequest;

    @Mock
    HttpServletResponse servletResponse;

    @Mock
    FilterChain filterChain;

    @Test
    public void testThatCachingFilterIsChangingCacheControlHeader() throws Exception {
        cachingHttpHeadersFilter.doFilter(servletRequest, servletResponse, filterChain);

        verify(filterChain, times(1)).doFilter(servletRequest, servletResponse);
        verify(servletResponse, times(1)).setHeader(eq("Cache-Control"), anyString());
    }

}
