import io.micronaut.inject.visitor.TypeElementVisitor;

module io.github.madethoughts.mayflower {
    requires org.bukkit;

    requires io.micronaut.inject;
    requires io.micronaut.aop;
    requires io.micronaut.context;
    requires io.micronaut.core;

    requires jakarta.inject;
    requires org.jetbrains.annotations;
    requires org.slf4j;
    // should be ok, since it has an automatic-module-name manifest entry
    //noinspection requires-transitive-automatic
    requires transitive org.yaml.snakeyaml;

    exports io.github.madethoughts.mayflower.plugin;
    exports io.github.madethoughts.mayflower.listener;
    exports io.github.madethoughts.mayflower.lifecycle.event;
    exports io.github.madethoughts.mayflower.configuration;

    provides TypeElementVisitor with io.github.madethoughts.mayflower.plugin.yaml.McPluginVisitor;
}