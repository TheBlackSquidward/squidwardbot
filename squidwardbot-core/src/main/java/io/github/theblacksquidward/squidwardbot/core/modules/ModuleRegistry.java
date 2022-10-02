package io.github.theblacksquidward.squidwardbot.core.modules;

import com.google.common.base.Stopwatch;
import io.github.theblacksquidward.squidwardbot.core.utils.StringUtils;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.UnmodifiableView;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class ModuleRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModuleRegistry.class);
    private static final ModuleRegistry INSTANCE = new ModuleRegistry();
    private static final List<ISquidwardBotModule> MODULES = new ArrayList<>();

    public void captureAndInitModules(Reflections reflections) {
        LOGGER.info("Beginning to scan for modules...");
        final Stopwatch timer = Stopwatch.createStarted();
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(SquidwardBotModule.class);
        LOGGER.info("Successfully found " + annotatedClasses.size() + " modules, Attempting to register them...");
        annotatedClasses.forEach((annotatedClass) -> {
            if(ISquidwardBotModule.class.isAssignableFrom(annotatedClass)) {
                addModuleFromClass(annotatedClass);
            }
        });
        LOGGER.debug("Loaded modules: {}", StringUtils.getIndentedStringList(getModules().stream()
                .map(ISquidwardBotModule::getModuleIdentifier)
                .map(name -> WordUtils.capitalize(name.replace("_", " ").replace("squidwardbot", "SquidwardBot")))
                .toList()
        ));
        timer.stop();
        LOGGER.info("Successfully found and registered {} modules in {}.", getModules().size(), timer);
        forEachPlugin(ISquidwardBotModule::onModuleRegister);
    }

    public static ModuleRegistry getInstance() {
        return INSTANCE;
    }

    @UnmodifiableView
    public List<ISquidwardBotModule> getModules(){
        return Collections.unmodifiableList(MODULES);
    }

    public void forEachPlugin(Consumer<ISquidwardBotModule> function) {
        getModules().forEach(function);
    }

    private boolean addModuleIfAbsent(ISquidwardBotModule module) {
        if(MODULES.stream().filter((existingModule) -> module.getModuleIdentifier().equalsIgnoreCase(existingModule.getModuleIdentifier())).toList().size() == 0) {
            MODULES.add(module);
            return true;
        }
        return false;
    }

    private void addModuleFromClass(Class<?> clazz) {
        try {
            if(addModuleIfAbsent((ISquidwardBotModule) clazz.getDeclaredConstructor().newInstance())) {
                LOGGER.info("Registered plugin: {}", clazz.getName());
            }
        } catch (Exception e) {
            LOGGER.error("Error loading plugin: {}", clazz.getName(), e) ;
        }
    }

}
