package Gate_Pack;

import Model_Pack.Library;
import com.mysql.jdbc.Statement;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

/**
 * Created by ultimaq on 4/1/17.
 * this should be the gateway for the sql database data will be set up similar to the original two gateways with minute
 * differences
 */
public class LibraryTableGateway {
    private static Logger logger = LogManager.getLogger();
    private MysqlDataSource ds = null;
    Connection conn = null;

    Statement stmt = null;
    //these result sets will be created just encase at first
    // may need more later on though
    ResultSet rs = null;
    ResultSet rs2 = null;
    ResultSet rs3 = null;

    public LibraryTableGateway() throws SQLException{
        //import sql properties
        Properties props = new Properties();
        FileInputStream file = null;
        try {
            file = new FileInputStream("./src/db.properties");
            props.load(file);
            file.close();
            this.ds = new MysqlDataSource();
            ds.setURL(props.getProperty("MYSQL_AUTHOR_DB_URL"));
            ds.setUser(props.getProperty("MYSQL_AUTHOR_DB_USERNAME"));
            ds.setPassword(props.getProperty("MYSQL_AUTHOR_DB_PASSWORD"));
        }catch (Exception e){
            logger.error(e);
        }
    }

    /**
     * This is where we are going to be pulling a lot of the content from.
     * we need to be able to pull our books and their libraries, note that all books
     * are connected to a library but a library may have 0 books of that name!
     * (also we will be using parameterized queries)
     * @return
     * @throws SQLException
     */
    public List<Library> getLibraries() throws SQLException{
        conn = ds.getConnection(); //connection to sql db
        try{
            conn.setAutoCommit(false);
            stmt = conn.createStatment();
            //need to think about what we are going to pull first
            rs2 = stmt.executeQuery("SELECT * FROM `AuthorTable` left join book on `AuthorTable`.id=author_id");




        }
    }

}
