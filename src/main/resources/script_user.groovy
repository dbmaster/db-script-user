/*
 *  File Version:  $Id: report_script_user.groovy 145 2013-05-22 18:10:44Z schristin $
 */

// TODO Handle exception when connection or user are not available


import com.branegy.service.connection.api.ConnectionService
import com.branegy.dbmaster.connection.ConnectionProvider
import io.dbmaster.tools.db_script_user.ScriptUser

connectionSrv = dbm.getService(ConnectionService.class);

connectionInfo = connectionSrv.findByName(p_server);
connector = ConnectionProvider.getConnector(connectionInfo);

def conn = connector.getJdbcConnection(null);
output2 = ScriptUser.scriptUser(conn, p_login)

conn.close();

println "<pre>"+output2+"</pre>"
