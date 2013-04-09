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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.goobi.beans.Processproperty;
import org.goobi.beans.Step;
import org.goobi.beans.User;
import org.goobi.beans.Usergroup;

import org.goobi.beans.Process;
import de.sub.goobi.beans.Schritteigenschaft;
import de.sub.goobi.beans.Vorlage;
import de.sub.goobi.beans.Vorlageeigenschaft;
import de.sub.goobi.beans.Werkstueck;
import de.sub.goobi.beans.Werkstueckeigenschaft;

public class BeanHelper {

	

	public void EigenschaftHinzufuegen(Process inProzess, String inTitel, String inWert) {
		Processproperty eig = new Processproperty();
		eig.setTitel(inTitel);
		eig.setWert(inWert);
		eig.setProzess(inProzess);
		List<Processproperty> eigenschaften = inProzess.getEigenschaften();
		if (eigenschaften == null) {
			eigenschaften = new ArrayList<Processproperty>();
		}
		eigenschaften.add(eig);
	}

	

	public void EigenschaftHinzufuegen(Step inSchritt, String inTitel, String inWert) {
		Schritteigenschaft eig = new Schritteigenschaft();
		eig.setTitel(inTitel);
		eig.setWert(inWert);
		eig.setSchritt(inSchritt);
		List<Schritteigenschaft> eigenschaften = inSchritt.getEigenschaften();
		if (eigenschaften == null) {
			eigenschaften = new ArrayList<Schritteigenschaft>();
		}
		eigenschaften.add(eig);
	}

	

	public void EigenschaftHinzufuegen(Vorlage inVorlage, String inTitel, String inWert) {
		Vorlageeigenschaft eig = new Vorlageeigenschaft();
		eig.setTitel(inTitel);
		eig.setWert(inWert);
		eig.setVorlage(inVorlage);
		Set<Vorlageeigenschaft> eigenschaften = inVorlage.getEigenschaften();
		if (eigenschaften == null) {
			eigenschaften = new HashSet<Vorlageeigenschaft>();
		}
		eigenschaften.add(eig);
	}

	

	public void EigenschaftHinzufuegen(Werkstueck inWerkstueck, String inTitel, String inWert) {
		Werkstueckeigenschaft eig = new Werkstueckeigenschaft();
		eig.setTitel(inTitel);
		eig.setWert(inWert);
		eig.setWerkstueck(inWerkstueck);
		Set<Werkstueckeigenschaft> eigenschaften = inWerkstueck.getEigenschaften();
		if (eigenschaften == null) {
			eigenschaften = new HashSet<Werkstueckeigenschaft>();
		}
		eigenschaften.add(eig);
	}

	

	public void SchritteKopieren(Process prozessVorlage, Process prozessKopie) {
		List<Step> mySchritte = new ArrayList<Step>();
		for (Step step : prozessVorlage.getSchritteList()) {

			/* --------------------------------
			 * Details des Schritts
			 * --------------------------------*/
			Step stepneu = new Step();
			stepneu.setTypAutomatisch(step.isTypAutomatisch());
			stepneu.setScriptname1(step.getScriptname1());
			stepneu.setScriptname2(step.getScriptname2());
			stepneu.setScriptname3(step.getScriptname3());
			stepneu.setScriptname4(step.getScriptname4());
			stepneu.setScriptname5(step.getScriptname5());
			
			stepneu.setTypAutomatischScriptpfad(step.getTypAutomatischScriptpfad());
			stepneu.setTypAutomatischScriptpfad2(step.getTypAutomatischScriptpfad2());
			stepneu.setTypAutomatischScriptpfad3(step.getTypAutomatischScriptpfad3());
			stepneu.setTypAutomatischScriptpfad4(step.getTypAutomatischScriptpfad4());
			stepneu.setTypAutomatischScriptpfad5(step.getTypAutomatischScriptpfad5());
			stepneu.setBatchStep(step.getBatchStep());
			stepneu.setTypScriptStep(step.getTypScriptStep());
			stepneu.setTypBeimAnnehmenAbschliessen(step.isTypBeimAnnehmenAbschliessen());
			stepneu.setTypBeimAnnehmenModul(step.isTypBeimAnnehmenModul());
			stepneu.setTypBeimAnnehmenModulUndAbschliessen(step.isTypBeimAnnehmenModulUndAbschliessen());
			stepneu.setTypModulName(step.getTypModulName());
			stepneu.setTypExportDMS(step.isTypExportDMS());
			stepneu.setTypExportRus(step.isTypExportRus());
			stepneu.setTypImagesLesen(step.isTypImagesLesen());
			stepneu.setTypImagesSchreiben(step.isTypImagesSchreiben());
			stepneu.setTypImportFileUpload(step.isTypImportFileUpload());
			stepneu.setTypMetadaten(step.isTypMetadaten());
			stepneu.setPrioritaet(step.getPrioritaet());
			stepneu.setBearbeitungsstatusEnum(step.getBearbeitungsstatusEnum());
			stepneu.setReihenfolge(step.getReihenfolge());
			stepneu.setTitel(step.getTitel());
			stepneu.setHomeverzeichnisNutzen(step.getHomeverzeichnisNutzen());
			stepneu.setProzess(prozessKopie);
			
			stepneu.setStepPlugin(step.getStepPlugin());
			stepneu.setValidationPlugin(step.getValidationPlugin());
			
			//Fixing a bug found by Holger Busse (Berlin)
			stepneu.setTypBeimAbschliessenVerifizieren(step.isTypBeimAbschliessenVerifizieren());
			
			/* --------------------------------
			 * Eigenschaften des Schritts
			 * --------------------------------*/
			List<Schritteigenschaft> myEigenschaften = new ArrayList<Schritteigenschaft>();
			for (Schritteigenschaft eig : step.getEigenschaftenList()) {
				Schritteigenschaft eigneu = new Schritteigenschaft();
				eigneu.setIstObligatorisch(eig.isIstObligatorisch());
				eigneu.setType(eig.getType());
				eigneu.setTitel(eig.getTitel());
				eigneu.setWert(eig.getWert());
				eigneu.setSchritt(stepneu);
				myEigenschaften.add(eigneu);
			}
			stepneu.setEigenschaften(myEigenschaften);

			/* --------------------------------
			 * Benutzer übernehmen
			 * --------------------------------*/
			List<User> myBenutzer = new ArrayList<User>();
			for (User benneu : step.getBenutzer()) {
				myBenutzer.add(benneu);
			}
			stepneu.setBenutzer(myBenutzer);

			/* --------------------------------
			 * Benutzergruppen übernehmen
			 * --------------------------------*/
			List<Usergroup> myBenutzergruppen = new ArrayList<Usergroup>();
			for (Usergroup grupneu : step.getBenutzergruppen()) {
				myBenutzergruppen.add(grupneu);
			}
			stepneu.setBenutzergruppen(myBenutzergruppen);

			/* Schritt speichern */
			mySchritte.add(stepneu);
		}
		prozessKopie.setSchritte(mySchritte);
	}

