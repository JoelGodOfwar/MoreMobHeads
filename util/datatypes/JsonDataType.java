package com.github.joelgodofwar.mmh.util.datatypes;

import javax.annotation.Nonnull;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

import com.google.gson.Gson;

/**
 * Lets you store arbitrary data in PDC.
 * @param <T>
 */
public class JsonDataType<T> implements PersistentDataType<String, T> {

    private static final Gson gson = new Gson();
    private final Class<T> typeClass;

    public JsonDataType(Class<T> typeClass) {
        this.typeClass = typeClass;
    }

    @Override
    public @Nonnull Class<String> getPrimitiveType() {
        return String.class;
    }

    @Override
    public @Nonnull Class<T> getComplexType() {
        return typeClass;
    }

    @Nonnull
    @Override
    public String toPrimitive(@Nonnull T complex, @Nonnull PersistentDataAdapterContext persistentDataAdapterContext) {
        return gson.toJson(complex);
    }

    @Nonnull
    @Override
    public T fromPrimitive(@Nonnull String primitive, @Nonnull PersistentDataAdapterContext context) {
        return gson.fromJson(primitive, getComplexType());
    }
}