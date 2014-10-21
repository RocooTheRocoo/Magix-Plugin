package com.rocoo.magix;

import org.bukkit.entity.Player;
import sun.org.mozilla.javascript.internal.ClassShutter;
import sun.org.mozilla.javascript.internal.Context;
import sun.org.mozilla.javascript.internal.GeneratedClassLoader;
import sun.org.mozilla.javascript.internal.SecurityController;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.lang.reflect.Field;

public class ScriptTank {

    private Magix magix;
    private ScriptEngine engine;

    public ScriptTank(Magix magix) {
        this.magix = magix;
        this.engine = new ScriptEngineManager().getEngineByName("javascript");

        this.engine.put("instance", magix);

        try {
            System.setSecurityManager(null);
            Context context = Context.enter();
            ClassShutter shutter = new ClassShutter() {
                @Override
                public boolean visibleToScripts(String s) {
                    return true;
                }
            };

            Field classShutterField = context.getClass().getDeclaredField("classShutter");
            classShutterField.setAccessible(true);
            classShutterField.set(context, shutter);

            SecurityController controller = new SecurityController() {
                @Override
                public GeneratedClassLoader createClassLoader(ClassLoader classLoader, Object o) {
                    return new MyClassLoader();
                }

                @Override
                public Object getDynamicSecurityDomain(Object o) {
                    return o;
                }
            };

            Field controllerField = context.getClass().getDeclaredField("securityController");
            controllerField.setAccessible(true);
            controllerField.set(context, controller);

            Field appClassLoaderField = context.getClass().getDeclaredField("applicationClassLoader");
            appClassLoaderField.setAccessible(true);
            appClassLoaderField.set(context, ClassLoader.getSystemClassLoader());

        } catch (Exception e) {
            throw new RuntimeException("Some problems might occur!", e);
        }

        this.engine.put("out", System.out);
        try {
            if (this.engine != null) {
                if (System.getProperty("java.version").contains("1.8"))
                    this.engine.eval("load('nashorn:mozilla_compat.js')");
                this.engine.eval("importPackage(org.bukkit);");
                this.engine.eval("importPackage(com.rocoo);");
                this.engine.eval("importPackage(java);");
                this.engine.eval("importPackage(net);");
            }
        } catch (ScriptException e) {
            throw new RuntimeException("Oops!", e);
        }

        loadScript();
    }

    private void loadScript() {
        String defaultCode = this.magix.getManager().getDefaultCommand();
        try {
            this.engine.eval("var" + " default_ " + " = function(player) {\n " + defaultCode + "\n}");
        } catch (ScriptException e) {
            throw new RuntimeException("Failed to load the default script!", e);
        }
    }

    public void addScript(String name, String code) {
        try {
            this.engine.eval("var " + name + " = function(player) {\n" + code + "\n}");
        } catch (ScriptException e) {
            throw new RuntimeException("Oops!", e);
        }
    }

    public void execute(String name, Player player) {
        try {
            ((Invocable) this.engine).invokeFunction(name, player);
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute script: " + name, e);
        }
    }

    private static class MyClassLoader extends ClassLoader implements GeneratedClassLoader {

        @Override
        public Class<?> defineClass(String s, byte[] bytes) {
            return super.defineClass(s, bytes, 0, bytes.length);
        }

        @Override
        public void linkClass(Class<?> aClass) {
            super.resolveClass(aClass);
        }
    }
}
