package de.sub.goobi.persistence.managers;

/**
 * This file is part of the Goobi Application - a Workflow tool for the support of mass digitization.
 * 
 * Visit the websites for more information. 
 *          - http://www.intranda.com
 *          - http://digiverso.com 
 *          - http://www.goobi.org
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
 */
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.log4j.Logger;
import org.goobi.beans.HistoryEvent;

import de.sub.goobi.helper.enums.HistoryEventType;

public class HistoryMysqlHelper {
    private static final Logger logger = Logger.getLogger(HistoryMysqlHelper.class);

    public static List<HistoryEvent> getHistoryEvents(int processId) throws SQLException {
        String sql = "SELECT * FROM history WHERE processID = " + processId;
        Connection connection = null;
        try {
            connection = MySQLHelper.getInstance().getConnection();
            List<HistoryEvent> list = new QueryRunner().query(connection, sql, resultSetToHistoryListHandler);
            return list;
        } finally {
            if (connection != null) {
                MySQLHelper.closeConnection(connection);
            }
        }
    }

    public static void updateHistory(Integer id, Date date, double order, String value, int type, int processId) throws SQLException {
        Connection connection = null;
        Timestamp datetime = new Timestamp(date.getTime());

        try {
            connection = MySQLHelper.getInstance().getConnection();
            QueryRunner run = new QueryRunner();
            // String propNames = "numericValue, stringvalue, type, date, processId";
            Object[] param = { order, value == null ? null : value, type, datetime, processId };
            String sql = "Update history set numericValue = ?, stringvalue = ?,  type = ?, date = ?, processId =? WHERE historyid = " + id;
            logger.trace("added history event " + sql + ", " + Arrays.toString(param));
            run.update(connection, sql, param);
        } finally {
            if (connection != null) {
                MySQLHelper.closeConnection(connection);
            }
        }
    }

    public static void addHistory(Date date, double order, String value, int type, int processId) throws SQLException {
        Connection connection = null;
        Timestamp datetime = new Timestamp(date.getTime());

        try {
            connection = MySQLHelper.getInstance().getConnection();
            QueryRunner run = new QueryRunner();
            // String propNames = "numericValue, stringvalue, type, date, processId";
            Object[] param = { order, value, type, datetime, processId };
            String sql = "INSERT INTO " + "history" + " (numericValue, stringvalue, type, date, processId) VALUES ( ?, ?, ?, ? ,?)";
            logger.trace("added history event " + sql + ", " + Arrays.toString(param));
            run.update(connection, sql, param);
        } finally {
            if (connection != null) {
                MySQLHelper.closeConnection(connection);
            }
        }
    }

    public static void updateHistoryEvent(HistoryEvent he) throws SQLException {
        Connection connection = null;
        Timestamp datetime = new Timestamp(he.getDate().getTime());

        try {
            connection = MySQLHelper.getInstance().getConnection();
            QueryRunner run = new QueryRunner();
            // String propNames = "numericValue, stringvalue, type, date, processId";
            Object[] param = { he.getNumericValue(), he.getStringValue(), he.getHistoryType().getValue(), datetime, he.getProcess().getId() };
            String sql = "UPDATE history set numericValue = ?, stringvalue = ?, type = ?, date = ?, processId = ? WHERE historyid =" + he.getId();
            logger.trace("added history event " + sql + ", " + Arrays.toString(param));
            run.update(connection, sql, param);
        } finally {
            if (connection != null) {
                MySQLHelper.closeConnection(connection);
            }
        }

    }

    public static void deleteHistoryEvent(HistoryEvent he) throws SQLException {
        Connection connection = null;
        try {
            connection = MySQLHelper.getInstance().getConnection();
            QueryRunner run = new QueryRunner();
            String sql = "DELETE from history  WHERE historyid =" + he.getId();
            run.update(connection, sql);
        } finally {
            if (connection != null) {
                MySQLHelper.closeConnection(connection);
            }
        }

    }

    public static int getNumberOfImages(int processId) throws SQLException {
        String sql = "SELECT * FROM history WHERE type = 3 and processID = " + processId + " order by date desc";
        Connection connection = null;
        int value = 0;
        try {
            connection = MySQLHelper.getInstance().getConnection();
            List<HistoryEvent> list = new QueryRunner().query(connection, sql, resultSetToHistoryListHandler);
            if (!list.isEmpty()) {
                HistoryEvent last = list.get(0);
                if (last.getNumericValue() < 0.0) {
                    value = last.getNumericValue().intValue() * -1;
                } else {
                    for (HistoryEvent he : list) {
                        value += he.getNumericValue().intValue();
                    }
                }
            }
        } finally {
            if (connection != null) {
                MySQLHelper.closeConnection(connection);
            }
        }
        return value;
    }

    public static ResultSetHandler<List<HistoryEvent>> resultSetToHistoryListHandler = new ResultSetHandler<List<HistoryEvent>>() {

        @Override
        public List<HistoryEvent> handle(ResultSet rs) throws SQLException {
            List<HistoryEvent> answer = new ArrayList<HistoryEvent>();
            try {
                while (rs.next()) {
                    int historyId = rs.getInt("historyid");
                    double numeric = rs.getDouble("numericvalue");
                    String stringvalue = rs.getString("stringvalue");
                    HistoryEventType type = HistoryEventType.getTypeFromValue(rs.getInt("type"));
                    Timestamp time = rs.getTimestamp("date");
                    HistoryEvent he = new HistoryEvent();
                    he.setDate(time == null ? null : new Date(time.getTime()));
                    he.setHistoryType(type);
                    he.setId(historyId);
                    he.setNumericValue(numeric);
                    he.setStringValue(stringvalue);

                    answer.add(he);

                }

            } finally {
                if (rs != null) {
                    rs.close();
                }
            }
            return answer;
        }
    };
}
