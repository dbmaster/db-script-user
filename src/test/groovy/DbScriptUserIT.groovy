import io.dbmaster.testng.BaseToolTestNGCase;

import static org.testng.Assert.assertTrue;
import org.testng.annotations.Test

import com.branegy.tools.api.ExportType;


public class DbScriptUserIT extends BaseToolTestNGCase {

    @Test
    public void test() {
        def parameters = [ "p_server"  :  getTestProperty("db-script-user.p_server") ]
        tools.toolExecutor("db-script-user", parameters).execute()
    }
}
