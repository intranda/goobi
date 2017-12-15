package de.sub.goobi.helper;

/**
 * This file is part of the Goobi Application - a Workflow tool for the support of mass digitization.
 * 
 * Visit the websites for more information. 
 *     		- http://www.goobi.org
 *     		- http://launchpad.net/goobi-production
 * 		    - http://gdz.sub.uni-goettingen.de
 * 			- http://www.intranda.com
 * 			- http://digiverso.com 
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
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.goobi.beans.User;
import org.goobi.beans.Process;
import org.goobi.io.WebDavFilter;

import de.sub.goobi.config.ConfigurationHelper;
import de.sub.goobi.export.download.TiffHeader;

public class WebDav implements Serializable {

	private static final long serialVersionUID = -1929234096626965538L;
	private static final Logger logger = Logger.getLogger(WebDav.class);

	/*
 	 * Kopieren bzw. symbolische Links für einen Prozess in das Benutzerhome	
	 */

	private static String DONEDIRECTORYNAME = "fertig/";
	public WebDav(){
		DONEDIRECTORYNAME =ConfigurationHelper.getInstance().getDoneDirectoryName();
	}
	
	
	/**
     * Retrieve all folders from one directory ================================================================
	 */

	public List<String> UploadFromHomeAlle(String inVerzeichnis) {
		List<String> rueckgabe = new ArrayList<String>();
		User aktuellerBenutzer = Helper.getCurrentUser();
		String VerzeichnisAlle;

		try {
			VerzeichnisAlle = aktuellerBenutzer.getHomeDir() + inVerzeichnis;
		} catch (Exception ioe) {
			logger.error("Exception UploadFromHomeAlle()", ioe);
			Helper.setFehlerMeldung("UploadFromHomeAlle abgebrochen, Fehler", ioe.getMessage());
			return rueckgabe;
		}

        List<String> dateien = NIOFileUtils.list(VerzeichnisAlle, new WebDavFilter());

        return dateien;

	}

	/**
     * Remove Folders from Directory ================================================================
	 */
	// TODO: Use generic types
	public void removeFromHomeAlle(List<String> inList, String inVerzeichnis) {
		String VerzeichnisAlle;
		User aktuellerBenutzer = Helper.getCurrentUser();
		try {
			VerzeichnisAlle = aktuellerBenutzer.getHomeDir() + inVerzeichnis;
		} catch (Exception ioe) {
			logger.error("Exception RemoveFromHomeAlle()", ioe);
			Helper.setFehlerMeldung("Upload stopped, error", ioe.getMessage());
			return;
		}

		for (Iterator<String> it = inList.iterator(); it.hasNext();) {
			String myname = it.next();
            FilesystemHelper.deleteSymLink(VerzeichnisAlle + myname);
		}
	}

	public void UploadFromHome(Process myProzess) {
		User aktuellerBenutzer = Helper.getCurrentUser();
        if (aktuellerBenutzer != null) {
        	UploadFromHome(aktuellerBenutzer, myProzess);
        }
	}

	public void UploadFromHome(User inBenutzer, Process myProzess) {
		String nach = "";

		try {
			nach = inBenutzer.getHomeDir();
		} catch (Exception ioe) {
			logger.error("Exception UploadFromHome(...)", ioe);
			Helper.setFehlerMeldung("Aborted upload from home, error", ioe.getMessage());
			return;
		}

		/* prüfen, ob Benutzer Massenupload macht */
		if (inBenutzer != null && inBenutzer.isMitMassendownload()) {
            nach += myProzess.getProjekt().getTitel() + "/";
            Path projectDirectory = Paths.get(nach = nach.replaceAll(" ", "__"));
            if (!Files.exists(projectDirectory)) {
                try {
                    Files.createDirectories(projectDirectory);
                } catch (IOException e) {
                    logger.error(e);
				Helper.setFehlerMeldung(Helper.getTranslation("MassDownloadProjectCreationError", nach.replaceAll(" ", "__")));
				logger.error("Can not create project directory " + nach.replaceAll(" ", "__"));
				return;
			}
		}
        }
		nach += myProzess.getTitel() + " [" + myProzess.getId() + "]";

		/* Leerzeichen maskieren */
		nach = nach.replaceAll(" ", "__");
        Path benutzerHome = Paths.get(nach);

        FilesystemHelper.deleteSymLink(benutzerHome.toString());
	}

	public void DownloadToHome(Process myProzess, int inSchrittID, boolean inNurLesen) {
		saveTiffHeader(myProzess);
		User aktuellerBenutzer = Helper.getCurrentUser();
		String von = "";
		String userHome = "";

		try {
			von = myProzess.getImagesDirectory();
			/* UserHome ermitteln */
			userHome = aktuellerBenutzer.getHomeDir();

			/*
			 * bei Massendownload muss auch das Projekt- und Fertig-Verzeichnis
			 * existieren
			 */
			if (aktuellerBenutzer.isMitMassendownload()) {
                Path projekt = Paths.get(userHome + myProzess.getProjekt().getTitel());
                FilesystemHelper.createDirectoryForUser(projekt.toString(), aktuellerBenutzer.getLogin());
                
                projekt = Paths.get(userHome + DONEDIRECTORYNAME);
                FilesystemHelper.createDirectoryForUser(projekt.toString(), aktuellerBenutzer.getLogin());
			}

		} catch (Exception ioe) {
			logger.error("Exception DownloadToHome()", ioe);
			Helper.setFehlerMeldung("Aborted download to home, error", ioe.getMessage());
			return;
		}

		/*
		 * abhängig davon, ob der Download als "Massendownload" in einen
		 * Projektordner erfolgen soll oder nicht, das Zielverzeichnis
		 * definieren
		 */
		String processLinkName = myProzess.getTitel() + "__[" + myProzess.getId() + "]";
		String nach = userHome;
		if (aktuellerBenutzer.isMitMassendownload() && myProzess.getProjekt() != null) {
            nach += myProzess.getProjekt().getTitel() + "/";
		}
		nach += processLinkName;

		/* Leerzeichen maskieren */
		nach = nach.replaceAll(" ", "__");

		logger.info("von: " + von);
		logger.info("nach: " + nach);

        Path imagePfad = Paths.get(von);
        Path benutzerHome = Paths.get(nach);

		// wenn der Ziellink schon existiert, dann abbrechen
        if (Files.exists(benutzerHome)) {
			return;
		}

		String command = ConfigurationHelper.getInstance().getScriptCreateSymLink()+ " "; 
		if (!command.isEmpty()){
			command += imagePfad + " " + benutzerHome + " ";
			if (inNurLesen) {
				command += ConfigurationHelper.getInstance().getUserForImageReading();
			} else {
				command += aktuellerBenutzer.getLogin();
			}
			try {
	            	ShellScript.legacyCallShell2(command, myProzess.getId());
	            } catch (java.io.IOException ioe) {
				logger.error("IOException DownloadToHome()", ioe);
				Helper.setFehlerMeldung("Download aborted, IOException", ioe.getMessage());
			} catch (InterruptedException e) {
				logger.error("InterruptedException DownloadToHome()", e);
				Helper.setFehlerMeldung("Download aborted, InterruptedException", e.getMessage());
				logger.error(e);
			}
		}
	}

	private void saveTiffHeader(Process inProzess) {
		try {
			/* prüfen, ob Tiff-Header schon existiert */
            if (Files.exists(Paths.get(inProzess.getImagesDirectory() + "tiffwriter.conf"))) {
				return;
			}
			TiffHeader tif = new TiffHeader(inProzess);
            BufferedWriter outfile =
                    new BufferedWriter(new OutputStreamWriter(new FileOutputStream(inProzess.getImagesDirectory() + "tiffwriter.conf"), "utf-8"));
			outfile.write(tif.getTiffAlles());
			outfile.close();
		} catch (Exception e) {
			Helper.setFehlerMeldung("Download aborted", e);
			logger.error(e);
		}
	}

	public int getAnzahlBaende(String inVerzeichnis) {
		try {
			User aktuellerBenutzer = Helper.getCurrentUser();
			String VerzeichnisAlle = aktuellerBenutzer.getHomeDir() + inVerzeichnis;

            return NIOFileUtils.list(VerzeichnisAlle, new WebDavFilter()).size();
		} catch (Exception e) {
			logger.error(e);
			return 0;
		}
	}

}
