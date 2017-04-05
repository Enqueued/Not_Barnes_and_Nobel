package Gate_Pack;

import Controller_Pack.MasterController;
import Model_Pack.*;
import com.mysql.jdbc.Statement;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.ParseException;
import javafx.scene.control.Alert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by ultimaq on 4/1/17.
 * this should be the gateway for the sql database data will be set up similar to the original two gateways with minute
 * differences
 */
public class LibraryTableGateway {
    private static Logger logger = LogManager.getLogger();
    private MysqlDataSource ds = null;
    Connection conn = null;
    java.sql.Statement stmt = null;
    //these result sets will be created just encase at first
    // may need more later on though
    ResultSet rs = null;
    List<Library> listView = new ArrayList<Library>();

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
        LibraryBook booky;
        List<LibraryBook> books = new ArrayList<LibraryBook>();
        conn = ds.getConnection(); //connection to sql db
        try{
            conn.setAutoCommit(false);
            stmt = conn.createStatement();
            //need to think about what we are going to pull first
            rs = stmt.executeQuery("select * from library "+
										"right join library_book on library.id=library_id "+
                                        "left join book on book_id=book.id "+
                                        "left join AuthorTable on author_id=AuthorTable.id "+
                                        "order by library.id");
            //todo: fill in all necessary items
            while(rs.next()){
                Author it = new Author(rs.getString("first_name"),rs.getString("last_name"),
                        rs.getString("gender"),rs.getString("web_site"),rs.getDate("dob"),
                        rs.getInt("id"), rs.getTimestamp("last_modified").toLocalDateTime());
                Book book = new Book (rs.getInt("id"), rs.getString("title"),
                        rs.getString("publisher"), rs.getDate("date_published").toString(),
                        rs.getString("summary"), it, rs.getTimestamp("last_modified").toLocalDateTime());
                booky = new LibraryBook (rs.getInt("library_book.id"), book);

                books.add(booky);
                Library library = new Library(rs.getInt("library.id"),
						rs.getString("library.library_name"), books, rs.getTimestamp("last_modified").toLocalDateTime());
            }
        }catch (Exception e){
            logger.error("Failed to register Join" + e);
        } finally {
            if(rs != null)
                rs.close();
            if(stmt != null)
                stmt.close();
            if(conn != null) {
                conn.close();
            }
        }
        return null;
    }

    public void updateLibName(Library lib)throws ParseException, SQLException{
		conn = ds.getConnection();
		PreparedStatement ps;
		try {
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			ps = conn.prepareStatement("UPDATE library SET library_name = ? WHERE id = ?");
			ps.setString(1, lib.getLibraryName());
			ps.setInt(2, lib.getId());
			ps.executeUpdate();
			conn.commit();
		} catch(SQLException e) {
			logger.error("Failed updating database" + e);
			conn.rollback();

			//handle the exception
		} finally {
			//be sure to close the objects
			if(stmt != null)
				stmt.close();
			if(conn != null) {
				conn.setAutoCommit(true);
				conn.close();
			}
		}

	}

	public void updateLibraryDB(Library Lib, Library oldLib)throws ParseException, SQLException {
		if(Lib.getId() > 0){
			conn = ds.getConnection();
			PreparedStatement ps;
			try {
				conn.setAutoCommit(false);
				stmt = conn.createStatement();
				rs = stmt.executeQuery("SELECT `last_modified` FROM library WHERE `id` = " + Lib.getId());
				rs.next();
				if(!Lib.getLastModified().toString().equals(rs.getTimestamp("last_modified").toLocalDateTime().toString())){
					Alert alert = new Alert(Alert.AlertType.WARNING);
					alert.setTitle("Update Error");
					alert.setContentText("Library not up to date. please try again.");
					alert.showAndWait();
					if(rs != null)
						rs.close();
					if(conn != null) {
						conn.setAutoCommit(true);
						conn.close();
					}
					try {
						MasterController.getInstance().changeView(ViewType.LIBRARY_DETAIL, Lib);
					} catch (java.text.ParseException e) {
						e.printStackTrace();
					}
				}
				if(!(valLibcheck(Lib.getId(),Lib.getLibraryName()))){
					if(rs != null)
						rs.close();
					if(conn != null) {
						conn.setAutoCommit(true);
						conn.close();
					}
					try {
						MasterController.getInstance().changeView(ViewType.LIBRARY_DETAIL, Lib);
					} catch (java.text.ParseException e) {
						e.printStackTrace();
					}
				}
				ps = conn.prepareStatement("UPDATE library SET library_name = ? WHERE id = ?");
				ps.setString(1, Lib.getLibraryName());
				ps.setInt(2, Lib.getId());
				ps.executeUpdate();
				stmt = conn.createStatement();
				rs = stmt.executeQuery("SELECT `last_modified` FROM library WHERE `id` = " + Lib.getId());
				rs.next();
				Lib.setLastModified(rs.getTimestamp("last_modified").toLocalDateTime());
				ps.close();
				if(Lib.equals(oldLib)){
					ps = conn.prepareStatement("insert into `audit_trail` (record_type, record_id, entry_msg) values ('L', ?, ?)");
					ps.setInt(1, Lib.getId());
					ps.setString(2, "Library Name changed from " + oldLib.getLibraryName() + " to " + Lib.getLibraryName());
					ps.executeUpdate();
				}
				conn.commit();
			} catch(SQLException e) {
				logger.error("Failed updating database" + e);
				conn.rollback();

				//handle the exception
			} finally {
				//be sure to close the objects
				if(stmt != null)
					stmt.close();
				if(conn != null) {
					conn.setAutoCommit(true);
					conn.close();
				}
			}
			if(Lib.getBooks() != oldLib.getBooks()){
				//logger.debug("list of books has changed");
				for(LibraryBook l1: oldLib.getBooks()){
					for(LibraryBook l2: Lib.getBooks()){
						if(l1.getBook() == l2.getBook()){
							if(l1.getQuantity() != l2.getQuantity()){
								//logger.debug("number of " + l1.getBook() + "has changed");
								updateLibraryBook(l2, Lib.getId(), l1.getQuantity() - l2.getQuantity());
							}
						}
						if(!(oldLib.getBooks().contains(l2))){
							//logger.debug("old Library doesn't contain LibraryBook");
							newLibraryBook(l2, Lib.getId());
							break;
						}
					}
				}

			}
		}else{
			addLibrary(Lib);
		}

	}

	public void updateLibraryBook(LibraryBook lb, int libID, int newQuan)throws ParseException, SQLException {
		conn = ds.getConnection();
		PreparedStatement ps;
		try{
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("UPDATE library_book SET quantity = ? WHERE library_id = ? AND book_id = ?");
			ps.setInt(2, libID);
			ps.setInt(3, lb.getBook().getId());
			ps.setInt(1, newQuan);
			logger.debug(ps);
			ps.executeUpdate();
			ps.close();
			ps = conn.prepareStatement("insert into `audit_trail` (record_type, record_id, entry_msg) values ('L', ?, ?)");
		    ps.setInt(1, libID);
		    ps.setString(2, lb.getBook() + " quantity changed from <" + lb.getQuantity() + "> to <" + newQuan  + ">");
		    ps.executeUpdate();
			conn.commit();
		} catch(SQLException e) {
			logger.error("Failed updating database" + e);
			conn.rollback();

			//handle the exception
		} finally {
			//be sure to close the objects
			if(stmt != null)
				stmt.close();
			if(conn != null) {
				conn.setAutoCommit(true);
				conn.close();
			}
		}
	}

	public void newLibraryBook(LibraryBook lb, int libID)throws ParseException, SQLException {
		conn = ds.getConnection();
		PreparedStatement ps;
		try{
			conn.setAutoCommit(false);
			ps = conn.prepareStatement("INSERT INTO `library_book` (`library_id` , `book_id` , `quantity`) VALUES (?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
			ps.setInt(1, libID);
			ps.setInt(2, lb.getBook().getId());
			ps.setInt(3, lb.getQuantity());
			ps.executeUpdate();
			ps = conn.prepareStatement("insert into `audit_trail` (record_type, record_id, entry_msg) values ('L', ?, ?)");
		    ps.setInt(1, libID);
		    ps.setString(2, lb.getBook() + " added");
		    ps.executeUpdate();
			ps.close();
			conn.commit();
		} catch(SQLException e) {
			logger.error("Failed updating database" + e);
			conn.rollback();

			//handle the exception
		} finally {
			//be sure to close the objects
			if(stmt != null)
				stmt.close();
			if(conn != null) {
				conn.setAutoCommit(true);
				conn.close();
			}
		}
	}

	public void addLibrary(Library lb)throws ParseException, SQLException {
		logger.debug("add library was called");
		conn = ds.getConnection();
		ResultSet rs = null;

		try {
			conn.setAutoCommit(false);
		    PreparedStatement ps = conn.prepareStatement("INSERT INTO `library`( `library_name` ) VALUES (?)", PreparedStatement.RETURN_GENERATED_KEYS);
		    ps.setString(1,lb.getLibraryName());
		    ps.executeUpdate();
		    rs = ps.getGeneratedKeys();

		    if(rs != null && rs.next()) {
		    	lb.setId(rs.getInt(1));
			}
		    ps.close();

		    ps = conn.prepareStatement("insert into `audit_trail` (record_type, record_id, entry_msg) values ('A', ?, ?)");
		    ps.setInt(1, lb.getId());
		    ps.setString(2, "Added " + lb.getLibraryName());
		    ps.executeUpdate();
		    conn.commit();
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

	public void deleteLibraryBook(LibraryBook lb, int libID) throws SQLException{
		conn = ds.getConnection();

		try {
			conn.setAutoCommit(false);
			PreparedStatement ps = conn.prepareStatement("DELETE from `audit_trail` WHERE record_id = ? AND record_type = 'L'");
			ps.setInt(1, lb.getBook().getId());
			ps.executeUpdate();

			ps.close();

			ps = conn.prepareStatement("DELETE from `library_book` WHERE book_id = ? AND library_id = ?");
			ps.setInt(1, lb.getBook().getId());
			ps.setInt(2, libID);
			ps.executeUpdate();
			ps.close();

			conn.commit();

		} catch(SQLException e) {
			logger.error("Failed to Delete entry in database: \n" +e.getMessage());
			conn.rollback();
		} finally {
			if(stmt != null)
				stmt.close();
			if(conn != null) {
				conn.setAutoCommit(true);
				conn.close();
			}
		}
	}

	public void deleteLibrary(int libID) throws SQLException{
		conn = ds.getConnection();

		try {
			conn.setAutoCommit(false);
			PreparedStatement ps = conn.prepareStatement("DELETE from `audit_trail` WHERE record_id = ? AND record_type = 'L'");
			ps.setInt(1, libID);
			ps.executeUpdate();

			ps.close();

			ps = conn.prepareStatement("DELETE from `library` WHERE library_id = ?");
			ps.setInt(1, libID);
			ps.executeUpdate();
			ps.close();

			conn.commit();

		} catch(SQLException e) {
			logger.error("Failed to Delete entry in database: \n" +e.getMessage());
			conn.rollback();
		} finally {
			if(stmt != null)
				stmt.close();
			if(conn != null) {
				conn.setAutoCommit(true);
				conn.close();
			}
		}
	}

	public List<LibraryBook> difBooks(List<LibraryBook> lib, List<LibraryBook> libold){
		for(int i=0;i<lib.size();i++){
			if(lib.get(i) == libold.get(1)){
				lib.remove(i);
			}
		}
		return lib;
	}

	public boolean valLibcheck(int id, String name){
		boolean correct = true;
		if(id<0){
			correct = false;
		}
		if(name.length() > 100 || name.length() == 0){
			correct = false;
		}

		return correct;
	}

	public boolean valBookcheck(Book b){
		boolean correct = true;
		if(b.getId() < 0){
			correct = false;
		}
		if(b.getTitle().length() > 100 || b.getTitle().length() == 0){
			correct = false;
		}
		if(b.getPublisher().length() > 100 || b.getPublisher().length() == 0){
			correct = false;
		}
		if(b.getSummary().length() != 0){
			correct = false;
		}
		if(b.getAuthor().getId() <= 0){
			correct = false;
		}
		return correct;
	}

}
