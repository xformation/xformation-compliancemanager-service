/*
 * */
package com.synectiks.process.server.shared.plugins;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.synectiks.process.server.plugin.Plugin;
import com.synectiks.process.server.plugin.PluginMetaData;
import com.synectiks.process.server.plugin.PluginModule;
import com.synectiks.process.server.plugin.ServerStatus;
import com.synectiks.process.server.plugin.Version;
import com.synectiks.process.server.shared.plugins.PluginLoader;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class PluginComparatorTest {
    private PluginLoader.PluginComparator comparator = new PluginLoader.PluginComparator();

    @Parameterized.Parameters
    public static Object[][] provideData() {
        return new Object[][]{
                {new TestPlugin("u", "n", Version.from(1, 0, 0)), new TestPlugin("u", "n", Version.from(1, 0, 0)), 0},
                {new TestPlugin("u1", "n", Version.from(1, 0, 0)), new TestPlugin("u2", "n", Version.from(1, 0, 0)), -1},
                {new TestPlugin("u", "n1", Version.from(1, 0, 0)), new TestPlugin("u", "n2", Version.from(1, 0, 0)), -1},
                {new TestPlugin("u2", "n1", Version.from(1, 0, 0)), new TestPlugin("u1", "n2", Version.from(1, 0, 0)), 1},
                {new TestPlugin("u", "n", Version.from(1, 0, 0, "beta.1")), new TestPlugin("u", "n", Version.from(1, 0, 0)), -1},
                {new TestPlugin("u", "n", Version.from(1, 0, 0, "beta.1")), new TestPlugin("u", "n", Version.from(1, 0, 0, "alpha.5")), 1},
                {new TestPlugin("u", "n", Version.from(1, 0, 1)), new TestPlugin("u", "n", Version.from(1, 0, 0)), 1},
                {new TestPlugin("u", "n", Version.from(1, 0, 0)), new TestPlugin("u", "n", Version.from(1, 0, 1)), -1},
                {new TestPlugin("u", "n", Version.from(2, 0, 0)), new TestPlugin("u", "n", Version.from(1, 0, 0)), 1},
                {new TestPlugin("u", "n", Version.from(1, 1, 0)), new TestPlugin("u", "n", Version.from(1, 0, 0)), 1},
                {new TestPlugin("u", "n", Version.from(1, 0, 1)), new TestPlugin("u", "n", Version.from(1, 0, 0)), 1}
        };
    }

    private Plugin first;
    private Plugin second;
    private int comparisonResult;

    public PluginComparatorTest(Plugin first, Plugin second, int comparisonResult) {
        this.first = first;
        this.second = second;
        this.comparisonResult = comparisonResult;
    }

    @Test
    public void testCompare() throws Exception {
        assertTrue(comparator.compare(first, second) == comparisonResult);
    }

    public static class TestPlugin implements Plugin {
        private final String uniqueId;
        private final String name;
        private final Version version;

        public TestPlugin(String uniqueId, String name, Version version) {
            this.uniqueId = uniqueId;
            this.name = name;
            this.version = version;
        }

        @Override
        public PluginMetaData metadata() {
            return new PluginMetaData() {
                @Override
                public String getUniqueId() {
                    return uniqueId;
                }

                @Override
                public String getName() {
                    return name;
                }

                @Override
                public String getAuthor() {
                    return null;
                }

                @Override
                public URI getURL() {
                    return null;
                }

                @Override
                public Version getVersion() {
                    return version;
                }

                @Override
                public String getDescription() {
                    return null;
                }

                @Override
                public Version getRequiredVersion() {
                    return null;
                }

                @Override
                public Set<ServerStatus.Capability> getRequiredCapabilities() {
                    return null;
                }
            };
        }

        @Override
        public Collection<PluginModule> modules() {
            return Collections.emptySet();
        }

        @Override
        public String toString() {
            return uniqueId + " " + name + " " + version;
        }
    }
}
