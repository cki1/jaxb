/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2013 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * http://glassfish.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.impl.util.SchemaWriter;
import com.sun.xml.xsom.parser.SchemaDocument;
import com.sun.xml.xsom.parser.XSOMParser;
import com.sun.xml.xsom.util.DomAnnotationParserFactory;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.OutputStreamWriter;
import java.util.Set;

/**
 * Parses all the schemas specified as the command line arguments,
 * then dumps it (to see if the parsing was done correctly.)
 */
public class Dumper {
    public static void main(String[] args) throws Exception {
        XSOMParser reader = new XSOMParser();
        // set an error handler so that you can receive error messages
        reader.setErrorHandler(new ErrorReporter(System.out));
        // DomAnnotationParserFactory is a convenient default to use
        reader.setAnnotationParser(new DomAnnotationParserFactory());

        try {
            // the parse method can by called many times
            for( int i=0; i<args.length; i++ )
                reader.parse(new File(args[i]));

            XSSchemaSet xss = reader.getResult();
            if(xss==null)
                System.out.println("error");
            else
                new SchemaWriter(new OutputStreamWriter(System.out)).visit(xss);

            dump(reader.getDocuments());
        } catch( SAXException e ) {
            if(e.getException()!=null)
                e.getException().printStackTrace();
            else
                e.printStackTrace();
            throw e;
        }
    }

    private static void dump(Set<SchemaDocument> documents) {
        for (SchemaDocument doc : documents) {
            System.out.println("Schema document: "+doc.getSystemId());
            System.out.println("  target namespace: "+doc.getTargetNamespace());
            for (SchemaDocument ref : doc.getReferencedDocuments()) {
                System.out.print("    -> "+ref.getSystemId());
                if(doc.includes(ref))
                    System.out.print(" (include)");
                System.out.println();
            }
        }

    }
}
