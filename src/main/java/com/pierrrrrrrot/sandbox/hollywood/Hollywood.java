package com.pierrrrrrrot.sandbox.hollywood;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Hollywood {

    private final Map<String, InstantiatedDependency> iocContext = new HashMap<>();

    public void initialize(InputStream configurationInputStream) throws IOException {
        DependenciesDeclaration dependencies = new ObjectMapper().readValue(configurationInputStream, DependenciesDeclaration.class);

        dependencies.forEach((id, dependency) -> {
            if (iocContext.containsKey(id)) {
                throw new RuntimeException("Your casting contains two actors named " + id + " which is not good for the credits.");
            }
            iocContext.put(id, initializeDependency(dependency));
        });

        iocContext.values().forEach(this::setDependencies);
    }

    public <T> T get(Class<T> clazz) {
        for (InstantiatedDependency instantiatedDependency : iocContext.values()) {
            if (instantiatedDependency.clazz == clazz) {
                return clazz.cast(instantiatedDependency.instance);
            }
        }
        throw new RuntimeException("Hollywood cannot find any scenario matching " + clazz.toString());
    }

    private void setDependencies(InstantiatedDependency instantiatedDependency) {
        instantiatedDependency.dependencyDeclaration.properties.forEach((name, dependencyId) -> {
            if (!iocContext.containsKey(dependencyId)) {
                throw new RuntimeException("Error while recording the movie... The actor cannot be found " + dependencyId + " to work with" + instantiatedDependency.clazz.toString());
            }

            try {
                Field field = instantiatedDependency.clazz.getField(name);

                boolean initiallyAccessible = field.isAccessible();
                field.setAccessible(true);
                field.set(instantiatedDependency.instance, iocContext.get(dependencyId).instance);
                field.setAccessible(initiallyAccessible);

            } catch (NoSuchFieldException e) {
                throw new RuntimeException("Hollywood did not manage to find the property " + name + " in the actor " + instantiatedDependency.clazz.toString() + "'s description.", e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Hollywood did not manage to set the property " + name + " for the actor " + instantiatedDependency.clazz.toString() + ".", e);
            }
        });
    }

    private InstantiatedDependency initializeDependency(DependencyDeclaration dependency) {
        try {
            Class<?> dependencyClass = Class.forName(dependency.clazz);

            // TODO check if the constructor stuff is required
            Constructor<?> constructor = dependencyClass.getDeclaredConstructor();
            boolean isInitiallyAccessible = constructor.isAccessible();
            // Let's make the constructor
            constructor.setAccessible(true);
            Object instance = constructor.newInstance();
            constructor.setAccessible(isInitiallyAccessible);
            return new InstantiatedDependency(dependencyClass, instance, dependency);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Hollywood requires a default constructor without parameter and the class " + dependency.clazz + " is missing one.", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Hollywood could not reach the following actor " + dependency.clazz + ".", e);
        } catch (InstantiationException e) {
            throw new RuntimeException("Hollywood did not manage to instantiate the class " + dependency.clazz + " using the default constructor.", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Hollywood would like to gather your casting but did not manage to find the phone number of " + dependency.clazz + ".", e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("The actor finally did not accept to play in your movie " + dependency.clazz);
        }
    }

}

class InstantiatedDependency {
    final Class clazz;
    final Object instance;
    final DependencyDeclaration dependencyDeclaration;

    InstantiatedDependency(Class clazz, Object instance, DependencyDeclaration dependencyDeclaration) {
        this.clazz = clazz;
        this.instance = instance;
        this.dependencyDeclaration = dependencyDeclaration;
    }
}