	public void WerkstueckeKopieren(Process prozessVorlage, Process prozessKopie) {
		List<Werkstueck> myWerkstuecke = new ArrayList<Werkstueck>();
		for (Werkstueck werk : prozessVorlage.getWerkstuecke()) {
			/* --------------------------------
			 * Details des Werkstücks
			 * --------------------------------*/
			Werkstueck werkneu = new Werkstueck();
			werkneu.setProzess(prozessKopie);

			/* --------------------------------
			 * Eigenschaften des Schritts
			 * --------------------------------*/
			HashSet<Werkstueckeigenschaft> myEigenschaften = new HashSet<Werkstueckeigenschaft>();
			for (Iterator<Werkstueckeigenschaft> iterator = werk.getEigenschaften().iterator(); iterator.hasNext();) {
				Werkstueckeigenschaft eig = iterator.next();
				Werkstueckeigenschaft eigneu = new Werkstueckeigenschaft();
				eigneu.setIstObligatorisch(eig.isIstObligatorisch());
				eigneu.setType(eig.getType());
				eigneu.setTitel(eig.getTitel());
				eigneu.setWert(eig.getWert());
				eigneu.setWerkstueck(werkneu);
				myEigenschaften.add(eigneu);
			}
			werkneu.setEigenschaften(myEigenschaften);

			/* Schritt speichern */
			myWerkstuecke.add(werkneu);
		}
		prozessKopie.setWerkstuecke(myWerkstuecke);
	}

	public void EigenschaftenKopieren(Process prozessVorlage, Process prozessKopie) {
		// TODO Reihenfolge!
	    List<Processproperty> myEigenschaften = new ArrayList<Processproperty>();
		for (Iterator<Processproperty> iterator = prozessVorlage.getEigenschaftenList().iterator(); iterator.hasNext();) {
			Processproperty eig = iterator.next();
			Processproperty eigneu = new Processproperty();
			eigneu.setIstObligatorisch(eig.isIstObligatorisch());
			eigneu.setType(eig.getType());
			eigneu.setTitel(eig.getTitel());
			eigneu.setWert(eig.getWert());
			eigneu.setProzess(prozessKopie);
			myEigenschaften.add(eigneu);
		}
		// TODO read property configuration
		prozessKopie.setEigenschaften(myEigenschaften);
	}

	public void ScanvorlagenKopieren(Process prozessVorlage, Process prozessKopie) {
		List<Vorlage> myVorlagen = new ArrayList<Vorlage>();
		for (Vorlage vor : prozessVorlage.getVorlagen()) {
			/* --------------------------------
			 * Details der Vorlage
			 * --------------------------------*/
			Vorlage vorneu = new Vorlage();
			vorneu.setHerkunft(vor.getHerkunft());
			vorneu.setProzess(prozessKopie);

			/* --------------------------------
			 * Eigenschaften des Schritts
			 * --------------------------------*/
			HashSet<Vorlageeigenschaft> myEigenschaften = new HashSet<Vorlageeigenschaft>();
			for (Iterator<Vorlageeigenschaft> iterator = vor.getEigenschaften().iterator(); iterator.hasNext();) {
				Vorlageeigenschaft eig = iterator.next();
				Vorlageeigenschaft eigneu = new Vorlageeigenschaft();
				eigneu.setIstObligatorisch(eig.isIstObligatorisch());
				eigneu.setType(eig.getType());
				eigneu.setTitel(eig.getTitel());
				eigneu.setWert(eig.getWert());
				eigneu.setVorlage(vorneu);
				myEigenschaften.add(eigneu);
			}
			vorneu.setEigenschaften(myEigenschaften);

			/* Schritt speichern */
			myVorlagen.add(vorneu);
		}
		prozessKopie.setVorlagen(myVorlagen);
	}

