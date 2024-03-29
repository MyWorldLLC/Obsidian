package myworld.obsidian.display.skin.chipmunk;

import chipmunk.binary.BinaryModule;
import chipmunk.compiler.ChipmunkCompiler;
import chipmunk.compiler.ChipmunkSource;
import chipmunk.compiler.Compilation;
import chipmunk.vm.ChipmunkVM;
import chipmunk.vm.ModuleLoader;
import chipmunk.vm.jvm.CompilationUnit;
import myworld.obsidian.display.skin.*;
import myworld.obsidian.display.skin.resolvers.ClasspathResolver;

import java.util.*;
import java.util.stream.Collectors;

public class ChipmunkSkinLoader {

    public static final String DEFAULT_SKIN_FILE_NAME = "Skin.chp";

    public static UISkin load(ResourceResolver resolver) throws Exception {
        return load(resolver, DEFAULT_SKIN_FILE_NAME);
    }

    public static UISkin load(ResourceResolver resolver, String skinFileName) throws Exception {

        var vm = new ChipmunkVM();
        try{
            vm.start();

            var skinModule = new SkinModule();
            var varModule = new VarModule();
            var styleModule = new StyleModule();

            var loader = new ModuleLoader();
            loader.registerNativeFactory(SkinModule.MODULE_NAME, () -> skinModule);
            loader.registerNativeFactory(VarModule.MODULE_NAME, () -> varModule);
            loader.registerNativeFactory(StyleModule.MODULE_NAME, () -> styleModule);
            loader.registerNativeFactory(GraphicsModule.MODULE_NAME, GraphicsModule::new);

            var runtimeUnit = new CompilationUnit();
            runtimeUnit.setModuleLoader(new ModuleLoader());
            runtimeUnit.getModuleLoader().addToLoaded(Arrays.asList(compile(loader, new ChipmunkSource(resolver.resolve(skinFileName), skinFileName))));
            runtimeUnit.getModuleLoader().setDelegate(loader);
            runtimeUnit.setEntryModule("skin");
            runtimeUnit.setEntryMethodName("skin");
            var script = vm.compileScript(runtimeUnit);
            script.getModuleLoader().setDelegate(loader);
            script.run();

            var skin = new UISkin(skinModule.getSkinName(), resolver);
            skin.variables().set(varModule.getVars());
            skin.addFonts(skinModule.getFonts());

            skin.addImages(skinModule.getImages());

            skin.addSvgs(skinModule.getSvgs());

            styleModule.getStyles().forEach(style -> skin.styles().put(style.name(), style));

            var helpers = new ArrayList<ChipmunkSource>();
            for(var helper : skinModule.getHelpers()){
                helpers.add(new ChipmunkSource(resolver.resolve(helper), helper));
            }

            var helperModules = compile(null, helpers.toArray(new ChipmunkSource[]{}));

            for(var componentPath : skinModule.getComponents()){

                var componentLoader = new ModuleLoader();
                var componentModule = new ComponentModule();
                var componentVarModule = new VarModule();
                var componentStyleModule = new StyleModule();
                varModule.getVars().forEach((k, v) -> componentVarModule.getVars().put(k, v));
                componentLoader.registerNativeFactory(ComponentModule.MODULE_NAME, () -> componentModule);
                componentLoader.registerNativeFactory(VarModule.MODULE_NAME, () -> componentVarModule);
                componentLoader.registerNativeFactory(StyleModule.MODULE_NAME, () -> componentStyleModule);
                componentLoader.registerNativeFactory(GraphicsModule.MODULE_NAME, GraphicsModule::new);

                var componentUnit = new CompilationUnit();
                componentUnit.setModuleLoader(new ModuleLoader());
                componentUnit.getModuleLoader().addToLoaded(Arrays.asList(helperModules));
                componentUnit.getModuleLoader().addToLoaded(Arrays.asList(compile(componentLoader, new ChipmunkSource(resolver.resolve(componentPath), componentPath))));
                componentUnit.setEntryModule("component");
                componentUnit.setEntryMethodName("component");

                var componentScript = vm.compileScript(componentUnit);
                componentScript.getModuleLoader().setDelegate(componentLoader);
                componentScript.run();

                var styles = componentModule.getStyles();
                styles.addAll(componentStyleModule.getStyles());

                skin.addComponentSkin(new ComponentSkin(
                        componentModule.getComponentName(),
                        componentModule.getComponentInterface(),
                        styles.toArray(new StyleClass[0]))
                );

            }

            vm.stop();

            return skin;
        }finally{
            vm.stop();
        }

    }

    protected static BinaryModule[] compile(ModuleLoader dependencies, ChipmunkSource... sources){
        var compiler = new ChipmunkCompiler();

        var compilation = new Compilation();
        compilation.getSources().addAll(Arrays.asList(sources));
        compiler.getModuleLoader().setDelegate(dependencies);

        compilation.getSources().forEach(s -> {
            if(s.getIs() == null){
                throw new IllegalStateException("Could not resolve Chipmunk source path: " + s.getFileName());
            }
        });

        return compiler.compile(compilation);
    }

    public static UISkin loadFromClasspath(String path) throws Exception {
        return load(new ClasspathResolver(path));
    }

    public static UISkin loadFromClasspath(Class<?> resolveFrom, String path) throws Exception {
        return load(new ClasspathResolver(resolveFrom, path));
    }

    public static UISkin loadFromClasspath(String path, String skinFileName) throws Exception {
        return load(new ClasspathResolver(path), skinFileName);
    }

    public static UISkin loadFromClasspath(Class<?> resolveFrom, String path, String skinFileName) throws Exception {
        return load(new ClasspathResolver(resolveFrom, path), skinFileName);
    }

}
