/*
 *  File Version:  $Id: ScriptUser.groovy 145 2013-05-22 18:10:44Z schristin $
 */

package io.dbmaster.tools.db_script_user

import groovy.sql.Sql

// TODO How to add parameters to query in groovy ?

public class ScriptUser {

    def static String scriptUser(connection, user) {

       def query = """SELECT p.sid, p.name, p.type, p.is_disabled, l.hasaccess, l.denylogin, p.default_database_name,
                             pwd = CAST( LOGINPROPERTY( p.name, 'PasswordHash' ) AS varbinary (256) ),
                             is_policy_checked = CASE is_policy_checked WHEN 1 THEN 'ON' WHEN 0 THEN 'OFF' ELSE NULL END,
                             is_expiration_checked = CASE is_expiration_checked WHEN 1 THEN 'ON' WHEN 0 THEN 'OFF' ELSE NULL END
                      FROM sys.server_principals p
                      LEFT JOIN sys.syslogins l ON l.name = p.name
                      LEFT JOIN sys.sql_logins sl on l.name = sl.name
                      WHERE p.type IN ( 'S', 'G', 'U' )"""

       if (user!=null) {
         query+=" AND p.name = '${user}'"
       }

       Sql sql = new Sql(connection);
       def output = ""

       sql.eachRow(query) { row ->
           if (row.type.matches("G|U")) { // -- NT authenticated account/group
              output += "\n\n CREATE LOGIN [${row.name}] FROM WINDOWS"
           } else {

               def pwd_string = hexadecimal(row.pwd)
               def sid_string = hexadecimal(row.sid)


              output += "\n\n CREATE LOGIN [${row.name}]\n   WITH PASSWORD = ${pwd_string} HASHED,\n   SID = ${sid_string}"

              if ( row.is_policy_checked!=null)
                output += ", CHECK_POLICY = ${row.is_policy_checked}"

              if ( row.is_expiration_checked!=null)
                output += ", CHECK_EXPIRATION = ${row.is_expiration_checked}"

              if (row.denylogin == 1) // login is denied access
                output +="; DENY CONNECT SQL TO [ ${row.name} ]"

              if (row.hasaccess == 0) // login exists but does not have access
                 output +="; REVOKE CONNECT SQL TO [ ${row.name} ]"

              if (row.is_disabled == 1) // login is disabled
                 output +="; ALTER LOGIN [ ${row.name} ] DISABLE"


              // sp_defaultdb [@loginame =] 'login' , [@defdb =] 'database'

           }
          output +=";\n ALTER LOGIN [${row.name}] WITH DEFAULT_DATABASE = [${row.default_database_name}]"
       }
       return output
    }

    def static String hexadecimal(binvalue) {
        def hexstring = '0123456789ABCDEF'
        def charvalue = '0x'
        binvalue.each {
           def xx = it < 0 ? (256 + it) : it
           charvalue+= hexstring[((int)xx/16)]+hexstring[xx%16]
        }
        return charvalue
    }

   def void runReport() {

   }

}