	public String WerkstueckEigenschaftErmitteln(Process myProzess, String inEigenschaft) {
		String Eigenschaft = "";
		for (Werkstueck myWerkstueck : myProzess.getWerkstueckeList()) {
			for (Werkstueckeigenschaft eigenschaft : myWerkstueck.getEigenschaftenList()) {
				if (eigenschaft.getTitel().equals(inEigenschaft)) {
					Eigenschaft = eigenschaft.getWert();
				}
			}
		}
		return Eigenschaft;
	}

	public String ScanvorlagenEigenschaftErmitteln(Process myProzess, String inEigenschaft) {
		String Eigenschaft = "";
		for (Vorlage myVorlage : myProzess.getVorlagenList()) {
			for (Vorlageeigenschaft eigenschaft : myVorlage.getEigenschaftenList()) {
				if (eigenschaft.getTitel().equals(inEigenschaft)) {
					Eigenschaft = eigenschaft.getWert();
				}
			}
		}
		return Eigenschaft;
	}

	public void WerkstueckEigenschaftAendern(Process myProzess, String inEigenschaft, String inWert) {
		for (Werkstueck myWerkstueck : myProzess.getWerkstueckeList()) {
			for (Werkstueckeigenschaft eigenschaft : myWerkstueck.getEigenschaftenList()) {
				if (eigenschaft.getTitel().equals(inEigenschaft)) {
					eigenschaft.setWert(inWert);
				}
			}
		}
	}

	public void ScanvorlagenEigenschaftAendern(Process myProzess, String inEigenschaft, String inWert) {
		for (Vorlage myVorlage : myProzess.getVorlagenList()) {
			for (Vorlageeigenschaft eigenschaft : myVorlage.getEigenschaftenList()) {
				if (eigenschaft.getTitel().equals(inEigenschaft)) {
					eigenschaft.setWert(inWert);
				}
			}
		}
	}

	public void WerkstueckEigenschaftLoeschen(Process myProzess, String inEigenschaft, String inWert) {
		for (Werkstueck myWerkstueck : myProzess.getWerkstueckeList()) {
			for (Werkstueckeigenschaft eigenschaft : myWerkstueck.getEigenschaftenList()) {
				if (eigenschaft.getTitel().equals(inEigenschaft) && eigenschaft.getWert().equals(inWert)) {
					myWerkstueck.getEigenschaften().remove(eigenschaft);
				}
			}
		}
	}

	public void ScanvorlagenEigenschaftLoeschen(Process myProzess, String inEigenschaft, String inWert) {
		for (Vorlage myVorlage : myProzess.getVorlagenList()) {
			for (Vorlageeigenschaft eigenschaft : myVorlage.getEigenschaftenList()) {
				if (eigenschaft.getTitel().equals(inEigenschaft) && eigenschaft.getWert().equals(inWert)) {
					myVorlage.getEigenschaften().remove(eigenschaft);
				}
			}
		}
	}

	public void WerkstueckEigenschaftDoppelteLoeschen(Process myProzess) {
		for (Werkstueck myWerkstueck : myProzess.getWerkstueckeList()) {
			List<String> einzelstuecke = new ArrayList<String>();
			for (Werkstueckeigenschaft eigenschaft : myWerkstueck.getEigenschaftenList()) {
				/* prüfen, ob die Eigenschaft doppelt, wenn ja, löschen */
				if (einzelstuecke.contains(eigenschaft.getTitel() + "|" + eigenschaft.getWert())) {
					myWerkstueck.getEigenschaften().remove(eigenschaft);
				} else {
					einzelstuecke.add(eigenschaft.getTitel() + "|" + eigenschaft.getWert());
				}
			}
		}
	}

	public void ScanvorlageEigenschaftDoppelteLoeschen(Process myProzess) {
		for (Vorlage myVorlage : myProzess.getVorlagenList()) {
			List<String> einzelstuecke = new ArrayList<String>();
			for (Vorlageeigenschaft eigenschaft : myVorlage.getEigenschaftenList()) {
				/* prüfen, ob die Eigenschaft doppelt, wenn ja, löschen */
				if (einzelstuecke.contains(eigenschaft.getTitel() + "|" + eigenschaft.getWert())) {
					myVorlage.getEigenschaften().remove(eigenschaft);
				} else {
					einzelstuecke.add(eigenschaft.getTitel() + "|" + eigenschaft.getWert());
				}
			}
		}
	}
}
