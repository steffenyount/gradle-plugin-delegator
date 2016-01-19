/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package name.yount.steffen.gradle.plugins.plugindelegator;

import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.PluginManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * The PluginDelegatorPlugin is intended to serve as a test harness allowing me
 * and others to build, debug, apply, and test their custom plugin implementations
 * within the new Gradle plugins {} DSL's early apply phase while not requiring
 * that the latest targeted plugins' implementations are first uploaded to the
 * Gradle plugin portal before each test iteration.
 *
 * It has been built and tested using Gradle 2.4.
 *
 * To setup your custom plugin as a delegate plugin just add a
 * 'META-INF/plugin-delegator.properties' file on your project's classpath and
 * include a property named 'plugins' therein to specify the pluginId of your
 * custom plugin. Multiple pluginIds may be specified in a comma separated list
 * and/or by including multiple 'META-INF/plugin-delegator.properties' files on
 * your project's classpath.
 */
public class PluginDelegatorPlugin implements Plugin<Project> {
    private static final String PROPERTIES_FILE_NAME = "META-INF/plugin-delegator.properties";
    private static final String PLUGIN_IDS_PROPERTY_NAME = "plugins";

    @Override
    public void apply(Project project) {
        final PluginManager pluginManager = project.getPluginManager();
        final List<String> delegatePluginIds = loadDelegatePluginIds();

        for (String delegatePluginId : delegatePluginIds) {
            pluginManager.apply(delegatePluginId);
        }
    }

    private List<String> loadDelegatePluginIds() {
        final List<String> result = new ArrayList<>();
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        final Enumeration<URL> en;

        try {
            en = classLoader.getResources(PROPERTIES_FILE_NAME);

        } catch (IOException ex) {
            throw new GradleException(
                    String.format("Exception while searching for %s in the classpath.", PROPERTIES_FILE_NAME), ex);
        }

        while (en.hasMoreElements()) {
            final URL propsUrl = en.nextElement();
            final Properties properties = new Properties();

            try (final InputStream propsInput = propsUrl.openStream()) {
                properties.load(propsInput);

            } catch (IOException ex) {
                throw new GradleException(
                        String.format("Exception reading %s.", propsUrl.toString()), ex);
            }

            final String delegatePluginIds = properties.getProperty(PLUGIN_IDS_PROPERTY_NAME);

            if (delegatePluginIds != null && !delegatePluginIds.trim().isEmpty()) {
                for (String delegatePluginIdSplit : delegatePluginIds.split(",")) {
                    final String delegatePluginId = delegatePluginIdSplit.trim();

                    if (!delegatePluginId.isEmpty()) {
                        result.add(delegatePluginId);
                    }
                }
            }
        }

        return result;
    }
}

