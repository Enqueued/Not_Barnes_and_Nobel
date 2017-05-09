package Gate_Pack;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import redis.clients.jedis.Jedis;
import Model_Pack.Author;
import Model_Pack.ViewType;
import Model_Pack.auditTrailEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import Controller_Pack.MasterController;

public class AuthorTableGateway {
	private static Logger logger = LogManager.getLogger();
	private MysqlDataSource ds = null;
	Jedis jedis;
	Statement stmt = null;
	ResultSet rs = null;
	Connection conn = null;
	List<Author> listView = new ArrayList<Author>();

	/**
	 * Setting of the connection
	 * @throws SQLException
	 */
	public AuthorTableGateway() throws SQLException{
		Properties props = new Properties();
//		jedis = new Jedis("easel2.fulgentcorp.com");
//		jedis.auth("pd6BvDKAEMXhxwUg");
//		jedis.select(0);
		FileInputStream file = null;
		try{
			file = new FileInputStream("./src/db.properties");
			props.load(file);
			file.close();
			jedis = new Jedis(props.getProperty("REDIS_URL"));
            jedis.auth(props.getProperty("REDIS_AUTH"));
            jedis.select(0);
			this.ds = new MysqlDataSource();
			ds.setURL(props.getProperty("MYSQL_AUTHOR_DB_URL"));
			ds.setUser(props.getProperty("MYSQL_AUTHOR_DB_USERNAME"));
			ds.setPassword(props.getProperty("MYSQL_AUTHOR_DB_PASSWORD"));
		}catch (Exception e){
			logger.error(e);
		}
	}

