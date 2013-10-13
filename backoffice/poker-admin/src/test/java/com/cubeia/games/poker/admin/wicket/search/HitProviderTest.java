package com.cubeia.games.poker.admin.wicket.search;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.cubeia.games.poker.admin.wicket.search.HitProvider.Sort;

public class HitProviderTest {

    @Test
    public void testParseAndInitSort() {
        HitProvider hp = new HitProvider(null, null, null, 0);
        
        Sort s = hp.parseSort(" _sort:timestamp ");
        assertThat(s.getField(), is("timestamp"));
        assertThat(s.isAscending(), is(true));
        
        s = hp.parseSort("_sort:timestamp");
        assertThat(s.getField(), is("timestamp"));
        assertThat(s.isAscending(), is(true));
        
        s = hp.parseSort("_sort:timestamp,asc");
        assertThat(s.getField(), is("timestamp"));
        assertThat(s.isAscending(), is(true));
        
        s = hp.parseSort("_sort:timestamp,desc");
        assertThat(s.getField(), is("timestamp"));
        assertThat(s.isAscending(), is(false));
        
        s = hp.parseSort("_type:blabl admin _sort:timestamp,desc");
        assertThat(s.getField(), is("timestamp"));
        assertThat(s.isAscending(), is(false));
        
        s = hp.parseSort("_type:blabl admin _sort:timestamp,desc more stuff AND more");
        assertThat(s.getField(), is("timestamp"));
        assertThat(s.isAscending(), is(false));
        
        s = hp.parseSort("_type:blabl admin more stuff AND more");
        assertThat(s.getField(), nullValue());
        assertThat(s.isAscending(), is(true));
    }

}
