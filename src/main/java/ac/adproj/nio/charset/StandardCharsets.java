/*
    Copyright (C) 2011-2020 Andy Cheung

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License Version 2 only, 
    as published by the Free Software Foundation.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.

    The copyright holder permits this library as subject to the "Classpath"
    exception as provided below:

        Linking this library statically or dynamically with other modules is
        making a combined work based on this library.  Thus, the terms and
        conditions of the GNU General Public License cover the whole
        combination.

        As a special exception, the copyright holders of this library give you
        permission to link this library with independent modules to produce an
        executable, regardless of the license terms of these independent
        modules, and to copy and distribute the resulting executable under
        terms of your choice, provided that you also meet, for each linked
        independent module, the terms and conditions of the license of that
        module.  An independent module is a module which is not derived from
        or based on this library.  If you modify this library, you may extend
        this exception to your version of the library, but you are not
        obligated to do so.  If you do not wish to do so, delete this
        exception statement from your version.
*/

package ac.adproj.nio.charset;

/*
 * Based on the code from OpenJDK.
 *
 * Copyright (c) 2011, 2019, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

import java.nio.charset.Charset;

/**
 * Constant definitions for the standard {@link Charset charsets}. These
 * charsets are guaranteed to be available on every implementation of the Java
 * platform.
 *
 * @see <a href="Charset.html#standard">Standard Charsets</a>
 */
public final class StandardCharsets {

    // To avoid accidental eager initialization of often unused Charsets
    // from happening while the VM is booting up, which may delay
    // initialization of VM components, we should generally avoid depending
    // on this class from elsewhere in java.base.

    private StandardCharsets() {
        throw new AssertionError("No java.nio.charset.StandardCharsets instances for you!");
    }

    /**
     * Seven-bit ASCII, also known as ISO646-US, also known as the
     * Basic Latin block of the Unicode character set.
     */
    public static final Charset US_ASCII = Charset.forName("US-ASCII");

    /**
     * ISO Latin Alphabet {@literal No. 1}, also known as ISO-LATIN-1.
     */
    public static final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");

    /**
     * Eight-bit UCS Transformation Format.
     */
    public static final Charset UTF_8 = Charset.forName("UTF-8");
}