	/**
	 * setting up the author lists
	 * @return
	 * @throws SQLException
	 */
	public List<Author> getAuthors() throws SQLException{
		conn = ds.getConnection();

		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT * FROM AuthorTable");
			while(rs.next()) {
				//fetch the next record into rs
				listView.add(new Author(rs.getString("first_name"),rs.getString("last_name"),
						rs.getString("gender"),rs.getString("web_site"),rs.getDate("dob"),rs.getInt("id"), rs.getTimestamp("last_modified").toLocalDateTime()));
			}
		} catch(SQLException e) {
			logger.error("Failed reading database" + e);

			//handle the exception 
		} finally {
			//be sure to close the objects
			if(rs != null)
				rs.close();
			if(stmt != null)
				stmt.close();
			if(conn != null) {
				conn.close();
			}
		}
		return listView;

	}

	/**
	 * This method is used to set up and configure the authors
	 * it will check what was previously on the list and the updated (new) author
	 * will push if everything is good
	 * @param author
	 * @param oldAuthor
	 * @throws SQLException
	 * @throws ParseException
	 */
	public void updateAuthor(Author author, Author oldAuthor) throws SQLException, ParseException {
		conn = ds.getConnection();
		PreparedStatement ps;
		try {
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT `last_modified` FROM AuthorTable WHERE `id` = " + author.getId());
			rs.next();
			if(!author.getLastModified().toString().equals(rs.getTimestamp("last_modified").toLocalDateTime().toString())){
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Update Error");
				alert.setContentText("Author not up to date. please try again.");
				alert.showAndWait();
				if(rs != null)
					rs.close();
				if(conn != null) {
					conn.setAutoCommit(true);
					conn.close();
				}
				MasterController.getInstance().changeView(ViewType.AUTHOR_LIST, author);

			}
			ps = conn.prepareStatement("UPDATE AuthorTable SET first_name = ?, last_name = ?, dob = ?, gender = ?, web_site = ? WHERE id = ?");
			ps.setString(1,author.getFirstName());
			ps.setString(2,author.getLastName());
			ps.setDate(3,java.sql.Date.valueOf(author.getDob()));
			ps.setString(4,author.getGender());
			ps.setString(5, author.getWeb());
			ps.setInt(6, author.getId());
			ps.executeUpdate();
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT `last_modified` FROM AuthorTable WHERE `id` = " + author.getId());
			rs.next();
			author.setLastModified(rs.getTimestamp("last_modified").toLocalDateTime());
			ps.close();
			if(author.equals(oldAuthor)){
				ps = conn.prepareStatement("insert into `audit_trail` (record_type, record_id, entry_msg) values ('A', ?, ?)");
				ps.setInt(1, author.getId());
				ps.setString(2, "Updated");
				ps.executeUpdate();
			}
			if(!author.getFirstName().equals(oldAuthor.getFirstName())){
				ps = conn.prepareStatement("insert into `audit_trail` (record_type, record_id, entry_msg) values ('A', ?, ?)");
				ps.setInt(1, author.getId());
				ps.setString(2, "First Name changed from " + oldAuthor.getFirstName() + " to " + author.getFirstName());
				ps.executeUpdate();
			}
			if(!author.getLastName().equals(oldAuthor.getLastName())){
				ps = conn.prepareStatement("insert into `audit_trail` (record_type, record_id, entry_msg) values ('A', ?, ?)");
				ps.setInt(1, author.getId());
				ps.setString(2, "Last Name changed from " + oldAuthor.getLastName() + " to " + author.getLastName());
				ps.executeUpdate();
			}
			if(!author.getGender().equals(oldAuthor.getGender())){
				ps = conn.prepareStatement("insert into `audit_trail` (record_type, record_id, entry_msg) values ('A', ?, ?)");
				ps.setInt(1, author.getId());
				ps.setString(2, "Gender changed from " + oldAuthor.getGender() + " to " + author.getGender());
				ps.executeUpdate();
			}
			if(!author.getWeb().equals(oldAuthor.getWeb())){
				ps = conn.prepareStatement("insert into `audit_trail` (record_type, record_id, entry_msg) values ('A', ?, ?)");
				ps.setInt(1, author.getId());
				ps.setString(2, "Web address changed from " + oldAuthor.getWeb() + " to " + author.getWeb());
				ps.executeUpdate();
			}
			if(!author.getDob().equals(oldAuthor.getDob())){
				ps = conn.prepareStatement("insert into `audit_trail` (record_type, record_id, entry_msg) values ('A', ?, ?)");
				ps.setInt(1, author.getId());
				ps.setString(2, "DOB changed from " + oldAuthor.getDob() + " to " + author.getDob());
				ps.executeUpdate();
			}
			conn.commit();
			jedis.publish("Books", "modified");
		} catch(SQLException e) {
			logger.error("Failed updating database" + e);
			conn.rollback();

			//handle the exception 
		} finally {
			//be sure to close the objects 
			if(rs != null)
				rs.close();
			if(stmt != null)
				stmt.close();
			if(conn != null) {
				conn.setAutoCommit(true);
				conn.close();
			}
		}
	}

	/**
	 * inserts an author into the sqldb
	 * @param author
	 * @throws SQLException
	 * @throws ParseException
	 */
	public void insertAuthor(Author author) throws SQLException, ParseException{

		conn = ds.getConnection();
		ResultSet rs = null;

		try {
			conn.setAutoCommit(false);
			PreparedStatement ps = conn.prepareStatement("INSERT INTO `AuthorTable`( `first_name`, `last_name`, `dob`, `gender`, `web_site`) VALUES (?,?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
			ps.setString(1,author.getFirstName());
			ps.setString(2,author.getLastName());
			ps.setDate(3,java.sql.Date.valueOf(author.getDob()));
			ps.setString(4,author.getGender());
			ps.setString(5, author.getWeb());
			ps.executeUpdate();
			rs = ps.getGeneratedKeys();

			if(rs != null && rs.next()) {
				author.setId(rs.getInt(1));
			}
			ps.close();

			ps = conn.prepareStatement("insert into `audit_trail` (record_type, record_id, entry_msg) values ('A', ?, ?)");
			ps.setInt(1, author.getId());
			ps.setString(2, "Added " + author.toString());
			ps.executeUpdate();
			conn.commit();
			jedis.publish("Books", "Modified");
		} catch(SQLException e) {
			logger.error("Failed to insert new entry in database: \n" +e.getMessage());
			conn.rollback();
			//handle the exception 
		} finally {
			//be sure to close the objects 
			if(rs != null)
				rs.close();
			if(stmt != null)
				stmt.close();
			if(conn != null) {
				conn.setAutoCommit(true);
				conn.close();
			}
		}
	}

	/**
	 * deletes an author from the sqldb
	 * @param author
	 * @throws SQLException
	 * @throws ParseException
	 */
	public void deleteAuthor(Author author) throws SQLException, ParseException{

		conn = ds.getConnection();

		try {
			conn.setAutoCommit(false);
			PreparedStatement ps;
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT * from book WHERE author_id = " + author.getId());
			while(rs.next()) {
				ps = conn.prepareStatement("DELETE from `audit_trail` WHERE record_id = ?");
				ps.setInt(1, rs.getInt("id"));
				ps.executeUpdate();

				ps.close();
			}

			ps = conn.prepareStatement("DELETE from book WHERE author_id = ?");
			ps.setInt(1, author.getId());
			ps.executeUpdate();

			ps.close();

			ps = conn.prepareStatement("DELETE from `audit_trail` WHERE record_id = ? AND record_type = 'A'");
			ps.setInt(1, author.getId());
			ps.executeUpdate();

			ps.close();

			ps = conn.prepareStatement("DELETE from `AuthorTable` WHERE id = ?");
			ps.setInt(1, author.getId());
			ps.executeUpdate();

			conn.commit();
			jedis.publish("Books", "modified");
		} catch(SQLException e) {
			logger.error("Failed to Delete entry in database: \n" +e.getMessage());
			conn.rollback();
			//handle the exception 
		} finally {
			//be sure to close the objects 
			if(rs != null)
				rs.close();
			if(stmt != null)
				stmt.close();
			if(conn != null) {
				conn.setAutoCommit(true);
				conn.close();
			}
		}

	}

	/**
	 * closing the sql stuffs
	 * @throws SQLException
	 */
	public void close() throws SQLException {
		if(rs != null)
			rs.close();
		if(stmt != null)
			stmt.close();
		if(conn != null) {
			conn.close();
			logger.info("closed");
		}
	}

	/**
	 * creates the audit trail for the authors
	 * @param author
	 * @return
	 * @throws SQLException
	 */
	public List<auditTrailEntry> auditTrail(Author author) throws SQLException {
		List<auditTrailEntry> list = new ArrayList<auditTrailEntry>();
		conn = ds.getConnection();

		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT * FROM `audit_trail` WHERE `record_id` = "+author.getId()+" AND `record_type` = 'A' ORDER BY `date_added` ASC");
			while(rs.next()) {
				//fetch the next record into rs
				list.add(new auditTrailEntry(rs.getString("record_type"),rs.getTimestamp("date_added"),
						rs.getString("entry_msg")));
			}
		} catch(SQLException e) {
			logger.error("Failed reading database" + e);

			//handle the exception 
		} finally {
			//be sure to close the objects 
			if(rs != null)
				rs.close();
			if(stmt != null)
				stmt.close();
			if(conn != null) {
				conn.close();
			}
		}
		return list;
	}
}
