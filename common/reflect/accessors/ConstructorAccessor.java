package com.github.joelgodofwar.mmh.common.reflect.accessors;

import java.lang.reflect.Constructor;

public interface ConstructorAccessor {

	/**
	 * Invoke the underlying constructor.
	 *
	 * @param args - the arguments to pass to the method.
	 * @return The return value, or NULL for void methods.
	 */
	Object invoke(Object... args);

	/**
	 * Retrieve the underlying constructor.
	 *
	 * @return The method.
	 */
	Constructor<?> getConstructor();
}