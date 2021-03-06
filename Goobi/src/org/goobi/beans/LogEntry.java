package org.goobi.beans;

/**
 * This file is part of the Goobi Application - a Workflow tool for the support of mass digitization.
 * 
 * Visit the websites for more information.
 *          - https://www.intranda.com
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 * 
 */

import java.nio.file.Path;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.goobi.production.enums.LogType;

import de.sub.goobi.config.ConfigurationHelper;
import de.sub.goobi.helper.Helper;
import de.sub.goobi.persistence.managers.ProcessManager;
import lombok.Data;

@Data
public class LogEntry {

    private Integer id;
    private Integer processId;
    private Date creationDate;
    private String userName;
    private LogType type;
    private String content;
    private String secondContent;
    private String thirdContent;
    // used only for LogType.File
    transient Path file;

    public LogEntry() {
    }
    
    public String getFormattedCreationDate() {
        return Helper.getDateAsFormattedString(creationDate);
    }

    public static LogEntry build(Integer processId) {
        LogEntry le = new LogEntry();
        le.setProcessId(processId);
        return le;
    }

    public LogEntry withCreationDate(Date date) {
        this.creationDate = date;
        return this;
    }

    public LogEntry withUsername(String userName) {
        this.userName = userName;
        return this;
    }

    public LogEntry withType(LogType type) {
        this.type = type;
        return this;
    }

    public LogEntry withContent(String content) {
        this.content = content;
        return this;
    }

    public void persist() {
        ProcessManager.saveLogEntry(this);
    }

    /**
     * Return the base name of a file. The basename is the name part of the file without the path
     * 
     * @return
     */

    public String getBasename() {
        String basename = thirdContent;
        if (type == LogType.FILE && StringUtils.isNotBlank(thirdContent)) {
            if (basename.contains("/")) {
                basename = basename.substring(basename.lastIndexOf("/") + 1);
            }
            if (basename.contains("\\")) {
                basename = basename.substring(basename.lastIndexOf("\\") + 1);
            }
        }
        return basename;
    }

    public boolean isExternalFile() {
        return StringUtils.isNotBlank(thirdContent)
                && !thirdContent.contains(ConfigurationHelper.getInstance().getFolderForInternalProcesslogFiles());
    }

    public String getFormattedContent() {
        return content != null ? content.replace("\n", "<br/>") : null;
    }

    public String getFormattedSecondContent() {
        return secondContent != null ? secondContent.replace("\n", "<br/>") : null;
    }

    public String getFormattedThirdContent() {
        return thirdContent != null ? thirdContent.replace("\n", "<br/>") : null;
    }
}
