package io.goobi.workflow.xslt;

/**
 * This file is part of the Goobi Application - a Workflow tool for the support of mass digitization.
 * 
 * Visit the websites for more information.
 *     		- https://goobi.io
 * 			- https://www.intranda.com
 * 			- https://github.com/intranda/goobi-workflow
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * Linking this library statically or dynamically with other modules is making a combined work based on this library. Thus, the terms and conditions
 * of the GNU General Public License cover the whole combination. As a special exception, the copyright holders of this library give you permission to
 * link this library with independent modules to produce an executable, regardless of the license terms of these independent modules, and to copy and
 * distribute the resulting executable under terms of your choice, provided that you also meet, for each linked independent module, the terms and
 * conditions of the license of that module. An independent module is a module which is not derived from or based on this library. If you modify this
 * library, you may extend this exception to your version of the library, but you are not obliged to do so. If you do not wish to do so, delete this
 * exception statement from your version.
 */
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopConfParser;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.FopFactoryBuilder;
import org.apache.fop.apps.MimeConstants;
import org.goobi.beans.Process;

import de.sub.goobi.config.ConfigurationHelper;
import de.sub.goobi.helper.exceptions.ExportFileException;
import lombok.extern.log4j.Log4j2;

/**
 * This class provides generating a docket based on the generated xml log
 * 
 * @author Steffen Hankiewicz
 */

@Log4j2
public class XsltToPdf {

    /**
     * This method exports the production metadata as run note to a given stream. the docket.xsl has to be in the config-folder
     * 
     * @param process the process to export
     * @param os the OutputStream to write the contents to
     * @throws IOException
     * @throws ExportFileException
     */
    public void startExport(Process process, OutputStream os, String xsltfile, IXsltPreparator inexport) throws IOException {

        IXsltPreparator exl = inexport;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        exl.startExport(process, out, null);

        // generate pdf file
        StreamSource source = new StreamSource(new ByteArrayInputStream(out.toByteArray()));
        StreamSource transformSource = new StreamSource(xsltfile);
        File xconf = new File(ConfigurationHelper.getInstance().getXsltFolder() + "config.xml");
        FopConfParser parser = null;
        try {
            //parsing configuration
            parser = new FopConfParser(xconf);
        } catch (Exception e) {
            log.error(e);
            return;

        }
        //building the factory with the user options
        FopFactoryBuilder builder = parser.getFopFactoryBuilder();
        FopFactory fopFactory = builder.build();
        FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        // transform xml
        Transformer xslfoTransformer = getTransformer(transformSource);
        try {
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, outStream);
            Result res = new SAXResult(fop.getDefaultHandler());
            xslfoTransformer.transform(source, res);
        } catch (FOPException e) {
            throw new IOException("FOPException occured", e);
        } catch (TransformerException e) {
            throw new IOException("TransformerException occured", e);
        }

        // write the content to output stream
        byte[] pdfBytes = outStream.toByteArray();
        os.write(pdfBytes);
    }

    /**
     * This method is used to generate the PDF out of the xls and xml file and writes
     * it into the given output stream
     * 
     * @param processList list of processes to use for the xml generation
     * @param os OutputStream where to write to
     * @param xsltfile the name of the xsl file to use for the conversion
     * 
     * @throws IOException
     */
    public void startExport(List<Process> processList, OutputStream os, String xsltfile) throws IOException {

        XsltPreparatorDocket exl = new XsltPreparatorDocket();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        exl.startExport(processList, out, null);

        // generate pdf file
        StreamSource source = new StreamSource(new ByteArrayInputStream(out.toByteArray()));
        StreamSource transformSource = new StreamSource(xsltfile);

        File xconf = new File(ConfigurationHelper.getInstance().getXsltFolder() + "config.xml");
        FopConfParser parser = null;
        try {
            //parsing configuration
            parser = new FopConfParser(xconf);
        } catch (Exception e) {
            log.error(e);
            return;

        }
        //building the factory with the user options
        FopFactoryBuilder builder = parser.getFopFactoryBuilder();
        FopFactory fopFactory = builder.build();
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        // transform xml
        try {
            Transformer xslfoTransformer = TransformerFactory.newInstance().newTransformer(transformSource);
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, outStream);
            Result res = new SAXResult(fop.getDefaultHandler());
            xslfoTransformer.transform(source, res);
        } catch (FOPException e) {
            throw new IOException("FOPException occured", e);
        } catch (TransformerException e) {
            throw new IOException("TransformerException occured", e);
        }

        // write the content to output stream
        byte[] pdfBytes = outStream.toByteArray();
        os.write(pdfBytes);
    }

    /**
     * internal method to get a transformer object
     * @param streamSource
     * @return
     */
    private static Transformer getTransformer(StreamSource streamSource) {
        // setup the xslt transformer
        net.sf.saxon.TransformerFactoryImpl impl = new net.sf.saxon.TransformerFactoryImpl();
        try {
            return impl.newTransformer(streamSource);
        } catch (TransformerConfigurationException e) {
        }
        return null;
    }

}
