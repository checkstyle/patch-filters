///////////////////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code and other text files for adherence to a set of rules.
// Copyright (C) 2001-2026 the original author or authors.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
///////////////////////////////////////////////////////////////////////////////////////////////

package com.puppycrawl.tools.checkstyle;

import java.util.Locale;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * Forces English locale for every test class execution.
 */
public class EnglishLocaleTestExecutionExtension implements BeforeAllCallback, AfterAllCallback {

    private static final ExtensionContext.Namespace NAMESPACE =
            ExtensionContext.Namespace.create(EnglishLocaleTestExecutionExtension.class);

    private static final String DEFAULT_LOCALE_KEY = "defaultLocale";
    private static final String DISPLAY_LOCALE_KEY = "displayLocale";
    private static final String FORMAT_LOCALE_KEY = "formatLocale";

    @Override
    public void beforeAll(ExtensionContext context) {
        final ExtensionContext.Store store = context.getStore(NAMESPACE);
        store.put(DEFAULT_LOCALE_KEY, Locale.getDefault());
        store.put(DISPLAY_LOCALE_KEY, Locale.getDefault(Locale.Category.DISPLAY));
        store.put(FORMAT_LOCALE_KEY, Locale.getDefault(Locale.Category.FORMAT));

        Locale.setDefault(Locale.ENGLISH);
        Locale.setDefault(Locale.Category.DISPLAY, Locale.ENGLISH);
        Locale.setDefault(Locale.Category.FORMAT, Locale.ENGLISH);
    }

    @Override
    public void afterAll(ExtensionContext context) {
        final ExtensionContext.Store store = context.getStore(NAMESPACE);
        final Locale defaultLocale = store.remove(DEFAULT_LOCALE_KEY, Locale.class);
        final Locale displayLocale = store.remove(DISPLAY_LOCALE_KEY, Locale.class);
        final Locale formatLocale = store.remove(FORMAT_LOCALE_KEY, Locale.class);

        if (defaultLocale != null) {
            Locale.setDefault(defaultLocale);
        }
        if (displayLocale != null) {
            Locale.setDefault(Locale.Category.DISPLAY, displayLocale);
        }
        if (formatLocale != null) {
            Locale.setDefault(Locale.Category.FORMAT, formatLocale);
        }
    }
}

