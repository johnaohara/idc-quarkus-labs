package com.example;

import java.util.HashSet;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

public class NativeRuntimeHints implements RuntimeHintsRegistrar {
	@Override
	public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
		hints.serialization().registerType(HashSet.class);
	}
}
