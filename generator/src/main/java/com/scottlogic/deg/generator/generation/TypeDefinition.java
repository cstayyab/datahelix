package com.scottlogic.deg.generator.generation;

import com.scottlogic.deg.generator.DataGeneratorBaseTypes;
import com.scottlogic.deg.generator.fieldspecs.FieldSpec;
import com.scottlogic.deg.generator.generation.fieldvaluesources.FieldValueSource;
import com.scottlogic.deg.generator.inputs.InvalidProfileException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class TypeDefinition {
    public static final TypeDefinition String = StringFieldValueSourceFactory.getTypeDefinition();
    public static final TypeDefinition Numeric = NumericFieldValueSourceFactory.getTypeDefinition();
    public static final TypeDefinition Temporal = TemporalFieldValueSourceFactory.getTypeDefinition();

    private final FieldValueSourceFactory factory;

    public TypeDefinition(FieldValueSourceFactory factory) {
        this.factory = factory;
    }

    public static TypeDefinition parse(String typeString) throws InvalidProfileException {
        Class factoryClass;
        try {
            factoryClass = Class.forName(typeString);
        } catch (ClassNotFoundException e) {
            throw new InvalidProfileException("Unrecognised type in type constraint: " + typeString + "; class cannot be found");
        }

        if (!FieldValueSourceFactory.class.isAssignableFrom(factoryClass)){
            throw new InvalidProfileException("Invalid type in type constraint: " + typeString + "; class does not implement " + FieldValueSourceFactory.class.getSimpleName());
        }

        Constructor constructor;
        try {
            constructor = factoryClass.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new InvalidProfileException("Invalid type in type constraint: " + typeString + "; class does not have an empty constructor");
        }

        try {
            FieldValueSourceFactory factory = (FieldValueSourceFactory) constructor.newInstance();
            return new TypeDefinition(factory);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new InvalidProfileException("Unable to create type provider " + typeString + "; " + e.getMessage());
        }

        throw new InvalidProfileException("Unable to create type provider " + typeString);
    }

    public DataGeneratorBaseTypes getBaseType() {
        return factory.getUnderlyingDataType();
    }

    public FieldValueSource getFieldValueSource(FieldSpec fieldSpec){
        return factory.createValueSource(fieldSpec);
    }

    @Override
    public int hashCode(){
        return factory.getClass().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TypeDefinition) {
            return factory.getClass().equals(((TypeDefinition) obj).factory.getClass());
        }

        return false;
    }

    public boolean isValid(Object value, FieldSpec fieldSpec) {
        return factory.isValid(value, fieldSpec);
    }

    @Override
    public String toString(){
        return "Type: " + factory.getClass().getSimpleName();
    }

    public Class<?> getType() {
        return factory.getClass();
    }
}