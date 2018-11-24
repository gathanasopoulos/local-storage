package com.nu.art.storage;

import com.nu.art.storage.Test_Setup.PrefModel;

import org.junit.BeforeClass;

public class Test_Storage
	extends Test_StorageBase {

	@BeforeClass
	@SuppressWarnings("unchecked")
	public static void setUp() {
		Test_Setup.init();
	}

	protected <T> void testModel(PrefModel<T> model) {
		setAndValidate(model.pref, model.value);
		deleteAndValidate(model.pref, model.defaultValue);
	}
}
