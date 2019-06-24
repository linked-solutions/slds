/*
 * The MIT License
 *
 * Copyright 2019  FactsMission AG, Switzerland.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package solutions.linked.slds;

import java.util.Arrays;
import javax.ws.rs.core.MultivaluedMap;

/**
 *
 * @author me
 */
class HeaderUtil {

    static void addValue(MultivaluedMap<String, Object> headers, String header, String value) {
        Object varyValue = headers.getFirst(header);
        String[] varyValues = varyValue == null ? new String[0] : varyValue.toString().split(",");
        String[] newValues = Arrays.copyOf(varyValues, varyValues.length + 1);
        newValues[varyValues.length] = value;
        headers.putSingle("Vary", String.join(", ", newValues));
    }   
}
