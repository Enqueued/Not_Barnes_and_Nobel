package Gate_Pack;

import Controller_Pack.MasterController;
import Model_Pack.*;
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
import java.sql.Statement;
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
    Statement stmt = null;
    //these result sets will be created just encase at first
    // may need more later on though
    ResultSet rs = null;
    private List<Library> listView = new ArrayList<Library>();
    private List<LibraryBook> listViewBook = new ArrayList<LibraryBook>();
	private LibraryBook booky;

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
            stmt = conn.createStatement();
            //need to think about what we are going to pull first
            rs = stmt.executeQuery("select * from library "+
										"right join library_book on library.id=library_id "+
                                        "left join book on book_id=book.id "+
                                        "left join AuthorTable on author_id=AuthorTable.id "+
                                        "order by library.id");
            //todo: fill in all necessary items
            while(rs.next()){
                Author it = new Author(rs.getString("AuthorTable.first_name"),rs.getString("last_name"),
                        rs.getString("gender"),rs.getString("web_site"),rs.getDate("dob"),
                        rs.getInt("id"), rs.getTimestamp("last_modified").toLocalDateTime());
				logger.info("in the while loop~! " + it.toString() );
                Book book = new Book (rs.getInt("id"), rs.getString("title"),
                        rs.getString("publisher"), rs.getDate("date_published").toString(),
                        rs.getString("summary"), it, rs.getTimestamp("last_modified").toLocalDateTime());
				logger.info("New Book: "+book.toString());
                booky = new LibraryBook (rs.getInt("quantity"), book);
				logger.info(booky.toString() );
                listViewBook.add(booky);
                Library library = new Library(rs.getInt("library_id"),
						rs.getString("library_name"), listViewBook, rs.getTimestamp("last_modified").toLocalDateTime());
                logger.info(library.toString());
                listView.add(library);
                listViewBook = new ArrayList<LibraryBook>();
            }
            conn.commit();
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
        return listView;
    }

	public void deleteLibrary(Library library) throws SQLException {
		conn = ds.getConnection();

		try {
			conn.setAutoCommit(false);
			PreparedStatement ps = conn.prepareStatement("DELETE from `autdit_trail` WHERE record_id = ? AND record_type = 'L'");
			ps.setInt(1, library.getId());
			ps.executeUpdate();

			ps.close();

			ps = conn.prepareStatement("DELETE from `library_book` WHERE library_id = ?");
			ps.setInt(1, library.getId());
			ps.executeUpdate();
			ps.close();

			ps = conn.prepareStatement("DELETE from `library` WHERE id = ?");
			ps.setInt(1, library.getId());
			ps.executeUpdate();
			ps.close();

			conn.commit();

		} catch(SQLException e) {
			logger.error("Failed to Delete entry in database: \n" +e.getMessage());
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

	public void insertLibrary(Library library) throws SQLException {
		conn = ds.getConnection();
		ResultSet rs = null;

		try {
			conn.setAutoCommit(false);
			PreparedStatement ps = conn.prepareStatement("INSERT INTO `library`( `library_name`) VALUES (?)", PreparedStatement.RETURN_GENERATED_KEYS);
			ps.setString(1,library.getLibraryName());
			ps.executeUpdate();
			rs = ps.getGeneratedKeys();

			if(rs != null && rs.next()) {
				library.setId(rs.getInt(1));
			}
			ps.close();

			ps = conn.prepareStatement("insert into `autdit_trail` (record_type, record_id, entry_msg) values ('L', ?, ?)");
			ps.setInt(1, library.getId());
			ps.setString(2, "Added " + library.toString());
			ps.executeUpdate();
			logger.info("start");
			if(library.getBooks() != null){
				List<LibraryBook> librarybooks = library.getBooks();
				for(LibraryBook b : librarybooks){
					Book books = b.getBook();
					ps = conn.prepareStatement("insert into `library_book` (library_id, book_id, quantity) values (?, ?, ?)");
					ps.setInt(1, library.getId());
					ps.setInt(2, books.getId());
					ps.setInt(3, b.getQuantity());
					ps.executeUpdate();
					ps.close();
					ps = conn.prepareStatement("insert into `autdit_trail` (record_type, record_id, entry_msg) values ('L', ?, ?)");
					ps.setInt(1, library.getId());
					ps.setString(2, "Added Book" + books.toString());
					ps.executeUpdate();
				}
			}

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

	public void updateLibrary(Library library, Library oldlibrary) throws ParseException, SQLException {
		conn = ds.getConnection();
		PreparedStatement ps;
		try {
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT `last_modified` FROM library WHERE `id` = " + library.getId());
			rs.next();
			if(!library.getLastModified().toString().equals(rs.getTimestamp("last_modified").toLocalDateTime().toString())){
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Update Error");
				alert.setContentText("Library not up to date. please try again.");
				alert.showAndWait();
				if(rs != null)
					rs.close();
				if(conn != null) {
					conn.setAutoCommit(true);
					conn.close();
				}
				MasterController.getInstance().changeView(authorstuff.ViewType.Library_List_View, library);

			}
			ps = conn.prepareStatement("UPDATE library SET library_name = ? WHERE id = ?");
			ps.setString(1,library.getLibraryName());
			ps.setInt(2, library.getId());
			ps.executeUpdate();
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT `last_modified` FROM library WHERE `id` = " + library.getId());
			rs.next();
			library.setLastModified(rs.getTimestamp("last_modified").toLocalDateTime());
			ps.close();
			if(!library.getLibraryName().equals(oldlibrary.getLibraryName())){
				ps = conn.prepareStatement("insert into `autdit_trail` (record_type, record_id, entry_msg) values ('L', ?, ?)");
				ps.setInt(1, library.getId());
				ps.setString(2, "Library name changed from " + oldlibrary.getLibraryName() + " to " + library.getLibraryName());
				ps.executeUpdate();
			}
			List<LibraryBook> libraryBook = library.getBooks();
			stmt = conn.createStatement();
			rsb = stmt.executeQuery("SELECT * FROM library_book WHERE library_id = "+library.getId());
			for(LibraryBook books : libraryBook){
				int flag = 0;
				while(rsb.next()) {
					Book boook = books.getBook();
					if(boook.getId() == rsb.getInt("book_id")){
						flag = 1;
						if(books.getQuantity() != rsb.getInt("quantity")){
							ps = conn.prepareStatement("UPDATE library_book SET quantity = ? WHERE library_id = ? AND book_id = ?");
							ps.setInt(1,books.getQuantity());
							ps.setInt(2, library.getId());
							ps.setInt(3, boook.getId());
							ps.executeUpdate();

							ps = conn.prepareStatement("insert into `autdit_trail` (record_type, record_id, entry_msg) values ('L', ?, ?)");
							ps.setInt(1, library.getId());
							ps.setString(2, "Book quantity changed from " + rsb.getInt("quantity") + " to " + books.getQuantity());
							ps.executeUpdate();
						}
						rsb.first();
						break;
					}

				}
				if(flag == 0){
					ps = conn.prepareStatement("insert into `library_book` (library_id, book_id, quantity) values (?, ?, ?)");
					ps.setInt(1, library.getId());
					ps.setInt(2,books.getBook().getId() );
					ps.setInt(3, books.getQuantity());
					ps.executeUpdate();
					ps = conn.prepareStatement("insert into `autdit_trail` (record_type, record_id, entry_msg) values ('L', ?, ?)");
					ps.setInt(1, library.getId());
					ps.setString(2, "Added new book: " + books.getBook());
					ps.executeUpdate();
					rsb.first();
				}
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
	}

	public List<auditTrailEntry> auditTrail(Library library) throws SQLException {
		List<auditTrailEntry> list = new ArrayList<auditTrailEntry>();
		conn = ds.getConnection();

		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT * FROM `autdit_trail` WHERE `record_id` = "+library.getId()+" AND `record_type` = 'L' ORDER BY `date_added` ASC");
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

	public void close() throws SQLException {
		if(stmt != null)
			stmt.close();
		if(conn != null) {
			conn.close();
			logger.info("closed");
		}
	}


	public List<LibraryBook> getLibraryBooks(int id) throws SQLException {
		conn = ds.getConnection();
		List<LibraryBook> libraryBooks = new ArrayList<LibraryBook>();

		try {
			conn.setAutoCommit(false);

			stmt = conn.createStatement();
			rs = stmt.executeQuery("select * from library_book "+
					"left join book on book_id=book.id "+
					"left join AuthorTable on author_id=AuthorTable.id "+
					"order by book_id");
			while(rs.next()){
				libraryBooks.add(new LibraryBook((new Book(rs.getInt("id"), rs.getString("title"),rs.getString("publisher"),
				rs.getDate("date_published").toString(),rs.getString("summary"),new Author(rs.getString("first_name"),rs.getString("last_name"),
				rs.getString("gender"),rs.getString("web_site"),rs.getDate("dob"),rs.getInt("id"), rs.getTimestamp("last_modified").toLocalDateTime()), rs.getTimestamp("last_modified").toLocalDateTime())),rsa.getInt("quantity"),true));
			}
			conn.commit();
		} catch(SQLException e) {
			conn.rollback();
			logger.error("Failed reading database" + e);

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
		return listViewBook;

	}


	public void deleteLibraryBook(Library library, LibraryBook selectedItem) throws SQLException {
		// TODO Auto-generated method stub
		conn = ds.getConnection();

		try {
			conn.setAutoCommit(false);
			PreparedStatement ps = conn.prepareStatement("DELETE from `library_book` WHERE library_id = ? AND book_id = ?");
			ps.setInt(2, selectedItem.getBook().getId());
			ps.setInt(1, library.getId());
			ps.executeUpdate();
			ps.close();

			conn.commit();

		} catch(SQLException e) {
			logger.error("Failed to Delete entry in database: \n" +e.getMessage());
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
}

