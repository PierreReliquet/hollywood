package com.pierrrrrrrot.sandbox.hollywood;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;

class DependenciesDeclaration extends HashMap<String, DependencyDeclaration> {}

class DependencyDeclaration {
    @JsonProperty("class")
    String clazz;

    HashMap<String, String> properties;

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public HashMap<String, String> getProperties() {
        return properties;
    }

    public void setProperties(HashMap<String, String> properties) {
        this.properties = properties;
    }
}
