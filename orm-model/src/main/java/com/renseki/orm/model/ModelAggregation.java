package com.renseki.orm.model;

import com.efitrac.commons.annotation.Model;
import com.efitrac.commons.model.AbstractModel;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ModelAggregation {

    private final Collection<Class<? extends AbstractModel>> modelClasses;
    private ModelAggregation(Builder b) {
        this.modelClasses = b.modelClasses;
    }

    public Collection<Class<? extends AbstractModel>> getModelClasses() {
        return modelClasses;
    }

    public static class Builder {
        Collection<Class<? extends AbstractModel>> modelClasses = new ArrayList<>();

        final String packageName;
        public Builder(String packageName) {
            this.packageName = packageName;
        }

        public ModelAggregation build() {

            Reflections reflections = new Reflections(packageName);
            modelClasses.addAll(reflections.getSubTypesOf(AbstractModel.class));

            return new ModelAggregation(this);
        }
    }


}
