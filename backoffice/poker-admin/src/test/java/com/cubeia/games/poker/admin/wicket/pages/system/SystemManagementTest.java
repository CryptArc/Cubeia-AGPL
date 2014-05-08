/**
 * Copyright (C) 2012 Cubeia Ltd <info@cubeia.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cubeia.games.poker.admin.wicket.pages.system;

import static org.mockito.MockitoAnnotations.initMocks;

import com.cubeia.network.shared.web.wicket.AdminConfig;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.spring.test.ApplicationContextMock;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.cubeia.games.poker.admin.db.AdminDAO;
import com.cubeia.games.poker.admin.jmx.FirebaseJMXFactory;
import com.cubeia.games.poker.admin.network.NetworkClient;

public class SystemManagementTest {

    @Mock
    private AdminDAO adminDao;

    @Mock
    private NetworkClient networkClient;

    @Mock
    private FirebaseJMXFactory jmxFactory;

    @Mock
    AdminConfig config;

    private WicketTester tester;

    @Before
    public void setup() {
        initMocks(this);
        ApplicationContextMock context = new ApplicationContextMock();
        context.putBean("networkClient", networkClient);
        context.putBean("jmxFactory", jmxFactory);
        context.putBean("adminConfig",config);
        tester = new WicketTester();
        tester.getApplication().getComponentInstantiationListeners().add(new SpringComponentInjector(tester.getApplication(), context));
    }

    @Test
    public void testPageLoad() {
        PageParameters pageParameters = new PageParameters();
        tester.startPage(new SystemManagement(pageParameters));
        tester.assertRenderedPage(SystemManagement.class);
    }
}
