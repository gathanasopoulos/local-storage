/*
 * cyborg-core is an extendable  module based framework for Android.
 *
 * Copyright (C) 2018  Adam van der Kruk aka TacB0sS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nu.art.storage;

import com.nu.art.modular.core.ModuleManager;
import com.nu.art.storage.PreferencesModule.SharedPrefs;

import static com.nu.art.storage.PreferencesModule.DefaultStorageGroup;
import static com.nu.art.storage.PreferencesModule.EXPIRES_POSTFIX;

@SuppressWarnings( {
	                   "WeakerAccess",
	                   "unchecked",
	                   "unused"
                   })
abstract class PreferenceKey<PreferenceType, ItemType> {

	PreferenceKey() {}

	PreferenceKey(String key, ItemType defaultValue) {
		setKey(key, defaultValue);
	}

	protected String key;

	protected String storageGroup = DefaultStorageGroup;

	protected ItemType defaultValue;

	protected long expires = -1;

	public PreferenceType setKey(String key, ItemType defaultValue) {
		this.defaultValue = defaultValue;
		this.key = key;
		return (PreferenceType) this;
	}

	public PreferenceType setStorageGroup(String storageGroup) {
		this.storageGroup = storageGroup;
		return (PreferenceType) this;
	}

	public PreferenceType setDefaultValue(ItemType defaultValue) {
		this.defaultValue = defaultValue;
		return (PreferenceType) this;
	}

	public PreferenceType setExpires(long expires) {
		this.expires = expires;
		return (PreferenceType) this;
	}

	public final ItemType get() {
		return get(true);
	}

	public ItemType get(boolean printToLog) {
		SharedPrefs preferences = getPreferences();
		ItemType cache;
		if (expires == -1 || System.currentTimeMillis() - preferences.get(key + EXPIRES_POSTFIX, -1L) < expires) {
			cache = _get(preferences, key, defaultValue);
			if (printToLog)
				getPrefsModule().logInfo("+----+ LOADED: " + key + ": " + cache);
			return cache;
		} else {
			cache = defaultValue;
			if (printToLog)
				getPrefsModule().logInfo("+----+ DEFAULT: " + key + ": " + cache);
			return cache;
		}
	}

	private SharedPrefs getPreferences() {
		return getPrefsModule().getPreferences(storageGroup);
	}

	private PreferencesModule getPrefsModule() {
		return ModuleManager.ModuleManager.getModule(PreferencesModule.class);
	}

	protected abstract ItemType _get(SharedPrefs preferences, String key, ItemType defaultValue);

	public void set(ItemType value) {
		set(value, true);
	}

	public void set(final ItemType value, boolean printToLog) {
		ItemType savedValue = get(false);
		if (areEquals(savedValue, value))
			return;

		final SharedPrefs editor = getPreferences();
		if (printToLog)
			logDebug("+----+ SET: " + key + ": " + value);

		_set(editor, key, value);
		if (expires != -1)
			editor.put(key + EXPIRES_POSTFIX, System.currentTimeMillis());
	}

	private boolean areEquals(ItemType s1, ItemType s2) {
		return s1 == null && s2 == null || s1 != null && s2 != null && s1.equals(s2);
	}

	protected abstract void _set(SharedPrefs preferences, String key, ItemType value);

	public final void clearExpiration() {
		getPreferences().put(key + EXPIRES_POSTFIX, -1L);
	}

	private void removeValue() {
		getPreferences().remove(key);
	}

	public void delete() {
		logDebug("+----+ DELETE: " + key);
		clearExpiration();
		removeValue();
	}

	void logDebug(String s) {
		getPrefsModule().logDebug(s);
	}

	void logError(String s, Exception e) {
		getPrefsModule().logError(s, e);
	}

	void logInfo(String s) {
		getPrefsModule().logInfo(s);
	}
}










